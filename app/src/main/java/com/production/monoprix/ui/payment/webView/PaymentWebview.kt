package com.production.monoprix.ui.payment.webView

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.production.monoprix.MyApplication
import com.production.monoprix.R
import com.production.monoprix.mvp.BaseMvpActivity
import com.production.monoprix.ui.payment.MyJavaScriptInterface
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.activity_webview.*
import kotlinx.android.synthetic.main.include_toolbar.*


class PaymentWebview : BaseMvpActivity<PaymentvWContractor.View, PaymentvWContractor.Presenter>(),
    PaymentvWContractor.View {

    lateinit var orderId: String
    lateinit var url: String
    var loyaltyPoints: Double = 0.0
    var isInside: Boolean = false
    lateinit var title: String
    override var mPresenter: PaymentvWContractor.Presenter = PaymentVWPresenter()
    lateinit var sessionManager: SessionManager

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }

    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        ivtop.visibility = View.VISIBLE

        loyaltyPoints = intent.getDoubleExtra("loyaltyPoints", 0.0)
        orderId = intent.getStringExtra("orderId")!!
        url = intent.getStringExtra("url")!!
        title = intent.getStringExtra("title")!!
        txt_title.text = title
        sessionManager = SessionManager(this)
//      txt_title.text = getString(R.string.payment)
        val yourWebClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }

            //Show loader on url load
            override fun onLoadResource(view: WebView, url: String) {
                if (!isInside) {
                    Utils.showProgressDialog(this@PaymentWebview, true)
                    isInside = true
                }
                Log.d("7June:onLoadResource: ", url)

            }

            override fun onPageFinished(view: WebView, url: String) {
                Utils.pd.dismiss()
                webView.loadUrl("javascript:HtmlViewer.showHTML" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")

                Log.d("7June:onPageFinished: ", url)
            }
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true
        webView.webViewClient = yourWebClient
        webView.loadUrl(url)
        webView.addJavascriptInterface(
            MyJavaScriptInterface(this, orderId, loyaltyPoints),
            "HtmlViewer"
        )
        img_left_arrow.setOnClickListener {
            onBackPressed()
        }

    }


}