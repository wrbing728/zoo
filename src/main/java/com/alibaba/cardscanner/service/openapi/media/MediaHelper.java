package com.alibaba.cardscanner.service.openapi.media;

import java.io.File;
import com.alibaba.cardscanner.service.openapi.Env;
import com.alibaba.cardscanner.service.openapi.OApiException;
import com.alibaba.cardscanner.service.openapi.OApiResultException;
import com.alibaba.cardscanner.service.openapi.utils.HttpHelper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class MediaHelper {
	
	public static final String TYPE_IMAGE = "image";
	public static final String TYPE_VOICE = "voice";
	public static final String TYPE_VIDEO = "video";
	public static final String TYPE_FILE = "file";
	
	
	public static class MediaUploadResult {
		public String type;
		public String media_id;
		public String created_at;
	}
	

	public static MediaUploadResult upload(String accessToken, String type, File file) throws OApiException {
		String url = Env.OAPI_HOST + "/media/upload?" +
				"access_token=" + accessToken + "&type="  + type;
		JSONObject response = HttpHelper.uploadMedia(url, file);
		if (!response.containsKey("type") || !response.containsKey("media_id") || 
				response.containsKey("created_at")) {
			return JSON.parseObject(response.toJSONString(), MediaUploadResult.class);
		}
		else {
			throw new OApiResultException("type or media_id or create_at");
		}
	}
	
	
	public static void download(String accessToken, String mediaId, String fileDir) throws OApiException {
		String url = Env.OAPI_HOST + "/media/get?" +
				"access_token=" + accessToken + "&media_id="  + mediaId;
		JSONObject response = HttpHelper.downloadMedia(url, fileDir);
		System.out.println(response);
	}
}
