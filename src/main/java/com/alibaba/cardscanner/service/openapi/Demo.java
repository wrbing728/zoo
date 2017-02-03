/*
package com.alibaba.cardscanner.service.openapi;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.cardscanner.service.openapi.auth.AuthHelper;
import com.alibaba.cardscanner.service.openapi.department.Department;
import com.alibaba.cardscanner.service.openapi.department.DepartmentHelper;
import com.alibaba.cardscanner.service.openapi.media.MediaHelper;
import com.alibaba.cardscanner.service.openapi.message.*;
import com.alibaba.cardscanner.service.openapi.user.User;
import com.alibaba.cardscanner.service.openapi.user.UserHelper;
import com.alibaba.cardscanner.service.openapi.utils.FileUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class Demo {

	public static void main(String[] args) {

		try {

			// Map<String,String> values = new HashMap<String, String>();
			// values.put("corpid", "1");
			// values.put("token", "2");
			// values.put("time", "3");

			// JSONObject json = new JSONObject();
			// json.put("corpid", "11");
			// JSONObject json1 = new JSONObject();
			// json1.put("token", "22");
			// json1.put("time", "33");
			// json.put("11", json1);
			// JSONObject json2 = new JSONObject();
			// json2.put("token", "221");
			// json2.put("time", "331");
			// json.put("22", json2);
			// FileUtils.write2File(json,"test12");
			List<Department> departments = new ArrayList<Department>();
			departments = DepartmentHelper.listDepartments("ea2a8b067c8d3cc8b93980da6119dafa");
			JSONObject usersJSON = new JSONObject();
			
			System.out.println("depart num:"+departments.size());
			for(int i = 0;i<departments.size();i++){
				JSONObject userDepJSON = new JSONObject();
				System.out.println("dep:"+departments.get(i).toString());
				List<User> users = new ArrayList<User>();
				users = UserHelper.getDepartmentUser("5ffa11df8e9c3cbf95c791f824512608", Long.valueOf(departments.get(i).id));
				for(int j = 0;j<users.size();j++){
					String user = JSON.toJSONString(users.get(j));
					userDepJSON.put(j+"", JSONObject.parseObject(user, User.class));
				}
				usersJSON.put(departments.get(i).name, userDepJSON);
				System.out.println("user:"+usersJSON.toString());
			}
//			response.getWriter().append(usersJSON.toJSONString());
			System.out.println("depart:"+usersJSON.toJSONString());

			System.out.println("11:" + FileUtils.read2JSON("test12"));
			JSONObject json = new JSONObject();
			JSONObject json1 = new JSONObject();
			json1.put("token", "22aa1");
			json1.put("time", "33aa1");
			json.put("11", json1);
			FileUtils.write2File(json,"test12");
//			System.out.println("time:" + ((JSONObject) FileUtils.getValue("test12", "22")).getString("token"));
			System.out.println("11:" + FileUtils.read2JSON("test12"));

			// ��ȡaccess token
			String accessToken = AuthHelper.getAccessToken("ding0a4b72b432ac3b96");
			log("�ɹ���ȡaccess token: ", accessToken);

			// ��ȡjsapi ticket
			String ticket = AuthHelper.getJsapiTicket(accessToken,"");
			log("�ɹ���ȡjsapi ticket: ", ticket);

			// ��ȡǩ��
			String nonceStr = "nonceStr";
			long timeStamp = System.currentTimeMillis();
			String url = "http://www.dingtalk.com";
			String signature = AuthHelper.sign(ticket, nonceStr, timeStamp, url);
			log("�ɹ�ǩ��: ", signature);

			// ��������
			String name = "TestDept.34";
			String parentId = "1";
			String order = "1";
			boolean createDeptGroup = true;
			long departmentId = DepartmentHelper.createDepartment(accessToken, name, parentId, order, createDeptGroup);
			log("�ɹ���������", name, " ����id=", departmentId);

			// ��ȡ�����б�
			List<Department> list = DepartmentHelper.listDepartments(accessToken);
			log("�ɹ���ȡ�����б�", list);

			// ���²���
			name = "hahahaha";
			boolean autoAddUser = true;
			String deptManagerUseridList = "11|11";
			boolean deptHiding = false;
			String deptPerimits = "aa|qq";
			DepartmentHelper.updateDepartment(accessToken, name, parentId, order, departmentId, autoAddUser,
					deptManagerUseridList, deptHiding, deptPerimits);
			log("�ɹ����²���", " ����id=", departmentId);

			// ������Ա
			User user = new User("id_yuhuan", "name_yuhuan");
			user.email = "yuhuan@abc.com";
			user.mobile = "18645512324";
			user.department = new ArrayList();
			user.department.add(departmentId);
			UserHelper.createUser(accessToken, user);
			log("�ɹ�������Ա", "��Ա��Ϣ=", user);

			// �ϴ�ͼƬ
			File file = new File("/Users/liqiao/Desktop/icon.jpg");
			MediaHelper.MediaUploadResult uploadResult = MediaHelper.upload(accessToken, MediaHelper.TYPE_IMAGE, file);
			log("�ɹ��ϴ�ͼƬ", uploadResult);

			// ����ͼƬ
			String fileDir = "/Users/liqiao/Desktop/";
			MediaHelper.download(accessToken, uploadResult.media_id, fileDir);
			log("�ɹ�����ͼƬ");

			TextMessage textMessage = new TextMessage("TextMessage");
			ImageMessage imageMessage = new ImageMessage(uploadResult.media_id);
			LinkMessage linkMessage = new LinkMessage("http://www.baidu.com", "@lALOACZwe2Rk", "Link Message",
					"This is a link message");

			// ����oa��Ϣ
			OAMessage oaMessage = new OAMessage();
			oaMessage.message_url = "http://www.dingtalk.com";
			OAMessage.Head head = new OAMessage.Head();
			head.bgcolor = "FFCC0000";
			oaMessage.head = head;
			OAMessage.Body body = new OAMessage.Body();
			body.title = "��������";
			OAMessage.Body.Form form1 = new OAMessage.Body.Form();
			form1.key = "����";
			form1.value = "������";
			OAMessage.Body.Form form2 = new OAMessage.Body.Form();
			form2.key = "����";
			form2.value = "18";
			body.form = new ArrayList();
			body.form.add(form1);
			body.form.add(form2);
			OAMessage.Body.Rich rich = new OAMessage.Body.Rich();
			rich.num = "5";
			rich.unit = "ë";
			body.rich = rich;
			body.content = "����һ��������������¡���Լ��";
			body.image = "";
			body.file_found = "3";
			body.author = "ʶ��";
			oaMessage.body = body;

			// ����΢Ӧ����Ϣ
			String toUsers = Vars.TO_USER;
			String toParties = Vars.TO_PARTY;
			String agentId = Vars.AGENT_ID;
			LightAppMessageDelivery lightAppMessageDelivery = new LightAppMessageDelivery(toUsers, toParties, agentId);

			lightAppMessageDelivery.withMessage(textMessage);
			MessageHelper.send(accessToken, lightAppMessageDelivery);
			log("�ɹ����� ΢Ӧ���ı���Ϣ");
			lightAppMessageDelivery.withMessage(imageMessage);
			MessageHelper.send(accessToken, lightAppMessageDelivery);
			log("�ɹ����� ΢Ӧ��ͼƬ��Ϣ");
			lightAppMessageDelivery.withMessage(linkMessage);
			MessageHelper.send(accessToken, lightAppMessageDelivery);
			log("�ɹ����� ΢Ӧ��link��Ϣ");
			lightAppMessageDelivery.withMessage(oaMessage);
			MessageHelper.send(accessToken, lightAppMessageDelivery);
			log("�ɹ����� ΢Ӧ��oa��Ϣ");

			// ���ͻỰ��Ϣ
			String sender = Vars.SENDER;
			String cid = Vars.CID;
			ConversationMessageDelivery conversationMessageDelivery = new ConversationMessageDelivery(sender, cid,
					agentId);

			conversationMessageDelivery.withMessage(textMessage);
			MessageHelper.send(accessToken, conversationMessageDelivery);
			log("�ɹ����� �Ự�ı���Ϣ");
			conversationMessageDelivery.withMessage(imageMessage);
			MessageHelper.send(accessToken, conversationMessageDelivery);
			log("�ɹ����� �ỰͼƬ��Ϣ");
			conversationMessageDelivery.withMessage(linkMessage);
			MessageHelper.send(accessToken, conversationMessageDelivery);
			log("�ɹ����� �Ựlink��Ϣ");

			// ���³�Ա
			user.mobile = "18612341234";
			UserHelper.updateUser(accessToken, user);
			log("�ɹ����³�Ա", "��Ա��Ϣ=", user);

			// ��ȡ��Ա
			UserHelper.getUser(accessToken, user.userid);
			log("�ɹ���ȡ��Ա", "��Աuserid=", user.userid);

			// ��ȡ���ų�Ա
			List<User> userList = UserHelper.getDepartmentUser(accessToken, departmentId);
			log("�ɹ���ȡ���ų�Ա", "���ų�Աuser=", userList);

			// ��ȡ���ų�Ա�����飩
			List<User> userList2 = UserHelper.getUserDetails(accessToken, departmentId);
			log("�ɹ���ȡ���ų�Ա����", "���ų�Ա����user=", userList2);

			// ����ɾ����Ա
			User user2 = new User("id_yuhuan2", "name_yuhuan2");
			user2.email = "yuhua2n@abc.com";
			user2.mobile = "18611111111";
			user2.department = new ArrayList();
			user2.department.add(departmentId);
			UserHelper.createUser(accessToken, user2);

			List<String> useridlist = new ArrayList();
			useridlist.add(user.userid);
			useridlist.add(user2.userid);
			UserHelper.batchDeleteUser(accessToken, useridlist);
			log("�ɹ�����ɾ����Ա", "��Ա�б�useridlist=", useridlist);

			// ɾ����Ա
			User user3 = new User("id_yuhuan3", "name_yuhuan3");
			user3.email = "yuhua2n@abc.com";
			user3.mobile = "18611111111";
			user3.department = new ArrayList();
			user3.department.add(departmentId);
			UserHelper.createUser(accessToken, user3);
			UserHelper.deleteUser(accessToken, user3.userid);
			log("�ɹ�ɾ����Ա", "��Աuserid=", user3.userid);

			// ɾ������
			DepartmentHelper.deleteDepartment(accessToken, departmentId);
			log("�ɹ�ɾ������", " ����id=", departmentId);

		} catch (OApiException e) {
			e.printStackTrace();
		}
	}

	private static void log(Object... msgs) {
		StringBuilder sb = new StringBuilder();
		for (Object o : msgs) {
			if (o != null) {
				sb.append(o.toString());
			}
		}
		System.out.println(sb.toString());
	}
}
*/
