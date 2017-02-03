package com.alibaba.cardscanner.service.openapi.message;

import java.util.List;

/** 
  { 
    "message_url": "http://dingtalk.com", 
    "head": {
        "bgcolor": "FFCC0000"
    }, 
    "body": {
        "title": "����",
        "form": [
            {
                "key": "����",
                "value": "����"
            },
            {
                "key": "����",
                "value": "30"
            }
        ],
        "rich": {
            "num": "15.6",
            "unit": "Ԫ"
        },
        "content": "����ı�",
        "image": "@lADOAAGXIszazQKA",
        "file_count": "3",
        "author": "����"
    }
 */
public class OAMessage extends Message {
	
	public String message_url;
	public Head head;
	public Body body;
	

	@Override
	public String type() {
		return "oa";
	}
	
	//content
	public static class Head {
		public String bgcolor;
	}
	
	public static class Body {
		public String title;
		public List<Form> form;
		public Rich rich;
		public String content;
		public String image;
		public String file_found;
		public String author;
		
		public static class Form {
			public String key;
			public String value;
		}
		
		public static class Rich {
			public String num;
			public String unit;
		}
	}
}
