package com.production.monoprix.ui.webview

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.production.monoprix.R
import com.production.monoprix.util.SessionManager


class WebviewFragment : Fragment() {

    private lateinit var mWebView: WebView
    var link = ""
    private lateinit var session: SessionManager
    lateinit var pd: Dialog


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_webview, container, false)
        mWebView = v.findViewById(R.id.webView) as WebView

        var args = getArguments()
        link = args?.getString("weblink", "").toString()
        // Enable Javascript
        val webSettings: WebSettings = mWebView.getSettings()
        mWebView.settings.javaScriptEnabled = true
        mWebView.settings.loadWithOverviewMode = true
        mWebView.settings.useWideViewPort = true
        pd = Dialog(requireContext())
        pd.setContentView(R.layout.dialog_progressbar)
        pd.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pd.setCanceledOnTouchOutside(false)
        showProgressDialog()

        mWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                /* pd.dismiss()*/
//              showProgressDialog()
                Log.d("24Jan", "DISMISSSSSS SHOULD")
                Log.d("27feb", "url :" + url)

                /*view.loadUrl(url)*/
                Log.d("24Jan", "loadUrl")

                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(browserIntent)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                /* if (pd != null) {

                     Log.d("24Jan", "DISMISSSSSS onPageFinished")
                     pd.dismiss()

                 }*/
                Log.d("24Jan", "onPageFinished")

            }

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                Log.d("24Jan", "onReceivedError")

                Toast.makeText(requireActivity(), "Error:$description", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        mWebView.loadUrl(link)

        onClick(view)
        return v
    }

    fun showProgressDialog() {

        Log.d("24Jan", "Loader satrted")

        /* if (pd!=null)
             pd.dismiss()*/

        pd.show()

        Handler().postDelayed(Runnable {
            // do stuff

            pd.dismiss()
        }, 4000)
    }

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_left_arrow -> {
                activity!!.onBackPressed()
            }

        }
    }


}