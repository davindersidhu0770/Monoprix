/*
package com.production.monoprix.ui.loyalty


import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.daimajia.slider.library.Animations.DescriptionAnimation
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.daimajia.slider.library.Tricks.ViewPagerEx
import com.facebook.login.LoginManager
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.model.*
import com.production.monoprix.mvp.BaseMvpActivity
import com.production.monoprix.mvp.BaseMvpFragment
import com.production.monoprix.room.AppDatabase
import com.production.monoprix.ui.home.HomeActivity
import com.production.monoprix.ui.home.HomeContractor
import com.production.monoprix.ui.home.HomeFragment
import com.production.monoprix.ui.home.HomePresenter
import com.production.monoprix.ui.imageview.ImageActivity
import com.production.monoprix.ui.login.LoginContractor
import com.production.monoprix.ui.login.LoginPresenter
import com.production.monoprix.ui.promotions.PromotionsActivity
import com.production.monoprix.ui.webview.BenefitWebviewActivity
import com.production.monoprix.ui.webview.WebviewActivity
import com.production.monoprix.util.MyMediaController
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_homepage.view.*
import kotlinx.android.synthetic.main.activity_loyalty.*
import kotlinx.android.synthetic.main.activity_loyalty.view.*
import kotlinx.android.synthetic.main.activity_loyalty.view.banner
import kotlinx.android.synthetic.main.activity_loyalty.view.progress
import kotlinx.android.synthetic.main.activity_loyalty.view.txt_name
import kotlinx.android.synthetic.main.activity_main_layout.*
import kotlinx.android.synthetic.main.include_loylty.*

import kotlinx.android.synthetic.main.include_loylty.view.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class LoyaltyActivity : BaseMvpActivity<HomeContractor.View, HomeContractor.Presenter>(),
    HomeContractor.View,
    BaseSliderView.OnSliderClickListener,
    ViewPagerEx.OnPageChangeListener {

    lateinit var sessionManager: SessionManager
    var isClicked: Boolean = false
    override var mPresenter: HomeContractor.Presenter = HomePresenter()
    var mBannerList: MutableList<BannerModel> = java.util.ArrayList<BannerModel>()
    lateinit var textSliderView: DefaultSliderView
    var folder: String = ""
    var comingF: String = ""
    public val WRITE_REQUEST_CODE = 300

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_loyalty)
        init()

    }

    fun init() {
        sessionManager = SessionManager(this)

        if (getIntent() != null) {

            var args = getIntent()
            comingF = args?.getStringExtra("from").toString()

        }
        */
/* if (comingF.isNotEmpty()){

             rltop.visibility= View.VISIBLE
            this.include.visibility= View.GONE
             this.navigation.visibility= View.GONE
         }
         else{
             rltop.visibility = View.GONE
             this.include.visibility= View.VISIBLE
             this.navigation.visibility= View.VISIBLE

         }*//*


        if (sessionManager.cardno!!.isNotEmpty()) {
            getLoylty()
        } else {
            c1.visibility = View.VISIBLE
        }
        if (sessionManager.userId.toString().isNotEmpty())

            txt_name.text = getString(R.string.hello) + " " + sessionManager.name + "!"
        else
            txt_name.text = getString(R.string.hello1)

        c1.visibility = View.VISIBLE
        txt_no_loylty.visibility = View.GONE
        sessionManager.loyaltyAmount?.let {
            txt_loyalty_amount.text = sessionManager.loyaltyAmount + " " +
                    resources?.getString(R.string.Qatarcurrency)
        }
        */
/*slider api call*//*

        Utils.showProgressDialogBanner(this, true)
        mPresenter.loadBanner()

        if (sessionManager.loyaltyAmount!!.isNotEmpty()) {
            if (sessionManager.barcodePath != null && !sessionManager.barcodePath.equals("") && !sessionManager.barcodePath.equals(
                    "null"
                )
            ) {
                sessionManager.cardno?.let {
                    txt_user_name.text = sessionManager.userName
//                    mView.txt_display_card.visibility = View.VISIBLE
                    txt_barcode_no.text = sessionManager.cardno
                    Picasso.with(this).load(sessionManager.barcodePath)
                        .into(img_bar_code1)

                }
            }
        } else {
            c1.visibility = View.GONE
            txt_no_loylty.visibility = View.VISIBLE
        }
        img_left_arrow.setOnClickListener {

            onBackPressed()
        }

        tvbenefits.setOnClickListener {

            Utils.showProgressDialog(this, true)
            mPresenter.getBenefit()

        }

        tvpromotions.setOnClickListener {

            Utils.showProgressDialog(this, true)
            mPresenter.getTermAndConditions("1")
            */
/*var intent = Intent(this, PromotionsActivity::class.java)
            startActivity(intent)*//*

        }

        txt_user_name.text = sessionManager.name

        txt_display_card.setOnClickListener {
            */
/* if(isClicked){
                 isClicked = false*//*

            */
/*  mView.r_bar_code.visibility = View.GONE
              mView.txt_user_name.visibility = View.GONE
              mView.txt_barcode_no.visibility = View.GONE
              mView.txt_st_card_no.visibility = View.GONE*//*

            c1.visibility = View.GONE
            include_loylty.visibility = View.VISIBLE
            img_cross1.visibility = View.VISIBLE
//            mView.txt_display_card.text = getString(R.string.displaymycard)
            */
/* }else{
                 isClicked = true
                 mView.txt_display_card.text = getString(R.string.hidemycard)
              *//*
*/
/*   mView.r_bar_code.visibility = View.VISIBLE
                mView.txt_user_name.visibility = View.VISIBLE
                mView.txt_barcode_no.visibility = View.VISIBLE
                mView.txt_st_card_no.visibility = View.VISIBLE*//*
*/
/*
            }*//*

        }
        img_cross1.setOnClickListener {
            c1.visibility = View.VISIBLE
            img_cross1.visibility = View.GONE
            include_loylty.visibility = View.GONE
        }
//        txt_title.text = getString(R.string.myrewards)

    }

    fun addFragment(fragment: Fragment) {
//      mView.content.removeAllViewsInLayout()
        supportFragmentManager
            ?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.design_bottom_sheet_slide_in,
                R.anim.design_bottom_sheet_slide_out
            )
            ?.replace(R.id.content, fragment, fragment.javaClass.simpleName)
            ?.commit()
    }

    fun getLoylty() {
        progress.visibility = View.VISIBLE
        ApiManager.getLoyality(sessionManager.userId.toString().toInt())
            .subscribe(
                this::handleResponse, this::handleError
            )
    }

    fun handleResponse(rsponse: LoylityModel) {
        progress.visibility = View.GONE
        when {
            rsponse.status == 200 -> {
                if (rsponse.data != null) {
                    c1.visibility = View.VISIBLE
                    txt_no_loylty.visibility = View.GONE
                    rsponse.data.points?.let {
                        txt_loyalty_amount.text =
                            Utils.numbersWithoutLocalization(
                                rsponse.data.points
                            ) + " " + resources?.getString(R.string.Qatarcurrency)
                    }
                    if (rsponse.data.barcodePath != null && !rsponse.data.barcodePath.equals("") && !rsponse.data.barcodePath.equals(
                            "null"
                        )
                    ) {
                        rsponse.data.barcode?.let {
                            txt_user_name.text = rsponse.data.userName
//                            mView.txt_display_card.visibility = View.VISIBLE
                            txt_barcode_no.text = rsponse.data.barcode
                            Picasso.with(this).load(rsponse.data.barcodePath)
                                .into(img_bar_code1)
                        }
                    }

                } else {
                    c1.visibility = View.GONE
                    txt_no_loylty.visibility = View.VISIBLE
                }
            }
            rsponse.status == 402 -> {
                Utils.tokenExpire(this)
                progress.visibility = View.VISIBLE
                getLoylty()
            }
            else -> {
                c1.visibility = View.GONE
                txt_no_loylty.visibility = View.VISIBLE
            }
        }

    }

    fun handleError(error: Throwable) {
        progress.visibility = View.GONE
        Toast.makeText(this, "Error ${error.localizedMessage}", Toast.LENGTH_SHORT)
            .show()
    }

    override fun showBanner(response: StatusBannerModel) {

        Utils.pd.dismiss()
        if (response.status == 200) {

            mBannerList = response.data as MutableList<BannerModel>
            for (i in 0 until mBannerList.size) {
                if (mBannerList.get(i).link.contains(".pdf")) {
                    sessionManager.PROMOTION_LINK = mBannerList.get(i).link
                    break
                }
            }

            getSliderBanners()
            */
/*promotion api call*//*

            */
/* Utils.showProgressDialogBanner(requireContext(), true)
             mPresenter.promotionOnline()*//*

        } else if (response.status == 402) {
            Utils.tokenExpire(this)
            Utils.showProgressDialog(this, true)
//          mPresenter.loadBanner()
        } else {
            showMessage(response.statusMessage)
        }
    }

    private fun getSliderBanners() {
        if (banner != null) {
            banner.removeAllSliders()
        }
        try {

            for (i in mBannerList.indices) {
                textSliderView = DefaultSliderView(this)
                textSliderView
                    .image(mBannerList[i].image)
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener {
                        if (mBannerList[i].link != null && !mBannerList[i].link.equals("") && mBannerList[i].link.contains(
                                ".pdf"
                            )
                        ) {
//                            Toast.makeText(requireContext(),"PDF : "+mBannerList[i].link,Toast.LENGTH_SHORT).show()
                            download("" + mBannerList[i].link, folder)
                        } else {
                            var intent = Intent(this, ImageActivity::class.java)
                            intent.putParcelableArrayListExtra("LIST", ArrayList(mBannerList))
                            intent.putExtra("POSITION", i)
                            startActivity(intent)
//                        Toast.makeText(requireContext(),"Image : "+mBannerList[i].image,Toast.LENGTH_SHORT).show()
                        }
                    }
                banner.addSlider(textSliderView)
            }

        } catch (e: Exception) {
            Log.e("", e.message.toString())
            e.printStackTrace()
        }
        // mView.banner.moveNextPosition(true)
        banner.movePrevPosition(false)
        banner.setPresetTransformer(SliderLayout.Transformer.Accordion)
        banner.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        banner.setCustomAnimation(DescriptionAnimation())
        banner.pagerIndicator.setVisibility(View.VISIBLE)
        banner.pagerIndicator
            .setDefaultIndicatorColor(
                ContextCompat.getColor(this, R.color.color2),
                ContextCompat.getColor(this, R.color.gray)
            )
        banner.setDuration(3000)
        banner.addOnPageChangeListener(this)

        if (mBannerList.size == 1) {
            banner.stopAutoCycle()
        } else {
            banner.startAutoCycle()
        }

    }

    fun download(file: String, folder_: String) {
        val directory = File(folder_)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val dir = File(folder_)
        var children = dir.list()
        */
/*for(item in children) {
            File(dir, item).delete();
        }*//*

        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) && EasyPermissions.hasPermissions(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            DownloadFile().execute(file)
        } else {
            //If permission is not present request for the same.
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your file storage so that it can write files.",
                WRITE_REQUEST_CODE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    inner class DownloadFile : AsyncTask<String, String, String>() {

        private var progressDialog: ProgressDialog? = null
        private var fileName: String? = null

        override fun onPreExecute() {
            super.onPreExecute()
            this.progressDialog = ProgressDialog(this@LoyaltyActivity)
            this.progressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            this.progressDialog!!.setCancelable(false)
            this.progressDialog!!.show()
        }

        override fun doInBackground(vararg f_url: String): String {
            var count: Int
            try {
                val url = URL(f_url[0])
                val connection = url.openConnection()
                connection.connect()
                val lengthOfFile = connection.contentLength
                val input = BufferedInputStream(url.openStream(), 8192)
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1)
                val directory = File(folder)
                if (!directory.exists()) {
                    directory.mkdirs()
                }
                val output = FileOutputStream(folder + fileName!!)
                val data = ByteArray(1024)
                var total: Long = 0
                while (input.read(data).also { count = it } != -1) {
                    total += count.toLong()
                    publishProgress("" + (total * 100 / lengthOfFile).toInt())
                    Log.d("Download Activity", "Progress: " + (total * 100 / lengthOfFile).toInt())
                    output.write(data, 0, count)
                }
                output.flush()
                output.close()
                input.close()
                return resources.getString(R.string.downloaded_at) + " " + "$folder$fileName"
            } catch (e: Exception) {
                Log.e("Error: ", e.message!!)
            }

            return resources.getString(R.string.something_went_wrong)
        }

        override fun onProgressUpdate(vararg progress: String) {
            // setting progress percentage
            progressDialog!!.progress = Integer.parseInt(progress[0])
        }


        override fun onPostExecute(message: String) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog!!.dismiss()

            // Display File path after downloading
            Toast.makeText(
                this@LoyaltyActivity,
                message, Toast.LENGTH_LONG
            ).show()

            if (message.contains(resources.getString(R.string.downloaded_at))) {
                MediaScannerConnection.scanFile(
                    this@LoyaltyActivity,
                    arrayOf(folder + fileName!!), null
                ) { path, uri ->
                    Utils.openFile(this@LoyaltyActivity, File(path))
                }
            }
        }
    }

    override fun showPromotionOnline(response: PromotionOnlineModel) {
//        Utils.pd.dismiss()

    }

    override fun showLoyalty(response: LoylityModel) {


    }

    override fun setBenefit(response: BenefitModel) {

        Utils.pd.dismiss()
        if (response.status == 200) {
            val i = Intent(this, BenefitWebviewActivity::class.java)
//            val i = Intent(this, WebviewActivity::class.java)
            if (sessionManager.languageselect == "en") {
                i.putExtra("description", response.data.benefit_Eng)

            } else {
                i.putExtra("description", response.data.benefit_Eng)
            }
            i.putExtra("page_name", getString(R.string.benefits))
            startActivity(i)
        } else if (response.status == 402) {
            Utils.tokenExpire(this)
            Utils.showProgressDialog(this, true)
            mPresenter.getBenefit()
        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    override fun setTerms(response: TermsModel) {

        Utils.pd.dismiss()
        if (response.status == 200) {
//            val i = Intent(requireActivity(), WebviewActivity::class.java)
            val i = Intent(this, BenefitWebviewActivity::class.java)
            if (sessionManager.languageselect == "en") {
                i.putExtra("description", response.data.terms_Conditions_Eng)

            } else {
                i.putExtra("description", response.data.terms_Conditions_Arabic)
            }
            i.putExtra("page_name", getString(R.string.termsandconditions))
            startActivity(i)
        } else if (response.status == 402) {
            Utils.tokenExpire(this)
            Utils.showProgressDialog(this, true)
            mPresenter.getTermAndConditions("1")
        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    override fun showStoreLocation(response: StatusModel?) {

    }

    override fun showDeviceIDUpdated(response: StatusDiviceIDUpdateModel) {
    }

    override fun showReloadButton(throwable: Throwable, errorBody: ErrorBody?, network: Boolean) {


    }

    override fun onSliderClick(slider: BaseSliderView?) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {


    }

    override fun onPageScrollStateChanged(state: Int) {


    }


}*/
