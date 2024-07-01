//package com.production.monoprix.ui.promotions
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.os.Environment
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.webkit.WebView
//import android.webkit.WebViewClient
//import android.widget.Toast
//import androidx.core.content.ContextCompat
//import androidx.core.content.FileProvider
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.daimajia.slider.library.Animations.DescriptionAnimation
//import com.daimajia.slider.library.SliderLayout
//import com.daimajia.slider.library.SliderTypes.BaseSliderView
//import com.daimajia.slider.library.SliderTypes.DefaultSliderView
//import com.daimajia.slider.library.Tricks.ViewPagerEx
//import com.downloader.OnDownloadListener
//import com.downloader.PRDownloader
//import com.github.barteksc.pdfviewer.PDFView
//import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
//import com.production.monoprix.BuildConfig
//import com.production.monoprix.R
//import com.production.monoprix.api.ErrorBody
//import com.production.monoprix.model.CountryModel
//import com.production.monoprix.model.StatusBannerModel
//import com.production.monoprix.mvp.BaseMvpFragment
//import com.production.monoprix.util.SessionManager
//import com.production.monoprix.util.Utils
//import kotlinx.android.synthetic.main.activity_promotions.*
//import kotlinx.android.synthetic.main.activity_promotions.view.*
//import java.io.File
//
//class PromotionsFragment :
//    BaseMvpFragment<PromotionContractor.View, PromotionContractor.Presenter>(),
//    PromotionContractor.View,
//    View.OnClickListener, BaseSliderView.OnSliderClickListener,
//    ViewPagerEx.OnPageChangeListener, OnPageChangeListener {
//
//    private var pageNumber: Int = 0
//    override var mPresenter: PromotionContractor.Presenter = PromotionPresenter()
//    lateinit var mLinearLayoutManager: LinearLayoutManager
//    lateinit var sessionManager: SessionManager
//    lateinit var mAdapter: PromotionaAdapter
//    var mBannerList: MutableList<String> = java.util.ArrayList<String>()
//    lateinit var textSliderView: DefaultSliderView
//    var isZoomSize = 0f
//    lateinit var v : View
//
//    // on below line we are creating a variable for our pdf view url.
////    var pdfUrl = "https://unec.edu.az/application/uploads/2014/12/pdf-sample.pdf"
//    lateinit var pdfView: PDFView
//   /* override fun attachBaseContext(base: Context?) {
//        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
//    }*/
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        v = inflater.inflate(R.layout.activity_promotions, container, false)
//
//        super.onCreate(savedInstanceState)
//        init()
//        return v
//    }
//
//    @SuppressLint("SetJavaScriptEnabled")
//    fun init() {
//        /*onclick listener*/
//        sessionManager = SessionManager(requireActivity())
//        v.ivleft_arrow.setOnClickListener(this)
//        v.ivshare.setOnClickListener(this)
//        v.rltop.visibility= View.GONE
//        /*ivleft_arrow*/
//        v.tvtitle.text = getString(R.string.promotions)
//        mLinearLayoutManager = LinearLayoutManager(requireActivity())
////        list.layoutManager = mLinearLayoutManager
//
//        pdfView = v.findViewById(R.id.pdfView)
//
//        Utils.showProgressDialog(getContext(), true)
//        downloadPdfFromInternet(
//            getPdfUrl(),
//            getRootDirPath(requireActivity()),
//            "myFile.pdf"
//
//        )
//
//        v.ivzoomin.setOnClickListener(View.OnClickListener {
//
//            isZoomSize++
//            pdfView.zoomWithAnimation(isZoomSize)
////          pdfView.resetZoom()
//        })
//
//        v.ivleft.setOnClickListener(View.OnClickListener {
//
//            var currentPage = pdfView.currentPage
//            if (currentPage >= 0) {
//                if (currentPage != 0)
//                    currentPage--
//                pdfView.jumpTo(currentPage)
//            }
//
//        })
//
//        v.ivright.setOnClickListener({
//
//            var currentPage = pdfView.currentPage
//            if (currentPage >= 0) {
//                currentPage++
//                pdfView.jumpTo(currentPage)
//            }
//
//        })
//
//        v.ivzoomout.setOnClickListener({
//
//            if (isZoomSize > 1)
//                if(isZoomSize!=0f)
//                isZoomSize--
//
//            pdfView.zoomWithAnimation(isZoomSize)
////            pdfView.zoom
//
//        })
//
//    }
//
//    fun getRootDirPath(context: Context): String {
//        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
//            val file: File = ContextCompat.getExternalFilesDirs(
//                context.applicationContext,
//                null
//            )[0]
//            file.absolutePath
//        } else {
//            context.applicationContext.filesDir.absolutePath
//        }
//    }
//
//    fun getPdfUrl(): String {
//
////        var pdfUrl = "https://unec.edu.az/application/uploads/2014/12/pdf-sample.pdf"
//        var pdfUrl =
//            "https://mindorks.s3.ap-south-1.amazonaws.com/courses/MindOrks_Android_Online_Professional_Course-Syllabus.pdf"
//
//        return pdfUrl
//    }
//
//    private fun downloadPdfFromInternet(url: String, dirPath: String, fileName: String) {
//        PRDownloader.download(
//            url,
//            dirPath,
//            fileName
//        ).build()
//            .start(object : OnDownloadListener {
//                override fun onDownloadComplete() {
///*
//                    Toast.makeText(this@PromotionsActivity, "downloadComplete", Toast.LENGTH_LONG)
//                        .show()
//*/
//                    val downloadedFile = File(dirPath, fileName)
////                    progressBar.visibility = View.GONE
//                    Utils.pd.dismiss()
//                    showPdfFromFile(downloadedFile)
//                }
//
//                override fun onError(p0: com.downloader.Error?) {
//
//                    Toast.makeText(
//                        requireActivity(),
//                        "Error in downloading file : $p0",
//                        Toast.LENGTH_LONG
//                    ).show()
//
//                }
//
//            })
//    }
//
//    private fun showPdfFromFile(file: File) {
//        pdfView.fromFile(file)
//            .password(null)
//            .defaultPage(0)
//            .onPageChange(this)
//            .enableSwipe(true)
//            .swipeHorizontal(false)
//            .enableDoubletap(true)
//            .onPageError { page, _ ->
//                Toast.makeText(
//                    requireActivity(),
//                    "Error at page: $page", Toast.LENGTH_LONG
//                ).show()
//            }
//            .load()
//    }
//
//    override fun onPageChanged(page: Int, pageCount: Int) {
//        pageNumber = page + 1
//        tvpage.text = (pageNumber).toString() + "/" + pageCount.toString()
//        Log.d("Pageee", pageNumber.toString())
//        Log.d("Pageee" + "Count ", pageCount.toString())
//        //do what you want with the pageNumber
//    }
//
//    private inner class Callback : WebViewClient() {
//        override fun shouldOverrideUrlLoading(
//            view: WebView, url: String
//        ): Boolean {
//            return false
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        /* if (mBannerList.size == 1) {
//             banner.stopAutoCycle()
//         } else {
//             banner.startAutoCycle()
//         }*/
//    }
//
//    override fun showPromotions(response: CountryModel) {
//        Utils.pd.dismiss()
//        if (response.status == 200) {
//            mAdapter = PromotionaAdapter(response.data, requireActivity())
////            list.adapter = mAdapter
//        } else if (response.status == 402) {
//            Utils.tokenExpire(requireActivity())
//            Utils.showProgressDialog(requireActivity(), true)
//            mPresenter.loadPromotions()
//        } else {
//            if (sessionManager.languageselect == "en") {
//                showMessage(response.statusMessage)
//            } else {
//                showMessage(response.statusMsgArabic)
//            }
//        }
//    }
//
//    override fun showReloadButton(throwable: Throwable, errorBody: ErrorBody?, network: Boolean) {
//        Utils.pd.dismiss()
//        when {
//            network -> showDilaogBox(
//                getString(R.string.no_internet),
//                getString(R.string.generic_no_internet_desc)
//            )
////            errorBody != null -> showMessage(errorBody.toString())
////            throwable is SocketException -> showMessage(throwable.message!!)
////            else -> showMessage(throwable.message!!)
//        }
//    }
//
//    override fun onClick(v: View?) {
//        when (v?.id) {
//           /* R.id.ivleft_arrow -> {
//                onBackPressed()
//            }
//
//*/
//            R.id.ivshare -> {
//
//                // share pdf ....
//
////                val aName = intent.getStringExtra("myFile")
//                val aName = "myFile.pdf"
//                val shareIntent = Intent(Intent.ACTION_SEND)
//
//                /*  val uri = FileProvider.getUriForFile(
//                      this,
//                      this.getPackageName().toString() + ".provider",
//                      outputFile
//                  )*/
//
//                shareIntent.putExtra(
//                    Intent.EXTRA_STREAM, uriFromFile(
//                        requireActivity(),
//                        File(requireActivity().getExternalFilesDir(null)?.absolutePath.toString(), "$aName")
//                    )
//                )
//                shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//                shareIntent.type = "application/pdf"
//                startActivity(Intent.createChooser(shareIntent, "share.."))
//
//            }
//
//        }
//
//    }
//
//    fun uriFromFile(context: Context, file: File): Uri {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            return FileProvider.getUriForFile(
//                context,
//                BuildConfig.APPLICATION_ID + ".provider",
//                file
//            )
//        } else {
//            return Uri.fromFile(file)
//        }
//    }
//
//    private fun getSliderBanners() {
//        if (banner != null) {
//            banner.removeAllSliders()
//        }
//        for (i in mBannerList.indices) {
//            textSliderView = DefaultSliderView(requireActivity())
//            Log.e("===========", "" + mBannerList[i].replace("//", "/"))
//            textSliderView
//                .image(mBannerList[i].replace("//", "/").replace("https:/", "https://"))
//                .setScaleType(BaseSliderView.ScaleType.CenterInside)
//                .setOnSliderClickListener(this)
//            banner.addSlider(textSliderView)
//
//        }
//        // banner.moveNextPosition(true)
//        banner.movePrevPosition(false)
//        banner.setPresetTransformer(SliderLayout.Transformer.Accordion)
//        banner.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
//        banner.setCustomAnimation(DescriptionAnimation())
//        banner.pagerIndicator.setVisibility(View.VISIBLE)
//        banner.pagerIndicator
//            .setDefaultIndicatorColor(
//                ContextCompat.getColor(requireActivity(), R.color.color2),
//                ContextCompat.getColor(requireActivity(), R.color.gray)
//            )
//        banner.setDuration(3000)
//        banner.addOnPageChangeListener(this)
//
//        if (mBannerList.size == 1) {
//            banner.stopAutoCycle()
//        } else {
//            banner.startAutoCycle()
//        }
//    }
//
//    override fun showBanner(response: StatusBannerModel) {
//        Utils.pd.dismiss()
//        if (response.status == 200) {
//
//            mBannerList = response.data as MutableList<String>
//            getSliderBanners()
//        } else if (response.status == 402) {
//            Utils.tokenExpire(requireActivity())
//            Utils.showProgressDialog(requireActivity(), true)
//            mPresenter.loadBanner()
//        } else {
//            showMessage(response.statusMessage)
//        }
//    }
//
//    override fun onSliderClick(slider: BaseSliderView?) {
//
//    }
//
//    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//        //TODO("Not yet implemented")
//    }
//
//    override fun onPageSelected(position: Int) {
//        //TODO("Not yet implemented")
//    }
//
//    override fun onPageScrollStateChanged(state: Int) {
//        // TODO("Not yet implemented")
//    }
//}