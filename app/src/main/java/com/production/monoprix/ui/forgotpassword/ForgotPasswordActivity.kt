package com.production.monoprix.ui.forgotpassword

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.production.monoprix.MyApplication
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.StatusModel
import com.production.monoprix.mvp.BaseMvpActivity
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.txt_email
import kotlinx.android.synthetic.main.include_toolbar.*
import java.net.SocketException


class ForgotPasswordActivity :
    BaseMvpActivity<ForgotPasswordContractor.View, ForgotPasswordContractor.Presenter>(),
    ForgotPasswordContractor.View,
    View.OnClickListener {

    override var mPresenter: ForgotPasswordContractor.Presenter = ForgotPasswordPresenter()
    var isPhoneNumber: Boolean = false
    lateinit var type: String
    lateinit var sessionManager: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        init()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }

    fun init() {
        sessionManager = SessionManager(this)
        /*onclick listener*/
        img_left_arrow.setOnClickListener(this)
        txt_reset.setOnClickListener(this)
        /*title*/
        txt_title.text = getString(R.string.forgotpass)
    }

    override fun showForgotPassword(response: StatusModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
            if (isPhoneNumber) {
                val i = Intent(this, PasswordChangeActivity::class.java)
                i.putExtra("phonenumber", response.data.mobile)
                i.putExtra("userId", response.data.userId)
                startActivity(i)
            } else {
                sessionManager.email = ""
                sessionManager.password = ""
                finish()
            }
        } else if (response.status == 402) {
            Utils.tokenExpire(this)
            Utils.showProgressDialog(this, true)
            mPresenter.loadForgotPassword(txt_email.text.toString(), type)
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
            R.id.txt_reset -> {
                isPhoneNumber = txt_email.text.toString().matches("[0-9]+".toRegex())
                if (txt_email.text.toString().isNotEmpty()) {
                    if (!isPhoneNumber) {
                        type = "email"
                        if (txt_email.text.toString()
                                .matches(("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").toRegex())
                        ) {
                            Utils.showProgressDialog(this, true)
                            mPresenter.loadForgotPassword(txt_email.text.toString(), type)
                        } else {
                            showMessage(getString(R.string.er_inv_email))
                        }
                    } else {
                        type = "mobile"

                        if (txt_email.text.toString().length < 8) {
                            showMessage(getString(R.string.phone_number_length))

                        } else {
                            Utils.showProgressDialog(this, true)
                            mPresenter.loadForgotPassword(txt_email.text.toString(), type)

                        }

                    }
                } else {
                    showMessage(getString(R.string.er_email_phone))
                }
            }
        }
    }


}