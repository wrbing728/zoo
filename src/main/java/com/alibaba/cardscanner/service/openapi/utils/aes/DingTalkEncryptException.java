package com.alibaba.cardscanner.service.openapi.utils.aes;

import java.util.HashMap;
import java.util.Map;

/**
 * ��������ƽ̨�ӽ����쳣��
 */
public class DingTalkEncryptException extends Exception {
    /**�ɹ�**/
    public static final int SUCCESS = 0;
    /**���������ı��Ƿ�**/
    public final static int  ENCRYPTION_PLAINTEXT_ILLEGAL = 900001;
    /**����ʱ��������Ƿ�**/
    public final static int  ENCRYPTION_TIMESTAMP_ILLEGAL = 900002;
    /**��������ַ��������Ƿ�**/
    public final static int  ENCRYPTION_NONCE_ILLEGAL = 900003;
    /**���Ϸ���aeskey**/
    public final static int AES_KEY_ILLEGAL = 900004;
    /**ǩ����ƥ��**/
    public final static int SIGNATURE_NOT_MATCH = 900005;
    /**����ǩ������**/
    public final static int COMPUTE_SIGNATURE_ERROR = 900006;
    /**����������ִ���**/
    public final static int COMPUTE_ENCRYPT_TEXT_ERROR  = 900007;
    /**����������ִ���**/
    public final static int COMPUTE_DECRYPT_TEXT_ERROR  = 900008;
    /**����������ֳ��Ȳ�ƥ��**/
    public final static int COMPUTE_DECRYPT_TEXT_LENGTH_ERROR  = 900009;
    /**�����������corpid��ƥ��**/
    public final static int COMPUTE_DECRYPT_TEXT_CORPID_ERROR  = 900010;

    private static Map<Integer,String> msgMap = new HashMap<Integer,String>();
    static{
        msgMap.put(SUCCESS,"�ɹ�");
        msgMap.put(ENCRYPTION_PLAINTEXT_ILLEGAL,"���������ı��Ƿ�");
        msgMap.put(ENCRYPTION_TIMESTAMP_ILLEGAL,"����ʱ��������Ƿ�");
        msgMap.put(ENCRYPTION_NONCE_ILLEGAL,"��������ַ��������Ƿ�");
        msgMap.put(SIGNATURE_NOT_MATCH,"ǩ����ƥ��");
        msgMap.put(COMPUTE_SIGNATURE_ERROR,"ǩ������ʧ��");
        msgMap.put(AES_KEY_ILLEGAL,"���Ϸ���aes key");
        msgMap.put(COMPUTE_ENCRYPT_TEXT_ERROR,"����������ִ���");
        msgMap.put(COMPUTE_DECRYPT_TEXT_ERROR,"����������ִ���");
        msgMap.put(COMPUTE_DECRYPT_TEXT_LENGTH_ERROR,"����������ֳ��Ȳ�ƥ��");
        msgMap.put(COMPUTE_DECRYPT_TEXT_CORPID_ERROR,"�����������corpid����suiteKey��ƥ��");
    }

    public Integer  code;
    public DingTalkEncryptException(Integer exceptionCode){
        super(msgMap.get(exceptionCode));
        this.code = exceptionCode;
    }
}
