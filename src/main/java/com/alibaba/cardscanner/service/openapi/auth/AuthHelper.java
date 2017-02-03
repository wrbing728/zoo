package com.alibaba.cardscanner.service.openapi.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Timer;
import java.util.TimerTask;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.cardscanner.service.openapi.Env;
import com.alibaba.cardscanner.service.openapi.OApiException;
import com.alibaba.cardscanner.service.openapi.OApiResultException;
import com.alibaba.cardscanner.service.openapi.utils.FileUtils;
import com.alibaba.cardscanner.service.openapi.utils.HttpHelper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class AuthHelper {

	// public static String jsapiTicket = null;
	// public static String accessToken = null;
	public static Timer timer = null;
	// ������1Сʱ50����
	public static final long cacheTime = 1000 * 60 * 55 * 2;
	public static long currentTime = 0 + cacheTime + 1;
	public static long lastTime = 0;
	public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/*
	 * �ڴ˷����У�Ϊ�˱���Ƶ����ȡaccess_token��
	 * �ھ�����һ�λ�ȡaccess_tokenʱ��������Сʱ֮�ڵ������
	 * ��ֱ�Ӵӳ־û��洢�ж�ȡaccess_token
	 *
	 * ��Ϊaccess_token��jsapi_ticket�Ĺ���ʱ�䶼��7200��
	 * �����ڻ�ȡaccess_token��ͬʱҲȥ��ȡ��jsapi_ticket
	 * ע��jsapi_ticket����ǰ��ҳ��JSAPI��Ȩ����֤���õ�ʱ����Ҫʹ�õ�
	 * ������Ϣ��鿴�������ĵ�--Ȩ����֤����
	 */
	public static String getAccessToken(String corpId) throws OApiException {
		long curTime = System.currentTimeMillis();
		JSONObject accessTokenValue = (JSONObject) FileUtils.getValue("accesstoken", corpId);
		String accToken = "";
		String jsTicket = "";
		JSONObject jsontemp = new JSONObject();
//		if(accessTokenValue!=null){
//			System.out.println("accessTokenValue:"
//					+ accessTokenValue.toJSONString() + " beginT_time:"
//					+ Long.valueOf(accessTokenValue.get("begin_time").toString()) + " cur:" + curTime + " max:"
//					+ Long.MAX_VALUE);
//		}
		if (accessTokenValue == null || curTime - accessTokenValue.getLong("begin_time") >= cacheTime) {
			System.out.println(df.format(new Date())+" authhelper: get new access_token and ticket");
			String url = Env.OAPI_HOST + "/service/get_corp_token?" + "suite_access_token="
					+ FileUtils.getValue("ticket", "suiteToken");
			JSONObject args = new JSONObject();
			args.put("auth_corpid", corpId);
			args.put("permanent_code", FileUtils.getValue("permanentcode", corpId));
			JSONObject response = HttpHelper.httpPost(url, args);
			if (response.containsKey("access_token")) {
				accToken = response.getString("access_token");

				// save accessToken
				JSONObject jsonAccess = new JSONObject();
				jsontemp.clear();
				jsontemp.put("access_token", accToken);
				jsontemp.put("begin_time", curTime);
				jsonAccess.put(corpId, jsontemp);

				FileUtils.write2File(jsonAccess, "accesstoken");
			} else {
				throw new OApiResultException("access_token");
			}

			String url_ticket = Env.OAPI_HOST + "/get_jsapi_ticket?" + "type=jsapi" + "&access_token=" + accToken;
			JSONObject response_ticket = HttpHelper.httpGet(url_ticket);
			if (response_ticket.containsKey("ticket")) {
				jsTicket = response_ticket.getString("ticket");

				// save jsticket
				JSONObject jsonTicket = new JSONObject();
				jsontemp.clear();
				jsontemp.put("ticket", jsTicket);
				jsontemp.put("begin_time", curTime);
				jsonTicket.put(corpId, jsontemp);

				FileUtils.write2File(jsonTicket, "jsticket");
			} else {
				throw new OApiResultException("ticket");
			}

		} else {
			return accessTokenValue.getString("access_token");
		}

		return accToken;
	}

	// ����������£�jsapi_ticket����Ч��Ϊ7200�룬���Կ�������Ҫ��ĳ���ط����һ����ʱ��������ȥ����jsapi_ticket
	public static String getJsapiTicket(String accessToken, String corpId) throws OApiException {
		JSONObject jsTicketValue = (JSONObject) FileUtils.getValue("jsticket", corpId);
		long curTime = System.currentTimeMillis();
		String jsTicket = "";

		 if (jsTicketValue == null || curTime - jsTicketValue.getLong("begin_time") >= cacheTime)
		 {
			String url = Env.OAPI_HOST + "/get_jsapi_ticket?" + "type=jsapi" + "&access_token=" + accessToken;
			JSONObject response = HttpHelper.httpGet(url);
			if (response.containsKey("ticket")) {
				jsTicket = response.getString("ticket");
				
				JSONObject jsonTicket = new JSONObject();
				JSONObject jsontemp = new JSONObject();
				jsontemp.clear();
				jsontemp.put("ticket", jsTicket);
				jsontemp.put("begin_time", curTime);
				jsonTicket.put(corpId, jsontemp);
				FileUtils.write2File(jsonTicket, "jsticket");

				return jsTicket;
			} else {
				throw new OApiResultException("ticket");
			}
		 } else {
			 return jsTicketValue.getString("ticket");
		 }
//
	}

	public static String sign(String ticket, String nonceStr, long timeStamp, String url) throws OApiException {
		String plain = "jsapi_ticket=" + ticket + "&noncestr=" + nonceStr + "&timestamp=" + String.valueOf(timeStamp)
				+ "&url=" + url;
		try {
			MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
			sha1.reset();
			sha1.update(plain.getBytes("UTF-8"));
			return bytesToHex(sha1.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new OApiResultException(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			throw new OApiResultException(e.getMessage());
		}
	}

	private static String bytesToHex(byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	public static String getConfig(HttpServletRequest request) {
		String urlString = request.getRequestURL().toString().replace("/getAuthConfig", "/list.html");
		//String queryString = request.getQueryString();

		String queryString = request.getParameter("sign_url");
		// todo
		String corpId = request.getParameter("corpid");
		String appId = request.getParameter("appid");

		System.out.println(df.format(new Date())+
				" getconfig,url:" + urlString + " query:" + queryString + " corpid:" + corpId + " appid:" + appId);

		String queryStringEncode = null;
		String url;
		if (queryString != null) {
			try {
				queryStringEncode = URLDecoder.decode(queryString, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			url = urlString + "?" + queryStringEncode;
		} else {
			url = urlString;
		}
		System.out.println(url);
		String nonceStr = "abcdefg";
		long timeStamp = System.currentTimeMillis() / 1000;
		String signedUrl = url;
		String accessToken = null;
		String ticket = null;
		String signature = null;
		String agentid = null;

		try {
			accessToken = AuthHelper.getAccessToken(corpId);
			ticket = AuthHelper.getJsapiTicket(accessToken, corpId);
			// ticket = FileUtils.getValue("jsticket", corpId);
//			JSONObject jsTicketValue = (JSONObject) FileUtils.getValue("jsticket", corpId);
//			ticket =jsTicketValue.getString("ticket");
			signature = AuthHelper.sign(ticket, nonceStr, timeStamp, signedUrl);
			agentid = AuthHelper.getAgentId(corpId, appId);

		} catch (OApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ConfigObject configObject = new ConfigObject();
		configObject.jsticket = ticket;
		configObject.signature = signature;
		configObject.nonceStr = nonceStr;
		configObject.timeStamp = timeStamp;
		configObject.corpid = corpId;
		configObject.agentid = agentid;
		configObject.appid = appId;

		System.out.println("jsticket: "+ticket);
		System.out.println("signedUrl: "+signedUrl);
		System.out.println(JSONObject.toJSONString(configObject));
		return JSONObject.toJSONString(configObject);
		/*return "{jsticket:'" + ticket + "',signature:'" + signature + "',nonceStr:'" + nonceStr + "',timeStamp:'"
				+ timeStamp + "',corpId:'" + corpId + "',agentid:'" + agentid+ "',appid:'" + appId + "'}";*/
	}

	public static String getAgentId(String corpId, String appId) throws OApiException {
		String agentId = null;
		String accessToken = FileUtils.getValue("ticket", "suiteToken").toString();
		String url = "https://oapi.dingtalk.com/service/get_auth_info?suite_access_token=" + accessToken;
		JSONObject args = new JSONObject();
		args.put("suite_key", Env.SUITE_KEY);
		args.put("auth_corpid", corpId);
		args.put("permanent_code", FileUtils.getValue("permanentcode", corpId));
		JSONObject response = HttpHelper.httpPost(url, args);
		// System.out.println("response11:" + response.toJSONString() + "
		// appid:" + appId);
		if (response.containsKey("auth_info")) {
			JSONArray agents = (JSONArray) ((JSONObject) response.get("auth_info")).get("agent");
			// System.out.println("size11:" + agents.size() + " :" +
			// agents.toJSONString());
			for (int i = 0; i < agents.size(); i++) {
				// System.out.println("appid:" + ((JSONObject)
				// agents.get(i)).get("appid").toString());
				if (((JSONObject) agents.get(i)).get("appid").toString().equals(appId)) {
					agentId = ((JSONObject) agents.get(i)).get("agentid").toString();
					break;
				}
			}
			// agentId = response.getString("agentid");
		} else {
			throw new OApiResultException("agentid");
		}
		return agentId;
	}

	public static String getSsoToken() throws OApiException {
		String url = "https://oapi.dingtalk.com/sso/gettoken?corpid=" + Env.CORP_ID + "&corpsecret=" + Env.SSO_Secret;
		JSONObject response = HttpHelper.httpGet(url);
		String ssoToken;
		if (response.containsKey("access_token")) {
			ssoToken = response.getString("access_token");
		} else {
			throw new OApiResultException("Sso_token");
		}
		return ssoToken;

	}

	// public static String[] getValues(String values){
	// return values.split(":");
	// }

}
