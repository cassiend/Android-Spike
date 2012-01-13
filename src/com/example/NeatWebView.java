package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NeatWebView extends Activity
{
    WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
//        webView.loadDataWithBaseURL("https://cloud-qa.neat.com/folders", webCabinet(),"text/html", "UTF-8", null);
        webView.loadDataWithBaseURL("http://192.168.202.50:3000/folders", webCabinet(),"text/html", "UTF-8", null);
    }

    public String webCabinet(){
        HttpClient httpClient = new DefaultHttpClient();
//        HttpPost httpPost = new HttpPost("https://cloud-qa.neat.com/folders");
        HttpGet httpGet = new HttpGet("http://192.168.202.50:3000/folders");
        String webCabinet = null;

        try {
            try {
                JSONObject jArray = new JSONObject(login());
                String token = jArray.getString("access_token");

                String auth = "OAuth " + token;
                Log.d("OAuth Token", auth);
                httpGet.setHeader("Authorization", auth);
                httpGet.setHeader("X-Neat-Device", "android");

                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();

                webCabinet = EntityUtils.toString(entity);

                Log.d("Result", webCabinet);

            }catch (JSONException e) {
                Log.e("Error",  "JSON Exception");
            }
        } catch (ClientProtocolException e) {
            Log.e("Error", "Client Protocol Exception");
        } catch (IOException e) {
            Log.e("Error", "IO Exception");
        }
        return webCabinet;
    }

    public String login() {
        String loginResponse = null;
        HttpClient httpClient = new DefaultHttpClient();
//        HttpPost httpPost = new HttpPost("https://cloud-qa.neat.com/oauth/access_token");
        HttpPost httpPost = new HttpPost("http://192.168.202.50:3000/oauth/access_token");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("client_secret", "ecd1a4efede18c7323018a20bb95f4acf828b2febcd22b759a6bc2bf858d4a44"));
            nameValuePairs.add(new BasicNameValuePair("client_id", "4e9e5dd422b7a94bb7000108"));
//            nameValuePairs.add(new BasicNameValuePair("client_secret", "91f85595c7d364294aa596f4378ad99fa587d9c636e6ce3b53790f5fe72e3985"));
//            nameValuePairs.add(new BasicNameValuePair("client_id", "4e9e5dd422b7a94bb7000107"));
            nameValuePairs.add(new BasicNameValuePair("username", "sp.evan@mailinator.com"));
            nameValuePairs.add(new BasicNameValuePair("password", "p@ssw0rd"));
            nameValuePairs.add(new BasicNameValuePair("grant_type", "password"));
            nameValuePairs.add(new BasicNameValuePair("scope", "desktop_scope"));
            nameValuePairs.add(new BasicNameValuePair("instance_name", "android"));
            nameValuePairs.add(new BasicNameValuePair("instance_description", "\"\""));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            loginResponse = EntityUtils.toString(entity);
            Log.d("Login", loginResponse);


        } catch (ClientProtocolException e) {
            Log.e("Error", "Client Protocol Exception");
        } catch (IOException e) {
            Log.e("Error", "IO Exception");
        }
        return loginResponse;
    }
}
