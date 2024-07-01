package com.production.monoprix.ui.webview

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.production.monoprix.MyApplication
import com.production.monoprix.R
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.include_toolbar.*


class ShowWebView : Activity() {
    internal var url = ""
    internal var pageName = ""
    private var webView: WebView? = null
    private lateinit var session: SessionManager
    private var isInside: Boolean = false

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = SessionManager(this)
        setContentView(R.layout.activity_webview)
        val ii = intent
        url = ii.getStringExtra("page_url")!!
        pageName = ii.getStringExtra("page_name")!!
        txt_title.text = pageName
        //Get webview
        webView = findViewById(R.id.webView)
        if (!url.toLowerCase().endsWith(".pdf")) {
            if(!isInside) {
                Utils.showProgressDialog(this@ShowWebView, true)
                isInside = true
            }
            startWebView(url)
        } else {
            startpdfview(url)
        }
        img_left_arrow.setOnClickListener { onBackPressed() }
    }

    private fun startpdfview(url: String) {
        webView!!.settings.javaScriptEnabled = true
        webView!!.settings.pluginState = WebSettings.PluginState.ON
        webView!!.webViewClient = Callback()
        webView!!.loadUrl("http://docs.google.com/gview?embedded=true&url=$url")
    }

    private inner class Callback : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView, url: String
        ): Boolean {
            return false
        }
    }

    private fun startWebView(url: String) {

        webView!!.webViewClient = object : WebViewClient() {

            //If you will not use this method url links are opeen in new brower not in webview
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            //Show loader on url load
            override fun onLoadResource(view: WebView, url: String) {

            }

            override fun onPageFinished(view: WebView, url: String) {
                Utils.pd.dismiss()
            }

        }

        // Javascript inabled on webview
        webView!!.getSettings().javaScriptEnabled = true

        //Load url in webview
        webView!!.loadUrl(url)

    }

    override  fun onBackPressed() {
        if (webView!!.canGoBack()) {
            webView!!.goBack()
        } else {
            // Let the system handle the back button
            super.onBackPressed()
        }
    }


}