package com.spike;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NeatWebView extends Activity
{
    WebView webView;
    final Activity activity = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        webView = (WebView) findViewById(R.id.webview);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NORMAL);

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL,
                            Uri.parse(url));
                    startActivity(intent);
                }else if(url.startsWith("mailto:")){
                    Intent intent = new Intent(Intent.ACTION_SENDTO,
                            Uri.parse(url));
                    startActivity(intent);
                }else if(url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                }
                return true;
            }
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
                Toast.makeText(activity, "Error Code: " + Integer.toString(errorCode) + ": " + description, Toast.LENGTH_LONG).show();
                view.loadUrl("http://www.google.com");
            }
        });

        Map<String,String> map = new HashMap<String, String>();
        Log.d("Login", login().current());
        map.put("Authorization", "OAuth " + login().current());
        map.put("X-Neat-Device", "android");
//        map.put("X-Neat-Version","1.0 build 183");
//        map.put("Accept", "text/html");

        webView.loadUrl("https://cloud-qa.neat.com/folders", map);
//        webView.loadUrl("http://192.168.1.10:3000/folders", map);

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("Back", "I'm here");
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Toast.makeText(activity, "I just pushed the back button and now I'm catching the action!", Toast.LENGTH_LONG).show();
            webView.loadUrl("http://www.google.com");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public TokenCredentials login() {
        String loginResponse = null;
        TokenCredentials tokenCredentials = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("https://cloud-qa.neat.com/oauth/access_token");
//        HttpPost httpPost = new HttpPost("http://192.168.1.10:3000/oauth/access_token");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//            nameValuePairs.add(new BasicNameValuePair("client_secret", "ecd1a4efede18c7323018a20bb95f4acf828b2febcd22b759a6bc2bf858d4a44"));
//            nameValuePairs.add(new BasicNameValuePair("client_id", "4e9e5dd422b7a94bb7000108"));
            nameValuePairs.add(new BasicNameValuePair("client_secret", "91f85595c7d364294aa596f4378ad99fa587d9c636e6ce3b53790f5fe72e3985"));
            nameValuePairs.add(new BasicNameValuePair("client_id", "4e9e5dd422b7a94bb7000107"));
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
            JSONObject jArray = new JSONObject(loginResponse);
            String token = jArray.getString("access_token");
            tokenCredentials = new TokenCredentials(token);
            Log.d("Login", loginResponse);


        } catch (ClientProtocolException e) {
            Log.e("Error", "Client Protocol Exception");
        } catch (IOException e) {
            Log.e("Error", "Network Exception");
        } catch (JSONException e) {
            Log.e("Error", "JSON Exception");
        }
        return tokenCredentials;
    }
}
