package com.production.monoprix.ui.forgotpassword

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import com.production.monoprix.MyApplication
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.ChangePasswordModel
import com.production.monoprix.model.StatusRegisterModel
import com.production.monoprix.mvp.BaseMvpActivity
import com.production.monoprix.ui.login.LoginActivity
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.activity_forgotpassword_otp.*
import kotlinx.android.synthetic.main.activity_forgotpassword_otp.txt_verify
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.net.SocketException
import java.util.concurrent.TimeUnit

class PasswordChangeActivity :
    BaseMvpActivity<PasswordChangeContractor.View, PasswordChangeContractor.Presenter>(),
    PasswordChangeContractor.View,
    View.OnClickListener {


    override var mPresenter: PasswordChangeContractor.Presenter = PasswordChangePresenter()
    lateinit var phoneNumber: String
    lateinit var userId: String
    lateinit var sessionManager: SessionManager
    lateinit var countDownTimer: CountDownTimer
    var otpClicks: Int = 0


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword_otp)
        init()
    }

    fun init() {
        sessionManager = SessionManager(this)
        phoneNumber = intent.getStringExtra("phonenumber")!!
        userId = intent.getStringExtra("userId")!!
        txt_st_otp_on_no1.text = getString(R.string.otpon1) + " " + phoneNumber
        img_left_arrow.setOnClickListener(this)
        txt_dont.setOnClickListener(this)
        txt_verify.setOnClickListener(this)
        txt_st_otp_on_no1.setOnClickListener(this)
        /*title*/
        txt_title.text = getString(R.string.resetpass)
        /*timer*/
        getTimer()
    }

    private fun getTimer() {
        countDownTimer = object : CountDownTimer(60000, 1000) {
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
                txt_dont.text = getString(R.string.fetchotp) + " " + hms
                txt_dont.isEnabled = false
            }


            override fun onFinish() {
                /* lable_otp_time.visibility = View.GONE
                 txt_resend_otp.isEnabled = true*/
                txt_dont.isEnabled = true
                if (sessionManager.languageselect == "en" || sessionManager.languageselect!!.isEmpty()) {
                    val content = SpannableString("Didn\'t received the OTP? Resend OTP")
                    content.setSpan(UnderlineSpan(), 25, content.length, 0)
                    txt_dont.text = content
                } else {
                    val content = SpannableString("لم تستلم OTP؟إعادة إرسال  رمز التحقق")
                    content.setSpan(UnderlineSpan(), 25, content.length, 0)
                    txt_dont.text = content
                }

                // txt_resend_otp.text = getString(R.string.dontreceive)
            }
        }.start()

    }

    override fun showChangePassword(response: ChangePasswordModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {
            sessionManager.userId = response.data.toString()
            /* sessionManager.userId = response.data.userId.toString()
             sessionManager.email =  response.data.email.toString()*/
            sessionManager.password = ""
            val i = Intent(this, LoginActivity::class.java)
            i.putExtra("from_splash", true)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
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

    override fun showResendOtp(response: StatusRegisterModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
            getTimer()
        } else if (response.status == 402) {
            Utils.tokenExpire(this)
            Utils.showProgressDialog(this, true)
            mPresenter.loadResendOtp(userId.toInt())
        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_left_arrow -> {
                onBackPressed()
            }
            R.id.txt_dont -> {
                otpClicks += 1
                if (otpClicks <= sessionManager.maxotptry!!.toInt()) {
                    Utils.showProgressDialog(this, true)
                    mPresenter.loadResendOtp(userId.toInt())
                } else {
                    alert()
                }
            }
            R.id.txt_verify -> {
                if (txt_otp.text.toString().isNotEmpty()) {
                    if (txt_np.text.toString().isNotEmpty()) {
                        if (txt_cnp.text.toString().isNotEmpty()) {
                            if (txt_np.text.toString() == txt_cnp.text.toString()) {
                                Utils.showProgressDialog(this, true)
                                mPresenter.loadChangePassword(
                                    userId.toInt(),
                                    txt_otp.text.toString(),
                                    txt_cnp.text.toString()
                                )
                            } else {
                                showMessage(getString(R.string.er_match))
                            }

                        } else {
                            showMessage(getString(R.string.er_con_password))
                        }

                    } else {
                        showMessage(getString(R.string.er_np))
                    }
                } else {
                    showMessage(getString(R.string.er_otp))
                }
            }
            R.id.txt_st_otp_on_no1 -> {
                finish()
            }
        }
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


}