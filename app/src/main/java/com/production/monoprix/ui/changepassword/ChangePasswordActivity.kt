package com.production.monoprix.ui.changepassword

import android.os.Bundle
import android.view.View
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.ChangePasswordModel
import com.production.monoprix.mvp.BaseMvpActivity
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.activity_changepassword.*
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.net.SocketException


class ChangePasswordActivity : BaseMvpActivity<ChangePasswordContractor.View, ChangePasswordContractor.Presenter>(),
    ChangePasswordContractor.View,
    View.OnClickListener {


    override var mPresenter: ChangePasswordContractor.Presenter = ChangePasswordPresentor()
    lateinit var sessionManager: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changepassword)
        init()
    }

    fun init() {
        sessionManager = SessionManager(this)
        /*onclick listener*/
        img_left_arrow.setOnClickListener(this)
        txt_update.setOnClickListener(this)
        /*title*/
        txt_title.text = getString(R.string.chnagepassword)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_left_arrow -> {
                onBackPressed()
            }
            R.id.txt_update -> {
                if (txt_cp.text.toString().isNotEmpty()) {
                    if (txt_np.text.toString().isNotEmpty()) {
                        if (txt_cnp.text.toString().isNotEmpty()) {

                            //For non empty strings check length of password must be minimum 6 digits.
                            if (txt_cp.text.toString().length>=6 &&
                                txt_np.text.toString().length>=6 &&
                                txt_cnp.text.toString().length>=6)  {

                                if (txt_np.text.toString() == txt_cnp.text.toString()) {
                                    Utils.showProgressDialog(this,true)
                                    mPresenter.loadChangePassword(
                                        sessionManager.userId.toString().toInt(),
                                        txt_np.text.toString(),
                                        txt_np.text.toString(),
                                        "",
                                        sessionManager.email.toString()
                                    )
                                } else {
                                    showMessage(getString(R.string.er_match))
                                }
                            }
                            else {
                                showMessage(getString(R.string.password_length_shouldbe_6digit))
                            }

                        } else {
                            showMessage(getString(R.string.er_cnp))
                        }

                    } else {
                        showMessage(getString(R.string.er_np))
                    }

                } else {
                    showMessage(getString(R.string.er_op))
                }
            }
        }
    }

    override fun showChangePassword(response: ChangePasswordModel) {
        Utils.pd.dismiss()
        when {
            response.status == 200 -> {
                if(sessionManager.languageselect == "en"){
                    showMessage(response.statusMessage)
                } else{
                    showMessage(response.statusMsgArabic)
                }
                sessionManager.password = ""
                finish()
            }
            response.status == 402 -> {
                Utils.tokenExpire(this)
                Utils.showProgressDialog(this,true)
                mPresenter.loadChangePassword(
                    sessionManager.userId.toString().toInt(),
                    txt_np.text.toString(),
                    txt_np.text.toString(),
                    "",
                    sessionManager.email.toString()
                )
            }
            else -> {
                if(sessionManager.languageselect == "en"){
                    showMessage(response.statusMessage)
                } else{
                    showMessage(response.statusMsgArabic)
                }
            }
        }
    }

    override fun showReloadButton(throwable: Throwable, errorBody: ErrorBody?, network: Boolean) {
        Utils.pd.dismiss()
        when {
            network -> showDilaogBox(getString(R.string.no_internet), getString(R.string.generic_no_internet_desc))
            errorBody != null -> showMessage(errorBody.toString())
            throwable is SocketException -> showMessage(throwable.message!!)
            else -> showMessage(throwable.message!!)
        }
    }

}
