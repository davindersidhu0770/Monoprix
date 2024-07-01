package com.production.monoprix.ui.otp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.StatusModel
import com.production.monoprix.mvp.BaseMvpActivity
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.net.SocketException
import java.util.concurrent.TimeUnit
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.GoogleApiClient
import com.production.monoprix.model.StatusRegisterModel
import com.production.monoprix.ui.home.HomeActivity
import com.production.monoprix.ui.receiver.AppSignatureHelper

import com.production.monoprix.ui.receiver.MySMSBroadcastReceiver
import android.text.style.UnderlineSpan
import android.text.SpannableString
import com.production.monoprix.MyApplication
import com.production.monoprix.ui.barcodescanner.BarcodeScannerActivity
import kotlinx.android.synthetic.main.include_toolbar.view.*


class OTPActivity : BaseMvpActivity<OTPContractor.View, OTPContractor.Presenter>(),
    OTPContractor.View,
    View.OnClickListener, MySMSBroadcastReceiver.OTPReceiveListener {

    override var mPresenter: OTPContractor.Presenter = OTPPresenter()
    lateinit var sessionManager: SessionManager
    var mobileNo: String? = null
    var password: String? = null
    var userId: String? = null
    var newUserId: String? = null
    var name: String? = null
    var gender: String? = null
    var familyName: String? = null
    var loginType: String? = null
    var countryCode: String? = null
    var countryId: String? = null
    var myaccount: String? = null
    var countryName: String? = null
    var emailId: String? = null
    lateinit var str1: String
    lateinit var str2: String
    lateinit var str3: String
    lateinit var str4: String
    lateinit var str5: String
    lateinit var finalOtp: String
    lateinit var countDownTimer: CountDownTimer

    /*auto otp*/
    var mCredentialsApiClient: GoogleApiClient? = null
    private val RC_HINT = 2
    val smsBroadcast = MySMSBroadcastReceiver()
    var otpClicks: Int = 0

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        init()
    }

    fun init() {
        mCredentialsApiClient = GoogleApiClient.Builder(this)
            .addApi(Auth.CREDENTIALS_API)
            .build()

        requestHint()

        startSMSListener()

        smsBroadcast.initOTPListener(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)

        applicationContext.registerReceiver(smsBroadcast, intentFilter)
        //Used to generate hash signature
        AppSignatureHelper(applicationContext).appSignatures


        sessionManager = SessionManager(this)
        if (intent != null) {

            mobileNo = intent.getStringExtra("mobile_no")
            password = intent.getStringExtra("password")
            countryCode = intent.getStringExtra("countrycode")
            emailId = intent.getStringExtra("emailId")
            name = intent.getStringExtra("name")
            userId = intent.getStringExtra("userId")
            gender = intent.getStringExtra("gender")
            familyName = intent.getStringExtra("familyName")
            loginType = intent.getStringExtra("logintype")
            countryId = intent.getStringExtra("countryId")
            newUserId = intent.getStringExtra("signUpUserid")
            myaccount = intent.getStringExtra("myaccount")
            countryName = intent.getStringExtra("countryName")

        }

        /*onclick listener*/
        img_left_arrow.setOnClickListener(this)
        txt_resend_otp.setOnClickListener(this)
        txt_verify.setOnClickListener(this)
        /*title*/
        txt_title.text = getString(R.string.otpheader)
        //txt_resend_otp.setPaintFlags(txt_resend_otp.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        /*timer*/
        getTimer()
        /*otp edittext box*/
        et_otp_one.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
                val textlength1 = et_otp_one.getText().length

                if (textlength1 >= 1) {
                    et_otp_one.background =
                        ContextCompat.getDrawable(this@OTPActivity, R.drawable.oval_red_bg)
                    et_otp_one.setTextColor(ContextCompat.getColor(this@OTPActivity, R.color.white))
                    et_otp_two.requestFocus()
                } else {
                    et_otp_one.background =
                        ContextCompat.getDrawable(this@OTPActivity, R.drawable.oval_red_border)
                    et_otp_one.setTextColor(ContextCompat.getColor(this@OTPActivity, R.color.black))
                }
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub
            }
        })

        et_otp_two.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
                val textlength2 = et_otp_two.getText().length

                if (textlength2 >= 1) {
                    et_otp_two.background =
                        ContextCompat.getDrawable(this@OTPActivity, R.drawable.oval_red_bg)
                    et_otp_two.setTextColor(ContextCompat.getColor(this@OTPActivity, R.color.white))
                    et_otp_three.requestFocus()

                } else if (textlength2 == 0) {
                    et_otp_two.background =
                        ContextCompat.getDrawable(this@OTPActivity, R.drawable.oval_red_border)
                    et_otp_two.setTextColor(ContextCompat.getColor(this@OTPActivity, R.color.black))
                    et_otp_one.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub

            }
        })
        et_otp_three.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
                val textlength2 = et_otp_three.getText().length

                if (textlength2 >= 1) {
                    et_otp_three.background =
                        ContextCompat.getDrawable(this@OTPActivity, R.drawable.oval_red_bg)
                    et_otp_three.setTextColor(
                        ContextCompat.getColor(
                            this@OTPActivity,
                            R.color.white
                        )
                    )
                    et_otp_four.requestFocus()

                } else if (textlength2 == 0) {
                    et_otp_three.background =
                        ContextCompat.getDrawable(this@OTPActivity, R.drawable.oval_red_border)
                    et_otp_three.setTextColor(
                        ContextCompat.getColor(
                            this@OTPActivity,
                            R.color.black
                        )
                    )
                    et_otp_two.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub

            }
        })
        et_otp_four.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
                val textlength2 = et_otp_four.getText().length

                if (textlength2 >= 1) {
                    et_otp_four.background =
                        ContextCompat.getDrawable(this@OTPActivity, R.drawable.oval_red_bg)
                    et_otp_four.setTextColor(
                        ContextCompat.getColor(
                            this@OTPActivity,
                            R.color.white
                        )
                    )
                    et_otp_five.requestFocus()

                } else if (textlength2 == 0) {
                    et_otp_four.background =
                        ContextCompat.getDrawable(this@OTPActivity, R.drawable.oval_red_border)
                    et_otp_four.setTextColor(
                        ContextCompat.getColor(
                            this@OTPActivity,
                            R.color.black
                        )
                    )
                    et_otp_three.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub

            }
        })

        et_otp_five.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
                val textlength2 = et_otp_five.getText().length

                if (textlength2 >= 1) {
                    et_otp_five.background =
                        ContextCompat.getDrawable(this@OTPActivity, R.drawable.oval_red_bg)
                    et_otp_five.setTextColor(
                        ContextCompat.getColor(
                            this@OTPActivity,
                            R.color.white
                        )
                    )
                    et_otp_one.requestFocus()

                } else if (textlength2 == 0) {
                    et_otp_five.background =
                        ContextCompat.getDrawable(this@OTPActivity, R.drawable.oval_red_border)
                    et_otp_five.setTextColor(
                        ContextCompat.getColor(
                            this@OTPActivity,
                            R.color.black
                        )
                    )
                    et_otp_three.requestFocus()
                    et_otp_four.requestFocus()
                }
            }

        })
        // init broadcast receiver


    }

    private fun getTimer() {
        // timer changed to 5 mins from 1 min....
        countDownTimer = object : CountDownTimer(300000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hms = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(
                            millisUntilFinished
                        )
                    ),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                    )
                )
                /* lable_otp_time.visibility = View.GONE
                 lable_otp_time.text = hms*/
                txt_resend_otp.text = getString(R.string.fetchotp) + " " + hms
                txt_resend_otp.isEnabled = false
            }


            override fun onFinish() {
                /* lable_otp_time.visibility = View.GONE
                 txt_resend_otp.isEnabled = true*/
                txt_resend_otp.isEnabled = true
                if (sessionManager.languageselect == "en" || sessionManager.languageselect!!.isEmpty()) {
                    val content = SpannableString("Didn\'t received the OTP? Resend OTP")
                    content.setSpan(UnderlineSpan(), 25, content.length, 0)
                    txt_resend_otp.text = content
                } else {
                    val content = SpannableString("لم تستلم OTP؟إعادة إرسال  رمز التحقق")
                    content.setSpan(UnderlineSpan(), 25, content.length, 0)
                    txt_resend_otp.text = content
                }
                // txt_resend_otp.text = getString(R.string.dontreceive)
            }
        }.start()

    }


    override fun showVerifyOtp(response: StatusRegisterModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)

            }
            when {
                password != null -> {
                    Utils.showProgressDialog(this, true)
                    emailId?.let {
                        mPresenter.loadLogin(
                            it, password!!, sessionManager.deviceId.toString(),
                            sessionManager.fcmToken.toString(),
                            sessionManager.fcmToken.toString(),
                            Utils.getDeviceId(applicationContext).toString()
                        )
                    }
                }
                userId != null -> {
                    if (loginType == "2") {
                        Utils.gLogin = true
                    } else {
                        Utils.fLogin = true
                    }
                    sessionManager.userId = userId
                    sessionManager.mobileNumber = mobileNo
                    sessionManager.name = name
                    sessionManager.email = emailId
                    sessionManager.familyName = familyName
                    sessionManager.gender = gender
                    sessionManager.countryCode = countryCode
                    sessionManager.countryId = countryId
                    sessionManager.countryName = countryName

                    // showDialog here 27dec/2023

                    alertNewRegUser()

                }
                myaccount != null -> {
                    this.setResult(200)
                    this.finish()

                }
                else -> {
                    if (sessionManager.languageselect == "en") {
                        showMessage(response.statusMessage)
                    } else {
                        showMessage(response.statusMsgArabic)
                    }
                }
            }
        } else if (response.status == 402) {
            Utils.tokenExpire(this)
            Utils.showProgressDialog(this, true)
            if (myaccount == null) {
                if (newUserId == null) {
                    mPresenter.loadVerifyOtp(userId.toString(), finalOtp, "", "", false)
                } else {
                    mPresenter.loadVerifyOtp(newUserId!!, finalOtp, "", "", false)
                }
            } else {
                emailId?.let {
                    mPresenter.loadVerifyOtp(
                        newUserId!!, finalOtp,
                        it, mobileNo!!, true
                    )
                }
            }
        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    override fun showLogin(response: StatusModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
            sessionManager.userId = response.data.userId
            sessionManager.mobileNumber = response.data.mobileNumber
            sessionManager.name = response.data.userName
            sessionManager.email = response.data.userEmail
            sessionManager.countryCode = response.data.countryCode
            sessionManager.countryId = response.data.countryId
            sessionManager.gender = response.data.gender
            sessionManager.countryName = response.data.countryname

            //showDialog 27dec/2023
            alertNewRegUser()


        } else if (response.status == 402) {
            Utils.tokenExpire(this)
            Utils.showProgressDialog(this, true)
            emailId?.let {
                mPresenter.loadLogin(
                    it, password!!, sessionManager.deviceId.toString(),
                    sessionManager.fcmToken.toString(),
                    sessionManager.fcmToken.toString(),
                    Utils.getDeviceId(applicationContext).toString()
                )
            }
        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    override fun showResendOtp(response: StatusRegisterModel) {
        et_otp_one.setText("")
        et_otp_two.setText("")
        et_otp_three.setText("")
        et_otp_four.setText("")
        et_otp_five.setText("")
        et_otp_one.background = ContextCompat.getDrawable(this, R.drawable.oval_red_border)
        et_otp_two.background = ContextCompat.getDrawable(this, R.drawable.oval_red_border)
        et_otp_three.background = ContextCompat.getDrawable(this, R.drawable.oval_red_border)
        et_otp_four.background = ContextCompat.getDrawable(this, R.drawable.oval_red_border)
        et_otp_five.background = ContextCompat.getDrawable(this, R.drawable.oval_red_border)
        et_otp_one.requestFocus()
        Utils.pd.dismiss()
        getTimer()
        if (response.status == 204) {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        } else if (response.status == 402) {
            Utils.tokenExpire(this)
            Utils.showProgressDialog(this, true)
            mPresenter.loadResendOtp(newUserId!!.toInt())
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
            errorBody != null -> showMessage(errorBody.toString())
            throwable is SocketException -> showMessage(throwable.message!!)
            else -> showMessage(throwable.message!!)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_left_arrow -> {
                onBackPressed()
            }
            R.id.txt_verify -> {
                str1 = et_otp_one.text.toString().trim()
                str2 = et_otp_two.text.toString().trim()
                str3 = et_otp_three.text.toString().trim()
                str4 = et_otp_four.text.toString().trim()
                str5 = et_otp_five.text.toString().trim()
                finalOtp = str1 + str2 + str3 + str4 + str5
                if (finalOtp.length == 5) {
                    Utils.showProgressDialog(this, true)
                    if (myaccount == null) {
                        if (newUserId == null) {
                            mPresenter.loadVerifyOtp(userId.toString(), finalOtp, "", "", false)
                        } else {
                            mPresenter.loadVerifyOtp(newUserId!!, finalOtp, "", "", false)
                        }
                    } else {
                        emailId?.let {
                            mPresenter.loadVerifyOtp(
                                newUserId!!, finalOtp,
                                it, mobileNo!!, true
                            )
                        }
                    }
                } else {
                    showMessage(getString(R.string.er_otp))
                }
            }
            R.id.txt_resend_otp -> {
                otpClicks += 1
                if (otpClicks <= sessionManager.maxotptry!!.toInt()) {
                    Utils.showProgressDialog(this, true)
                    if (newUserId != null) {
                        mPresenter.loadResendOtp(newUserId!!.toInt())
                    } else {
                        mPresenter.loadResendOtp(userId!!.toInt())
                    }
                } else {
                    alert()
                }
            }

        }
    }

    fun alertNewRegUser() {
        val dialogBuilder = AlertDialog.Builder(this)
        // set message of alert dialog
        dialogBuilder.setMessage(getContext().getString(R.string.welcometotheMono))
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(
                getString(R.string.ok),
                DialogInterface.OnClickListener { dialog, id ->

                    dialog.dismiss()
                    val i = Intent(this, HomeActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)

                })
        // create dialog box
        val alert = dialogBuilder.create()
        // show alert dialog
        alert.show()
    }

    fun alert() {
        val dialogBuilder = AlertDialog.Builder(this)
        // set message of alert dialog
        dialogBuilder.setMessage(sessionManager.otpmaxmsg)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(
                getString(R.string.ok),
                DialogInterface.OnClickListener { dialog, id ->


                })
        // create dialog box
        val alert = dialogBuilder.create()
        // show alert dialog
        alert.show()
    }

    override fun onOTPReceived(otp: String) {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(smsBroadcast)
        Log.e("otp ", otp)
        if (otp.length == 5) {
            et_otp_one.setText(otp.substring(0, 1))
            et_otp_two.setText(otp.substring(1, 2))
            et_otp_three.setText(otp.substring(2, 3))
            et_otp_four.setText(otp.substring(3, 4))
            et_otp_five.setText(otp.substring(4, 5))
            et_otp_one.background = ContextCompat.getDrawable(this, R.drawable.oval_red_bg)
            et_otp_two.background = ContextCompat.getDrawable(this, R.drawable.oval_red_bg)
            et_otp_three.background = ContextCompat.getDrawable(this, R.drawable.oval_red_bg)
            et_otp_four.background = ContextCompat.getDrawable(this, R.drawable.oval_red_bg)
            et_otp_five.background = ContextCompat.getDrawable(this, R.drawable.oval_red_bg)
        }

    }

    override fun onOTPTimeOut() {
        //otpTxtView.text = "Timeout"
        //  Toast.makeText(this, " SMS retriever API Timeout", Toast.LENGTH_SHORT).show()
    }

    private fun startSMSListener() {

        SmsRetriever.getClient(this).startSmsRetriever()
            .addOnSuccessListener {
                //   otpTxtView.text = "Waiting for OTP"
                // Toast.makeText(this, "SMS Retriever starts", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                // otpTxtView.text = "Cannot Start SMS Retriever"
                // Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
            }
    }

    private fun requestHint() {

        val hintRequest = HintRequest.Builder().setPhoneNumberIdentifierSupported(true).build()
        val intent =
            mCredentialsApiClient?.let { Auth.CredentialsApi.getHintPickerIntent(it, hintRequest) }

        try {
            startIntentSenderForResult(intent?.intentSender, RC_HINT, null, 0, 0, 0)
        } catch (e: Exception) {
            Log.e("Error In getting Msg", e.message!!)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_HINT && resultCode == Activity.RESULT_OK) {
            val credential: Credential = data!!.getParcelableExtra(Credential.EXTRA_KEY)!!
            print("credential : $credential")
        }
    }
}


