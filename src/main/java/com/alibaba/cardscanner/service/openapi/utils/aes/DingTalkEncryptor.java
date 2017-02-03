package com.alibaba.cardscanner.service.openapi.utils.aes;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;


/**
 * ��������ƽ̨�ӽ��ܷ���
 * ��ORACLE�ٷ���վ����JCE������Ȩ�޲����ļ�
 *     JDK6�����ص�ַ��http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html
 *     JDK7�����ص�ַ�� http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
 */
public class DingTalkEncryptor {

    private static final Charset CHARSET = Charset.forName("utf-8");
    private static final Base64 base64  = new Base64();
    private byte[]         aesKey;
    private String         token;
    private String         corpId;
    /**ask getPaddingBytes key�̶�����**/
    private static final Integer AES_ENCODE_KEY_LENGTH = 43;
    /**��������ַ����ֽڳ���**/
    private static final Integer RANDOM_LENGTH = 16;

    /**
     * ���캯��
     * @param token             ��������ƽ̨�ϣ����������õ�token
     * @param encodingAesKey  ��������̨�ϣ����������õ�EncodingAESKey
     * @param corpId           ISV�������õ�ʱ��Ӧ�ô���Ӧ�׼���SUITE_KEY����ͨ��ҵ��Corpid
     * @throws DingTalkEncryptException ִ��ʧ�ܣ���鿴���쳣�Ĵ�����;���Ĵ�����Ϣ
     */
    public DingTalkEncryptor(String token, String encodingAesKey, String corpId) throws DingTalkEncryptException{
        if (null==encodingAesKey ||  encodingAesKey.length() != AES_ENCODE_KEY_LENGTH) {
            throw new DingTalkEncryptException(DingTalkEncryptException.AES_KEY_ILLEGAL);
        }
        this.token = token;
        this.corpId = corpId;
        aesKey = Base64.decodeBase64(encodingAesKey + "=");
    }

    /**
     * ���Ͷ�������ƽ̨ͬ������Ϣ�����,���ؼ���Map
     * @param plaintext     ���ݵ���Ϣ������
     * @param timeStamp      ʱ���
     * @param nonce           ����ַ���
     * @return
     * @throws DingTalkEncryptException
     */
    public Map<String,String> getEncryptedMap(String plaintext, Long timeStamp, String nonce) throws DingTalkEncryptException {
        if(null==plaintext){
            throw new DingTalkEncryptException(DingTalkEncryptException.ENCRYPTION_PLAINTEXT_ILLEGAL);
        }
        if(null==timeStamp){
            throw new DingTalkEncryptException(DingTalkEncryptException.ENCRYPTION_TIMESTAMP_ILLEGAL);
        }
        if(null==nonce){
            throw new DingTalkEncryptException(DingTalkEncryptException.ENCRYPTION_NONCE_ILLEGAL);
        }
        // ����
        String encrypt = encrypt(Utils.getRandomStr(RANDOM_LENGTH), plaintext);
        String signature = getSignature(token, String.valueOf(timeStamp), nonce, encrypt);
        Map<String,String> resultMap = new HashMap<String, String>();
        resultMap.put("msg_signature", signature);
        resultMap.put("encrypt", encrypt);
        resultMap.put("timeStamp", String.valueOf(timeStamp));
        resultMap.put("nonce", nonce);
        return  resultMap;
    }

    /**
     * ���Ľ���
     * @param msgSignature     ǩ����
     * @param timeStamp        ʱ���
     * @param nonce             �����
     * @param encryptMsg       ����
     * @return                  ���ܺ��ԭ��
     * @throws DingTalkEncryptException
     */
    public String getDecryptMsg(String msgSignature, String timeStamp, String nonce, String encryptMsg)throws DingTalkEncryptException {
        //У��ǩ��
        String signature = getSignature(token, timeStamp, nonce, encryptMsg);
        if (!signature.equals(msgSignature)) {
            throw new DingTalkEncryptException(DingTalkEncryptException.COMPUTE_SIGNATURE_ERROR);
        }
        // ����
        String result = decrypt(encryptMsg);
        return result;
    }


    /*
     * �����ļ���.
     * @param text ��Ҫ���ܵ�����
     * @return ���ܺ�base64������ַ���
     */
    private String encrypt(String random, String plaintext) throws DingTalkEncryptException {
        try {
            byte[] randomBytes = random.getBytes(CHARSET);
            byte[] plainTextBytes = plaintext.getBytes(CHARSET);
            byte[] lengthByte = Utils.int2Bytes(plainTextBytes.length);
            byte[] corpidBytes = corpId.getBytes(CHARSET);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            byteStream.write(randomBytes);
            byteStream.write(lengthByte);
            byteStream.write(plainTextBytes);
            byteStream.write(corpidBytes);
            byte[] padBytes = PKCS7Padding.getPaddingBytes(byteStream.size());
            byteStream.write(padBytes);
            byte[] unencrypted = byteStream.toByteArray();
            byteStream.close();
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(aesKey, 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            byte[] encrypted = cipher.doFinal(unencrypted);
            String result = base64.encodeToString(encrypted);
            return result;
        } catch (Exception e) {
            throw new DingTalkEncryptException(DingTalkEncryptException.COMPUTE_ENCRYPT_TEXT_ERROR);
        }
    }

    /*
     * �����Ľ��н���.
     * @param text ��Ҫ���ܵ�����
     * @return ���ܵõ�������
     */
    private String decrypt(String text) throws DingTalkEncryptException {
        byte[] originalArr;
        try {
            // ���ý���ģʽΪAES��CBCģʽ
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            // ʹ��BASE64�����Ľ��н���
            byte[] encrypted = Base64.decodeBase64(text);
            // ����
            originalArr = cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new DingTalkEncryptException(DingTalkEncryptException.COMPUTE_DECRYPT_TEXT_ERROR);
        }

        String plainText;
        String fromCorpid;
        try {
            // ȥ����λ�ַ�
            byte[] bytes = PKCS7Padding.removePaddingBytes(originalArr);
            // ����16λ����ַ���,�����ֽ����corpId
            byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);
            int plainTextLegth = Utils.bytes2int(networkOrder);
            plainText = new String(Arrays.copyOfRange(bytes, 20, 20 + plainTextLegth), CHARSET);
            fromCorpid = new String(Arrays.copyOfRange(bytes, 20 + plainTextLegth, bytes.length), CHARSET);
        } catch (Exception e) {
            throw new DingTalkEncryptException(DingTalkEncryptException.COMPUTE_DECRYPT_TEXT_LENGTH_ERROR);
        }

        // corpid����ͬ�����
        if (!fromCorpid.equals(corpId)) {
            throw new DingTalkEncryptException(DingTalkEncryptException.COMPUTE_DECRYPT_TEXT_CORPID_ERROR);
        }
        return plainText;
    }

    /**
     * ����ǩ��
     * @param token         isv token
     * @param timestamp     ʱ���
     * @param nonce          �����
     * @param encrypt       �����ı�
     * @return
     * @throws DingTalkEncryptException
     */
    public String getSignature(String token, String timestamp, String nonce, String encrypt) throws DingTalkEncryptException {
        try {
            String[] array = new String[] { token, timestamp, nonce, encrypt };
            Arrays.sort(array);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 4; i++) {
                sb.append(array[i]);
            }
            String str = sb.toString();
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(str.getBytes());
            byte[] digest = md.digest();

            StringBuffer hexstr = new StringBuffer();
            String shaHex = "";
            for (int i = 0; i < digest.length; i++) {
                shaHex = Integer.toHexString(digest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexstr.append(0);
                }
                hexstr.append(shaHex);
            }
            return hexstr.toString();
        } catch (Exception e) {
            throw new DingTalkEncryptException(DingTalkEncryptException.COMPUTE_SIGNATURE_ERROR);
        }
    }






}
