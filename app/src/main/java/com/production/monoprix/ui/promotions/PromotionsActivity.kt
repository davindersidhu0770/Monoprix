package com.production.monoprix.ui.promotions

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.crashlytics.android.BuildConfig
import com.daimajia.slider.library.Animations.DescriptionAnimation
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.daimajia.slider.library.Tricks.ViewPagerEx
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.production.monoprix.MyApplication
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.CountryModel
import com.production.monoprix.model.StatusBannerModel
import com.production.monoprix.mvp.BaseMvpActivity
import com.production.monoprix.ui.home.HomeFragment
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import com.production.monoprix.util.Utils.Companion.pd
import kotlinx.android.synthetic.main.activity_promotions.*
import kotlinx.android.synthetic.main.activity_webview.*
import java.io.File

class PromotionsActivity :
    BaseMvpActivity<PromotionContractor.View, PromotionContractor.Presenter>(),
    PromotionContractor.View,
    View.OnClickListener, BaseSliderView.OnSliderClickListener,
    ViewPagerEx.OnPageChangeListener, OnPageChangeListener {

    private var pageNumber: Int = 0
    override var mPresenter: PromotionContractor.Presenter = PromotionPresenter()
    lateinit var mLinearLayoutManager: LinearLayoutManager
    lateinit var sessionManager: SessionManager
    lateinit var rlpromotion: RelativeLayout
    lateinit var mAdapter: PromotionaAdapter
    var mBannerList: MutableList<String> = java.util.ArrayList<String>()
    lateinit var textSliderView: DefaultSliderView
    var isZoomSize = 0f

    // on below line we are creating a variable for our pdf view url.
//    var pdfUrl = "https://unec.edu.az/application/uploads/2014/12/pdf-sample.pdf"
    lateinit var pdfView: PDFView
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promotions)
        init()
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun init() {
        /*onclick listener*/
        rlpromotion = findViewById(R.id.rlpromotion)
        sessionManager = SessionManager(this)
        ivleft_arrow.setOnClickListener(this)
        ivshare.setOnClickListener(this)
        /*ivleft_arrow*/
        mLinearLayoutManager = LinearLayoutManager(this)
        list.layoutManager = mLinearLayoutManager
        /*Utils.showProgressDialog(getContext(), true)*/
        showProgressDialog();
        val webview = findViewById(R.id.webview) as WebView
        pdfView = findViewById(R.id.pdfView)

        webview.clearCache(true)
        webview.settings.javaScriptEnabled = true
        webview.settings.pluginState = WebSettings.PluginState.ON
        webview.webViewClient = Callback()
        webview.loadUrl("https://www.monoprix.qa/promotions/")

        webview.getSettings().setJavaScriptEnabled(true)
        webview.getSettings().setLoadWithOverviewMode(true)
        webview.getSettings().setUseWideViewPort(true)
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);


        webview.webViewClient = object : WebViewClient() {
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

            }
        }


//        progressBar.visibility = View.VISIBLE
/*
        downloadPdfFromInternet(
            getPdfUrl().toString(),
            getRootDirPath(this),
            "myFile.pdf"

        )
*/
/*
        ivzoomin.setOnClickListener(View.OnClickListener {

            isZoomSize++
            pdfView.zoomWithAnimation(isZoomSize)
//          pdfView.resetZoom()
        })

        ivleft.setOnClickListener(View.OnClickListener {

            var currentPage = pdfView.currentPage
            if (currentPage >= 0) {
                if (currentPage != 0)
                    currentPage--
                pdfView.jumpTo(currentPage)
            }

        })

        ivright.setOnClickListener(View.OnClickListener {

            var currentPage = pdfView.currentPage
            if (currentPage >= 0) {
                currentPage++
                pdfView.jumpTo(currentPage)
            }

        })

        ivzoomout.setOnClickListener(View.OnClickListener {

            if (isZoomSize > 1)
                if (isZoomSize != 0f)
                    isZoomSize--

            pdfView.zoomWithAnimation(isZoomSize)
//            pdfView.zoom

        })*/

    }

    fun showProgressDialog() {

        Log.d("24Jan", "Loader satrted")

        /* if (pd!=null)
             pd.dismiss()*/
        Utils.showProgressDialog(getContext(), true)

        Handler().postDelayed(Runnable {
            // do stuff

            pd.dismiss()
        }, 4000)
    }

    fun getRootDirPath(context: Context): String {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val file: File = ContextCompat.getExternalFilesDirs(
                context.applicationContext,
                null
            )[0]
            file.absolutePath
        } else {
            context.applicationContext.filesDir.absolutePath
        }
    }

    fun getPdfUrl(): String? {

//      var pdfUrl = intent?.getStringExtra("PLink")
        var pdfUrl = sessionManager.PROMOTION_LINK

        if (pdfUrl != null && !pdfUrl.isNullOrBlank()) {
            Log.d("20Jan" + "pdf", pdfUrl)
            rlpromotion.visibility = View.VISIBLE
        } else {
            rlpromotion.visibility = View.GONE

            Log.d("20Jan" + "pdf", "Pdf is Empty")

            // show dialog with static alert message...
            dialog()
        }

        return pdfUrl
    }

    private fun dialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.nopromotionsavailable)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, id ->
                onBackPressed()

            }
        val alert = builder.create()
        alert.show()
    }


    override fun onPageChanged(page: Int, pageCount: Int) {
        pageNumber = page + 1
        tvpage.text = (pageNumber).toString() + "/" + pageCount.toString()
        Log.d("Pageee", pageNumber.toString())
        Log.d("Pageee" + "Count ", pageCount.toString())
        //do what you want with the pageNumber
    }

    private inner class Callback : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView, url: String
        ): Boolean {
            return false
        }

        override fun onPageFinished(view: WebView, url: String) {
            Utils.pd.dismiss()
        }
    }

    override fun showPromotions(response: CountryModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {
            mAdapter = PromotionaAdapter(response.data, this)
            list.adapter = mAdapter
        } else if (response.status == 402) {
            Utils.tokenExpire(this)
            Utils.showProgressDialog(this, true)
            mPresenter.loadPromotions()
        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    override fun showReloadButton(throwable: Throwable, errorBody: ErrorBody?, network: Boolean) {
        Utils.pd.dismiss()
        when {
            network -> showDilaogBox(
                getString(R.string.no_internet),
                getString(R.string.generic_no_internet_desc)
            )
//            errorBody != null -> showMessage(errorBody.toString())
//            throwable is SocketException -> showMessage(throwable.message!!)
//            else -> showMessage(throwable.message!!)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivleft_arrow -> {
                onBackPressed()
            }


            R.id.ivshare -> {

                // share pdf ....

//                val aName = intent.getStringExtra("myFile")
                val aName = "myFile.pdf"
                val shareIntent = Intent(Intent.ACTION_SEND)

                /*  val uri = FileProvider.getUriForFile(
                      this,
                      this.getPackageName().toString() + ".provider",
                      outputFile
                  )*/

                shareIntent.putExtra(
                    Intent.EXTRA_STREAM, uriFromFile(
                        this,
                        File(this.getExternalFilesDir(null)?.absolutePath.toString(), "$aName")
                    )
                )
                shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                shareIntent.type = "application/pdf"
                startActivity(Intent.createChooser(shareIntent, "share.."))

            }

        }

    }

    fun uriFromFile(context: Context, file: File): Uri {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(
                context,
                com.production.monoprix.BuildConfig.APPLICATION_ID + ".provider",
                file
            )
        } else {
            return Uri.fromFile(file)
        }
    }

    private fun getSliderBanners() {
        if (banner != null) {
            banner.removeAllSliders()
        }
        for (i in mBannerList.indices) {
            textSliderView = DefaultSliderView(this@PromotionsActivity)
            Log.e("===========", "" + mBannerList[i].replace("//", "/"))
            textSliderView
                .image(mBannerList[i].replace("//", "/").replace("https:/", "https://"))
                .setScaleType(BaseSliderView.ScaleType.CenterInside)
                .setOnSliderClickListener(this)
            banner.addSlider(textSliderView)

        }
        // banner.moveNextPosition(true)
        banner.movePrevPosition(false)
        banner.setPresetTransformer(SliderLayout.Transformer.Accordion)
        banner.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        banner.setCustomAnimation(DescriptionAnimation())
        banner.pagerIndicator.setVisibility(View.VISIBLE)
        banner.pagerIndicator
            .setDefaultIndicatorColor(
                ContextCompat.getColor(this@PromotionsActivity, R.color.color2),
                ContextCompat.getColor(this@PromotionsActivity, R.color.gray)
            )
        banner.setDuration(3000)
        banner.addOnPageChangeListener(this)

        if (mBannerList.size == 1) {
            banner.stopAutoCycle()
        } else {
            banner.startAutoCycle()
        }
    }

    override fun showBanner(response: StatusBannerModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {

            mBannerList = response.data as MutableList<String>
            getSliderBanners()
        } else if (response.status == 402) {
            Utils.tokenExpire(this)
            Utils.showProgressDialog(this, true)
            mPresenter.loadBanner()
        } else {
            showMessage(response.statusMessage)
        }
    }

    override fun onSliderClick(slider: BaseSliderView?) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        //TODO("Not yet implemented")
    }

    override fun onPageSelected(position: Int) {
        //TODO("Not yet implemented")
    }

    override fun onPageScrollStateChanged(state: Int) {
        // TODO("Not yet implemented")
    }
}