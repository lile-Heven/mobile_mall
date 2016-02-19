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
	//一个创建简单的连接客户端的链接方法	
	public static String easyLinkInternet(String request){
				String str_entity="值为null";
		
		
			HttpClient httpClient=new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(myWebUrl+request);
			
			//这里的HttpGet应该是相当于HttpRequest
			try {
				
				HttpResponse httpResponse = httpClient.execute(httpGet);
				int statusCode=httpResponse.getStatusLine().getStatusCode();
				System.out.println(statusCode);
				if(statusCode==200){
					//200的响应码代表的是连接成功且执行良好
					Log.d("tag", "success_statusCode==200");
					HttpEntity entity = httpResponse.getEntity();
					//将entity中的数据转化成字符串——得到json
					str_entity =EntityUtils.toString(entity);
					
					//将entity中的数据转化成字符串——得到服务器返回的字符串
					// str_entity = entity.toString();
					System.out.println(str_entity);
				}
				
				
				
			} catch (ClientProtocolException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} 
			
			return str_entity;
			
		}
}
