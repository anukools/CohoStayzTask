package com.tesk.cohostayz.network;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

@SuppressWarnings("deprecation")
public class ApiConnector {
	 
	
	public JSONObject GetAllShops(List<NameValuePair> params,String url) {
		

		HttpEntity httpEntity = null;

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			String paramString = URLEncodedUtils.format(params, "utf-8");
			url += "?" + paramString;
			
			System.out.println("URL : " + url);
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient.execute(httpGet);

			httpEntity = httpResponse.getEntity();
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject jsonObj = null;
		if (httpEntity != null) {
			try {
				String entityResponse = EntityUtils.toString(httpEntity);

				Log.d("RESPONSE : ", entityResponse);

				// Log.e("Entity Response : ", entityResponse);

				jsonObj = new JSONObject(entityResponse);

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return jsonObj;
	}
}