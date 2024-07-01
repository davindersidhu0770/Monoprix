package com.production.monoprix.ui.loyalty


import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.daimajia.slider.library.Animations.DescriptionAnimation
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.daimajia.slider.library.Tricks.ViewPagerEx
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.model.*
import com.production.monoprix.mvp.BaseMvpFragment
import com.production.monoprix.room.AppDatabase
import com.production.monoprix.ui.home.HomeActivity
import com.production.monoprix.ui.home.HomeContractor
import com.production.monoprix.ui.home.HomeFragment
import com.production.monoprix.ui.home.HomePresenter
import com.production.monoprix.ui.imageview.ImageActivity
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

import kotlinx.android.synthetic.main.include_loylty.view.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class LoyaltyFragment : BaseMvpFragment<HomeContractor.View, HomeContractor.Presenter>(),
    HomeContractor.View,
    BaseSliderView.OnSliderClickListener,
    ViewPagerEx.OnPageChangeListener {

    lateinit var mView: View
    lateinit var tvmycredit: TextView
    lateinit var sessionManager: SessionManager
    var isClicked: Boolean = false
    override var mPresenter: HomeContractor.Presenter = HomePresenter()
    var mBannerList: MutableList<BannerModel> = java.util.ArrayList<BannerModel>()
    lateinit var textSliderView: DefaultSliderView
    var folder: String = ""
    var comingF: String = ""
    var promotionLink: String = ""
    public val WRITE_REQUEST_CODE = 300
    lateinit var pd: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.activity_loyalty, container, false)
        init()
        return mView
    }

    fun init() {
        sessionManager = SessionManager(requireActivity())
        tvmycredit = mView.findViewById(R.id.tvmycredit)

        if (getArguments() != null) {

            var args = getArguments()
            comingF = args?.getString("from", "").toString()

        }
        if (comingF.isNotEmpty()) {

            mView.rltop.visibility = View.VISIBLE
            requireActivity().include.visibility = View.GONE
            requireActivity().navigation.visibility = View.GONE
        } else {
            mView.rltop.visibility = View.GONE
            requireActivity().include.visibility = View.VISIBLE
            requireActivity().navigation.visibility = View.VISIBLE

        }

        if (sessionManager.cardno!!.isNotEmpty()) {
            getLoylty()
        } else {
            mView.c1.visibility = View.VISIBLE
        }

        if (sessionManager.userId.toString().isNotEmpty()) {

            var gender = ""
            if (sessionManager.gender.toString() == "Mr") {
                gender = context.resources.getString(R.string.mr)
            } else if (sessionManager.gender.toString() == "Ms") {
                gender = context.resources.getString(R.string.ms)

            } else if (sessionManager.gender.toString() == "Mrs") {
                gender = context.resources.getString(R.string.mrs)

            }
            mView.txt_name.text =
                getString(R.string.hello) + " " + gender + "\n" + sessionManager.name

        } else
            mView.txt_name.text = getString(R.string.hello1)

        /* if (sessionManager.userId.toString().isNotEmpty())

             mView.txt_name.text = getString(R.string.hello) + " " + sessionManager.name + "!"
         else
             mView.txt_name.text = getString(R.string.hello1)
 */
        mView.c1.visibility = View.VISIBLE
        mView.txt_no_loylty.visibility = View.GONE
        sessionManager.loyaltyAmount?.let {
            mView.txt_loyalty_amount.text = sessionManager.loyaltyAmount + " " +
                    context?.resources?.getString(R.string.Qatarcurrency)
        }
        /*slider api call*/
        showProgressDialogBanner(this.requireContext(), true)
        mPresenter.loadBanner()

/*
        if (sessionManager.loyaltyAmount!!.isNotEmpty()) {
            if (sessionManager.barcodePath != null && !sessionManager.barcodePath.equals("") && !sessionManager.barcodePath.equals(
                    "null"
                )
            ) {
                sessionManager.cardno?.let {
                    mView.txt_user_name.text = sessionManager.userName
//                  mView.txt_display_card.visibility = View.VISIBLE
                    mView.txt_barcode_no.text = sessionManager.cardno
                    Picasso.with(activity).load(sessionManager.barcodePath)
                        .into(mView.img_bar_code1)

                }
            }
        } else {
            mView.c1.visibility = View.GONE
            mView.txt_no_loylty.visibility = View.VISIBLE
            mView.tvmycredit.visibility = View.VISIBLE
        }
*/

        mView.tvbenefits.setOnClickListener {

            Utils.showProgressDialog(requireContext(), true)
            mPresenter.getBenefit()
        }

        mView.img_left_arrow.setOnClickListener {
//          activity!!.onBackPressed()
            //send it to home...
            val fragment = HomeFragment()
            if (isAdded) {
                addFragment(fragment)
            }
        }

        mView.tvpromotions.setOnClickListener {

            Utils.showProgressDialog(requireActivity(), true)
            mPresenter.getTermAndConditions("1")
            /*  var intent = Intent(requireActivity(), PromotionsActivity::class.java)
              intent.putExtra("PLink", promotionLink)
              startActivity(intent)*/
        }

        mView.txt_user_name.text = sessionManager.gender + " " + sessionManager.name

        mView.txt_display_card.setOnClickListener {
            /* if(isClicked){
                 isClicked = false*/
            /*  mView.r_bar_code.visibility = View.GONE
              mView.txt_user_name.visibility = View.GONE
              mView.txt_barcode_no.visibility = View.GONE
              mView.txt_st_card_no.visibility = View.GONE*/
            mView.c1.visibility = View.GONE
            mView.include_loylty.visibility = View.VISIBLE
            mView.img_cross1.visibility = View.VISIBLE
//            mView.txt_display_card.text = getString(R.string.displaymycard)
            /* }else{
                 isClicked = true
                 mView.txt_display_card.text = getString(R.string.hidemycard)
              *//*   mView.r_bar_code.visibility = View.VISIBLE
                mView.txt_user_name.visibility = View.VISIBLE
                mView.txt_barcode_no.visibility = View.VISIBLE
                mView.txt_st_card_no.visibility = View.VISIBLE*//*
            }*/
        }
        mView.img_cross1.setOnClickListener {
            mView.c1.visibility = View.VISIBLE
            mView.img_cross1.visibility = View.GONE
            mView.include_loylty.visibility = View.GONE
        }
        mView.txt_title.text = getString(R.string.myrewards)

        mView.lldisplaymycard.setOnClickListener {

/*
            if (sessionManager.loyaltyAmount?.toDouble()!! > 0) {
                mView.c1.visibility = View.GONE
                mView.include_loylty.visibility = View.VISIBLE
                mView.img_cross1.visibility = View.VISIBLE
            }
*/

            mView.c1.visibility = View.GONE
            mView.include_loylty.visibility = View.VISIBLE
            mView.img_cross1.visibility = View.VISIBLE

        }

    }

    private fun addFragment(fragment: Fragment) {
//      mView.content.removeAllViewsInLayout()
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.design_bottom_sheet_slide_in,
                R.anim.design_bottom_sheet_slide_out
            )
            ?.replace(R.id.content, fragment, fragment.javaClass.simpleName)
            ?.commit()
    }

    private fun getLoylty() {
        mView.progress.visibility = View.VISIBLE
        ApiManager.getLoyality(sessionManager.userId.toString().toInt())
            .subscribe(
                this::handleResponse, this::handleError
            )
    }

    private fun handleResponse(rsponse: LoylityModel) {
        mView.progress.visibility = View.GONE
        when {
            rsponse.status == 200 -> {
                if (rsponse.data != null) {
                    mView.c1.visibility = View.VISIBLE
                    mView.txt_no_loylty.visibility = View.GONE
                    mView.tvmycredit.visibility = View.GONE

                    if (rsponse.data.points != 0.0) {
                        mView.tvmycredit.visibility = View.VISIBLE
                        mView.txt_loyalty_amount.visibility = View.VISIBLE
                        try {
                            mView.txt_loyalty_amount.text =
                                Utils.numbersWithoutLocalization(
                                    rsponse.data.points
                                ) + " " + context.resources?.getString(R.string.Qatarcurrency)

                        } catch (e: java.lang.Exception) {

                        }
                    }

                    if (rsponse.data.barcodePath != null && !rsponse.data.barcodePath.equals("") && !rsponse.data.barcodePath.equals(
                            "null"
                        )
                    ) {
                        rsponse.data.barcode?.let {
                            mView.txt_user_name.text = rsponse.data.userName
//                            mView.txt_display_card.visibility = View.VISIBLE
                            mView.txt_barcode_no.text = rsponse.data.barcode
                            Picasso.with(activity).load(rsponse.data.barcodePath)
                                .into(mView.img_bar_code1)
                        }
                    }

                } else {
                    mView.c1.visibility = View.GONE
                    mView.txt_no_loylty.visibility = View.VISIBLE
                }
            }
            rsponse.status == 402 -> {
                Utils.tokenExpire(requireActivity())
                mView.progress.visibility = View.VISIBLE
                getLoylty()
            }
            else -> {
                mView.c1.visibility = View.GONE
                mView.txt_no_loylty.visibility = View.VISIBLE
            }
        }

    }

    fun showProgressDialogBanner(context: Context, isShow: Boolean) {
        if (!(context as Activity).isFinishing) {
            pd = Dialog(context)
            pd.setContentView(R.layout.dialog_progressbar)
            pd.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            pd.setCanceledOnTouchOutside(false)


        } else {
            if (pd.isShowing) {
                pd.dismiss()
            }
        }
        /* val handler = Handler()
         handler.postDelayed(Runnable { pd.dismiss() }, 10000)*/
    }

    private fun handleError(error: Throwable) {
        if (isAdded) {
            mView?.let {
                it.progress?.visibility = View.GONE
                Toast.makeText(
                    requireActivity(),
                    "Error ${error.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun showBanner(response: StatusBannerModel) {

        pd.dismiss()
        if (response.status == 200) {

            mBannerList = response.data as MutableList<BannerModel>

            for (i in 0 until mBannerList.size) {
                if (mBannerList.get(i).link.contains(".pdf")) {
                    for (i in 0 until mBannerList.size) {
                        if (mBannerList.get(i).link.contains(".pdf")) {
                            sessionManager.PROMOTION_LINK = mBannerList.get(i).link
                            break
                        }
                    }

                    break
                }
            }

            getSliderBanners()
            /*promotion api call*/
            /* Utils.showProgressDialogBanner(requireContext(), true)
             mPresenter.promotionOnline()*/
        } else if (response.status == 402) {
            Utils.tokenExpire(requireActivity())
            Utils.showProgressDialog(requireActivity(), true)
//          mPresenter.loadBanner()
        } else {
            showMessage(response.statusMessage)
        }
    }

    private fun getSliderBanners() {
        if (mView.banner != null) {
            mView.banner.removeAllSliders()
        }
        try {

            for (i in mBannerList.indices) {
                textSliderView = DefaultSliderView(requireActivity())
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
                            var intent = Intent(requireActivity(), ImageActivity::class.java)
                            intent.putParcelableArrayListExtra("LIST", ArrayList(mBannerList))
                            intent.putExtra("POSITION", i)
                            startActivity(intent)
//                        Toast.makeText(requireContext(),"Image : "+mBannerList[i].image,Toast.LENGTH_SHORT).show()
                        }
                    }
                mView.banner.addSlider(textSliderView)
            }

        } catch (e: Exception) {
            Log.e("", e.message.toString())
            e.printStackTrace()
        }
        // mView.banner.moveNextPosition(true)
        mView.banner.movePrevPosition(false)
        mView.banner.setPresetTransformer(SliderLayout.Transformer.Accordion)
        mView.banner.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        mView.banner.setCustomAnimation(DescriptionAnimation())
        mView.banner.pagerIndicator.setVisibility(View.VISIBLE)
        mView.banner.pagerIndicator
            .setDefaultIndicatorColor(
                ContextCompat.getColor(requireActivity(), R.color.color2),
                ContextCompat.getColor(requireActivity(), R.color.gray)
            )
        mView.banner.setDuration(3000)
        mView.banner.addOnPageChangeListener(this)

        if (mBannerList.size == 1) {
            mView.banner.stopAutoCycle()
        } else {
            mView.banner.startAutoCycle()
        }

    }

    fun download(file: String, folder_: String) {
        val directory = File(folder_)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val dir = File(folder_)
        var children = dir.list()
        /*for(item in children) {
            File(dir, item).delete();
        }*/
        if (EasyPermissions.hasPermissions(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) && EasyPermissions.hasPermissions(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
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

    private inner class DownloadFile : AsyncTask<String, String, String>() {

        private var progressDialog: ProgressDialog? = null
        private var fileName: String? = null

        override fun onPreExecute() {
            super.onPreExecute()
            this.progressDialog = ProgressDialog(requireActivity())
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
                requireContext(),
                message, Toast.LENGTH_LONG
            ).show()

            if (message.contains(resources.getString(R.string.downloaded_at))) {
                MediaScannerConnection.scanFile(
                    requireContext(),
                    arrayOf(folder + fileName!!), null
                ) { path, uri ->
                    Utils.openFile(requireContext(), File(path))
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

        Log.d("Response>>", response.toString())
        Utils.pd.dismiss()
        if (response.status == 200) {
            val i = Intent(requireActivity(), BenefitWebviewActivity::class.java)
//            val i = Intent(requireActivity(), WebviewActivity::class.java)
            if (sessionManager.languageselect == "en") {
                i.putExtra("description", response.data.benefit_Eng)

            } else {
                i.putExtra("description", response.data.benefit_Arabic)
            }
            i.putExtra("page_name", getString(R.string.benefits))
            startActivity(i)
        } else if (response.status == 402) {
            Utils.tokenExpire(requireActivity())
            Utils.showProgressDialog(requireActivity(), true)
            mPresenter.getBenefit()
        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    override fun setTerms(response: TermsAndConditionsModel) {

        Utils.pd.dismiss()
        if (response.status == 200) {
//            val i = Intent(requireActivity(), WebviewActivity::class.java)
            val i = Intent(requireActivity(), BenefitWebviewActivity::class.java)
            if (sessionManager.languageselect == "en") {
                i.putExtra("description", response.data.getLoyaltyProgram_T_C_Eng)

            } else {
                i.putExtra("description", response.data.getLoyaltyProgram_T_C_Arabic)
            }
            i.putExtra("page_name", getString(R.string.termsandconditions))
            startActivity(i)
        } else if (response.status == 402) {
            Utils.tokenExpire(requireActivity())
            Utils.showProgressDialog(requireActivity(), true)
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

    override fun showAccountDetails(response: StatusModel) {


    }

    override fun onSliderClick(slider: BaseSliderView?) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {


    }

    override fun onPageScrollStateChanged(state: Int) {


    }


}