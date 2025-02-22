package com.production.monoprix.ui.login;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.production.monoprix.R;

public class AuthenticationDialog_old extends Dialog {
    private final String request_url;
    private final String redirect_url;
    private AuthenticationListener listener;

    public AuthenticationDialog_old(@NonNull Context context, AuthenticationListener listener) {
        super(context);
        this.listener = listener;
        /*this.redirect_url = context.getResources().getString(R.string.redirect_url);

        this.request_url = context.getResources().getString(R.string.base_url) +
                "oauth/authorize/?client_id=" +
                context.getResources().getString(R.string.client_id) +
                "&redirect_uri=" + redirect_url +
                "&response_type=code&scope=user_profile,user_media";*/


        this.redirect_url = context.getResources().getString(R.string.redirect_url);
        this.request_url = context.getResources().getString(R.string.base_url) +
                "oauth/authorize/?client_id=" +
                context.getResources().getString(R.string.client_id) +
                "&redirect_uri=" + redirect_url +
                "&response_type=token&display=touch&scope=public_content";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.auth_dialog);
        initializeWebView();
    }

    private void initializeWebView() {
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(request_url);
        webView.setWebViewClient(webViewClient);
    }

    WebViewClient webViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(redirect_url)) {
                AuthenticationDialog_old.this.dismiss();
                return true;
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if(url != null){
                Uri uri = Uri.EMPTY.parse(url);
               String access_token="IGQVJVTzEwd0NtdnJ5dG81aVNQeTVoSXdDWDJabU84WkxrellVZAXRaN0F4bElraGhSdk1mWHZA0WUlJdC03S2REcWhsVkVVU1pJcUY0bVA3aHZAlREdHbE5Yb1ltRFFCT3lRY0lHdExTSVRTSlllOG1ZAaQZDZD";
               // String access_token = uri.getEncodedFragment();
                access_token = access_token.substring(access_token.lastIndexOf("=") + 1);
                Log.e("access_token", access_token);
                listener.onTokenReceived(access_token);
                dismiss();
            } else if(url.contains("?error")){
                Log.e("access_token", "getting error fetching access token");
                dismiss();
            }
           /* if (url.contains("access_token=")) {
                Uri uri = Uri.EMPTY.parse(url);
                String access_token = uri.getEncodedFragment();
                access_token = access_token.substring(access_token.lastIndexOf("=") + 1);
                Log.e("access_token", access_token);
                listener.onTokenReceived(access_token);
                dismiss();
            } else if (url.contains("?error")) {
                Log.e("access_token", "getting error fetching access token");
                dismiss();
            }*/
        }
    };
}