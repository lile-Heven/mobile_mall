package com.huayu.lile.net;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

public class NetUtils {
	public static String myWebUrl="http://10.0.2.2:8080/com.huayu.lile.music_player.background";
	//һ�������򵥵����ӿͻ��˵����ӷ���	
	public static String easyLinkInternet(String request){
				String str_entity="ֵΪnull";
		
		
			HttpClient httpClient=new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(myWebUrl+request);
			
			//�����HttpGetӦ�����൱��HttpRequest
			try {
				
				HttpResponse httpResponse = httpClient.execute(httpGet);
				int statusCode=httpResponse.getStatusLine().getStatusCode();
				System.out.println(statusCode);
				if(statusCode==200){
					//200����Ӧ�����������ӳɹ���ִ������
					Log.d("tag", "success_statusCode==200");
					HttpEntity entity = httpResponse.getEntity();
					//��entity�е�����ת�����ַ��������õ�json
					str_entity =EntityUtils.toString(entity);
					
					//��entity�е�����ת�����ַ��������õ����������ص��ַ���
					// str_entity = entity.toString();
					System.out.println(str_entity);
				}
				
				
				
			} catch (ClientProtocolException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			} 
			
			return str_entity;
			
		}
}
