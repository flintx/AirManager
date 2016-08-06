package com.flint.airmanager;

/**
 * Created by whufl on 2016/7/27.
 */
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.List;

@SuppressWarnings("ALL")
public class JSONParser {
    InputStream is = null;
    JSONObject jObj = null;
    JSONArray jArray = null;
    String json = "";

    /**
     * @param url
     * @param method
     * @param params
     * @return
     */
    public JSONObject makeHttpRequest(String url, String method,
                                      List<NameValuePair> params) {

        if (method == "POST") {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            HttpParams httpParams = httpClient.getParams(); // 计算网络超时用.
            try {
                httpParams.setParameter(
                        CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);// 请求超时
                httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);// 读取超时
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                }
            } catch (SocketTimeoutException e) {
                Log.e("timeout", "timeout");
            } catch (Exception e) {
                Log.e("Exception", e.toString());
            }
        } else if (method == "GET") {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String paramString = URLEncodedUtils.format(params, "utf-8");
            url += "?" + paramString;
            Log.e("url", url + "");
            HttpGet httpGet = new HttpGet(url);
            HttpParams httpParams = httpClient.getParams(); // 计算网络超时用.

            httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                    5000);// 请求超时
            httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);// 读取超时
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                }
            } catch (SocketTimeoutException e) {
                Log.e("Exception", "timeout");
            } catch (Exception e) {
                Log.e("Exception0", e.toString());
            }
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "utf-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.d("JSON: ", json);
            jObj = new JSONObject(json);
        } catch (Exception e) {
            Log.e("Exception1", e.toString());
        }
        return jObj;
    }

    public String makeHttpRequest(String url, List<NameValuePair> params,
                                  String unicode) {
        String result = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String paramString = URLEncodedUtils.format(params, unicode);
        url += "?" + paramString;
        Log.e("url", url + "");
        HttpGet httpGet = new HttpGet(url);
        HttpParams httpParams = httpClient.getParams(); // 计算网络超时用.

        httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);// 请求超时
        httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);// 读取超时
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }
        } catch (SocketTimeoutException e) {
            Log.e("Exception", "timeout");
        } catch (Exception e) {
            Log.e("Exception2", e.toString());
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, unicode));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("Exception3", e.toString());
        }
        return result;
    }

}
