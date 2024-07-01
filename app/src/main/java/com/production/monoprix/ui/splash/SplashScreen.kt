package com.production.monoprix.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.production.monoprix.R
import com.production.monoprix.StickyService
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.StatusModel
import com.production.monoprix.model.StatusTokenModel
import com.production.monoprix.mvp.BaseMvpActivity
import com.production.monoprix.ui.home.HomeActivity
import com.production.monoprix.ui.introscreen.IntroScreen
import com.production.monoprix.ui.signup.SignUpActivity
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.activity_splash.*
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class SplashScreen : BaseMvpActivity<SplashContractor.View, SplashContractor.Presenter>(),
    SplashContractor.View {

    override var mPresenter: SplashContractor.Presenter = SplashPresenter()
    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 10 //5 seconds
    lateinit var session: SessionManager
    lateinit var deviceId: String
    var mRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // Use your custom layout file

        val isAppInForeground =
            Utils.isAppInForeground(this) // Implement this function to check if the app is in the foreground
        if (isAppInForeground) {
            val stickyService = Intent(this, StickyService::class.java)
            startService(stickyService)
        }

        session = SessionManager(this)

        session.notiAsked = "no"

        deviceId =
            Settings.Secure.getString(this!!.getContentResolver(), Settings.Secure.ANDROID_ID)
        session.deviceId = deviceId
        /*token generation*/
        Utils.showProgressDialog(this, true)
        mPresenter.loadSalt()
        if (session.languageselect == "مع") {
            setLocale("ar")
        } else {
            setLocale("en")

        }

    }


    private fun setLocale(s: String) {
        val activityRes = resources
        val activityConf = activityRes.configuration
        val newLocale = Locale(s)
        activityConf.setLocale(newLocale)
        activityRes.updateConfiguration(activityConf, activityRes.displayMetrics)
        val applicationRes = applicationContext.resources
        val applicationConf = applicationRes.configuration
        applicationConf.setLocale(newLocale)
        applicationRes.updateConfiguration(
            applicationConf, applicationRes.displayMetrics
        )
    }

    override fun showToken(response: StatusTokenModel) {
        if (response.status == 200) {

//          img_splash.visibility = View.VISIBLE
//          include.visibility = View.GONE
            val c = Calendar.getInstance()
            System.out.println("Current time =&gt; " + c.time)
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val formattedDate = df.format(c.time)
            session.tokencreationtime = formattedDate.toString()
            session.token = response.data

            FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(
                    this
                ) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                    if (pendingDynamicLinkData != null) {
                        // Dynamic link is present, handle it
                        val deepLink = pendingDynamicLinkData.link
                        Log.d("24Jan", "deepLink $deepLink")
                        Utils.pd.dismiss()

                        if (session.userId.toString().isEmpty()) {
                            val i = Intent(this, SignUpActivity::class.java)
                            i.putExtra("from_splash", true)
                            startActivity(i)
                            finish()
                        } else {
                            val i = Intent(this, HomeActivity::class.java)
                            i.putExtra("from_splash", true)
                            startActivity(i)
                            finish()
                        }

                    } else {
                        // Dynamic link is not present
                        Log.d("24Jan", "No dynamic link found")
                        Utils.pd.dismiss()

                        if (session.freshInstall.isNullOrEmpty()) {
                            val i = Intent(this, IntroScreen::class.java)
                            i.putExtra("from_splash", true)
                            startActivity(i)
                            finish()
                        } else {

                            val i = Intent(this, HomeActivity::class.java)
                            i.putExtra("from_splash", true)
                            startActivity(i)
                            finish()
                        }


                    }
                }
                .addOnFailureListener(this) { e: java.lang.Exception ->
                    // Handle failure if needed
                    Log.d("24Jan", "Exception: " + e.message)
                    Utils.pd.dismiss()

                }


            mRunnable = Runnable {
/*
                if (!isFinishing) {
                    if (session.freshInstall.isNullOrEmpty()) {
                        val i = Intent(this, IntroScreen::class.java)
                        i.putExtra("from_splash", true)
                        startActivity(i)
                        finish()
                    } else {
                        val i = Intent(this, HomeActivity::class.java)
                        i.putExtra("from_splash", true)
                        startActivity(i)
                        finish()
                    }

                }
*/
            }
            //Navigate with delay
            mDelayHandler!!.postDelayed(mRunnable!!, SPLASH_DELAY)
        } else {
            Utils.pd.dismiss()

            // Toast.makeText(this, rsponse.statusMessage, Toast.LENGTH_SHORT).show()
            img_splash.visibility = View.GONE
            include.visibility = View.VISIBLE

        }
    }

    override fun showSalt(response: StatusModel) {
        if (response.status == 200) {
            Utils.pd.dismiss()

            hmac(response.data.key, response.data.key + Utils.mid)
            session.maxotptry = response.data.maxOTPTry
            session.otpmaxmsg = response.data.otpMaxMsg
        } else {
            Utils.pd.dismiss()

            if (session.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    private fun hmac(salt: String, text: String) {
        try {
            val secret = salt
            val message = text
            val sha256_HMAC = Mac.getInstance("HmacSHA256")
            val secret_key = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
            sha256_HMAC.init(secret_key)
            val hash = android.util.Base64.encodeToString(
                sha256_HMAC.doFinal(message.toByteArray()),
                android.util.Base64.NO_WRAP
            )
            val encodedItem = URLEncoder.encode(hash, "utf-8")
            println(encodedItem)

            Utils.showProgressDialog(this, true)
            mPresenter.loadToken(encodedItem)
            println(hash)
        } catch (e: Exception) {
            println("Error")
        }

    }


    override fun showReloadButton(throwable: Throwable, errorBody: ErrorBody?, network: Boolean) {
        showDilaogBoxx(
            getString(R.string.no_internet),
            getString(R.string.generic_no_internet_desc)
        )

    }

    fun showDilaogBoxx(
        title: String,
        desc: String
    ) {


        try {

//          Toast.makeText(this, desc, Toast.LENGTH_LONG).show()
            AlertDialog.Builder(applicationContext)
                .setTitle(title)
                .setMessage(desc)
                .setCancelable(false)
                .setPositiveButton(
                    "Ok"
                ) { dialogInterface, i ->
                    dialogInterface.dismiss()
                    finish()
                }
                .show()
            /*  AlertDialog.Builder(applicationContext)
                  .setTitle(title)
                  .setMessage(desc)
                  .setCancelable(false)
                  .setPositiveButton("Ok") { dialogInterface, i -> dialogInterface.dismiss() }
                  .show()*/

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    public override fun onDestroy() {
        if (mDelayHandler != null && mRunnable != null) {
            mDelayHandler!!.removeCallbacks(mRunnable!!)
        }
        super.onDestroy()
    }

}