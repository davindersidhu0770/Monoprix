package com.production.monoprix.ui.webview


import android.content.Context
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.production.monoprix.MyApplication
import com.production.monoprix.R
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.activity_webview.*
import kotlinx.android.synthetic.main.include_toolbar.*


class WebviewActivity : AppCompatActivity() {


    var desc = ""
    var pageName = ""
    private lateinit var session: SessionManager
    private var isInside: Boolean = false

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = SessionManager(this)
        setContentView(R.layout.activity_webview)

        val ii = getIntent()
        if (ii.getStringExtra("description") != null)
            desc = ii.getStringExtra("description")!!
        if (ii.getStringExtra("page_name") != null)
            pageName = ii.getStringExtra("page_name")!!

        Log.d("Title::", pageName)
        txt_title.text = pageName

        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        //webView.getSettings().setBuiltInZoomControls(true);
        //webView.getSettings().setDefaultFontSize(40);
        webView.settings.minimumFontSize = 30
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (!isInside) {
                    Utils.showProgressDialog(this@WebviewActivity, true)
                    isInside = true
                }
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                Utils.pd.dismiss()
            }
        }

        val result = Html.fromHtml(desc).toString()

        if (session.languageselect == "en") {
            webView.loadData(
                "<html><body>" + result + "</body></html>",
                "text/html; charset=UTF-8",
                null
            )
        } else {
            webView.loadData(
                "<html><body dir=\"rtl\">" + result + "</body></html>",
                "text/html; charset=UTF-8",
                null
            )
        }



        img_left_arrow.setOnClickListener { onBackPressed() }
    }
    /*fun html2text(html: String?): String? {
        return Jsoup.parse(html).text()
    }*/

}