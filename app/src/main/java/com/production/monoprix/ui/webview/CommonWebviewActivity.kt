package com.production.monoprix.ui.webview


import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.production.monoprix.MyApplication
import com.production.monoprix.R
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.activity_webview.*
import kotlinx.android.synthetic.main.include_toolbar.*


class CommonWebviewActivity : AppCompatActivity() {

    private lateinit var mWebView: WebView
    private lateinit var img_left_arrow: ImageView
    var link = ""

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commonwebview)

        mWebView = findViewById(R.id.webView)
        img_left_arrow = findViewById(R.id.img_left_arrow)
        Utils.showProgressDialog(this, true)
        Handler().postDelayed(Runnable {
            // do stuff

            Utils.pd.dismiss()
        }, 4000)
        var args = getIntent()
        link = args?.getStringExtra("weblink")!!
        // Enable Javascript
        val webSettings: WebSettings = mWebView.getSettings()
        mWebView.settings.javaScriptEnabled = true
        mWebView.settings.loadWithOverviewMode = true
        mWebView.settings.useWideViewPort = true
        //webView.getSettings().setBuiltInZoomControls(true);
        //webView.getSettings().setDefaultFontSize(40);
//      mWebView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + ApiManager.MRESTAURANTWEBLINK)
        mWebView.loadUrl(link)
        // Force links and redirects to open in the WebView instead of in a browser
//        mWebView.setWebViewClient(WebViewClient())

        webView!!.webViewClient = object : WebViewClient() {

            //If you will not use this method url links are opeen in new brower not in webview
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

                /*view.loadUrl(url)*/
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(browserIntent)
                return true
            }

            //Show loader on url load
            override fun onLoadResource(view: WebView, url: String) {

            }

            override fun onPageFinished(view: WebView, url: String) {
                Utils.pd.dismiss()
            }

        }

        img_left_arrow.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                // Do some work here

                onBackPressed()
            }

        })

    }


}