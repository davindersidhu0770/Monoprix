package com.production.monoprix.ui.contactus

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import com.production.monoprix.MyApplication
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.StatusModel
import com.production.monoprix.model.StatusTokenModel
import com.production.monoprix.mvp.BaseMvpActivity
import com.production.monoprix.ui.account.MyAccountActivity
import com.production.monoprix.ui.webview.ShowWebView
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_contactus.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.net.SocketException


class ContactUsActivity :
    BaseMvpActivity<ContactUsContractor.View, ContactUsContractor.Presenter>(),
    ContactUsContractor.View,
    View.OnClickListener {

    override var mPresenter: ContactUsContractor.Presenter = ContactUsPresenter()
    lateinit var sessionManager: SessionManager
    private val CALL_PERMISSION_REQUEST_CODE = 1000
    var mobileNumber: String = ""
    var fblink: String = ""
    var instalink: String = ""
    var twitterlink: String = ""
    var utubelink: String = ""


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactus)
        init()
    }

    fun init() {
        sessionManager = SessionManager(this)
        /*onclick listener*/
        img_left_arrow.setOnClickListener(this)
        txt_send.setOnClickListener(this)
//        txt_call.setOnClickListener(this)
        tvemail.setOnClickListener(this)
        txt_social.setOnClickListener(this)
        /*title*/
        txt_title.text = getString(R.string.contactus)
        txt_name.setText(sessionManager.name.toString())
        fb.setOnClickListener(this)
        insta.setOnClickListener(this)
        twitter.setOnClickListener(this)
        utube.setOnClickListener(this)
        txt_familyname.setText(sessionManager.familyName)
        //show email if user is logged in...
        if (sessionManager.userId.toString().isNotEmpty()) {
            txt_email.setText(sessionManager.email)
        } else
            txt_email.setText("")
        if (sessionManager.mobileNumber!!.isNotEmpty()) {
            val mobCon = "+".plus(sessionManager.countryCode.toString().trim()).plus(" ")
                .plus(sessionManager.mobileNumber)
//            txt_mobile_number.setText("+" + sessionManager.countryCode.toString().trim() + " " + sessionManager.mobileNumber)
            txt_mobile_number.setText(sessionManager.mobileNumber)
        }

//        txt_call.text = getString(R.string.contactus) + " info@monoprix.qa"
        Utils.showProgressDialog(this, true)
        mPresenter.getContactUs()
    }

    override fun showgetContactUs(response: StatusModel) {
        Utils.pd.dismiss()
        when {
            response.status == 200 -> {
//              txt_call.text = getString(R.string.contactus) + "\n" + response.data.contactUsNo
                mobileNumber = response.data.contactUsNo
                txt_social.text = response.data.contactUsFb
                Picasso.with(this).load(response.data.socialmedia[0].image_url).into(fb)
                Picasso.with(this).load(response.data.socialmedia[3].image_url).into(utube)
                Picasso.with(this).load(response.data.socialmedia[2].image_url).into(insta)
                Picasso.with(this).load(response.data.socialmedia[1].image_url).into(twitter)
                fblink = response.data.socialmedia[0].social_link
                instalink = response.data.socialmedia[2].social_link
                utubelink = response.data.socialmedia[3].social_link
                twitterlink = response.data.socialmedia[1].social_link


            }
            response.status == 402 -> {
                Utils.tokenExpire(this)
                Utils.showProgressDialog(this, true)
                mPresenter.getContactUs()
            }
            else -> {
                if (sessionManager.languageselect == "en") {
                    showMessage(response.statusMessage)
                } else {
                    showMessage(response.statusMsgArabic)
                }
            }
        }
    }

    override fun showSubmitContactUs(response: StatusTokenModel) {
        Utils.pd.dismiss()
        when {
            response.status == 200 -> {
                if (sessionManager.languageselect == "en") {
                    showMessage(response.statusMessage)
                } else {
                    showMessage(response.statusMsgArabic)
                }
                Utils.isContactUs = true
                finish()
            }
            response.status == 402 -> {
                Utils.tokenExpire(this)
                Utils.showProgressDialog(this, true)
                mPresenter.doContactUs(
                    txt_name.text.toString() + " " + txt_familyname.text.toString(),
                    txt_email.text.toString(),
                    txt_mobile_number.text.toString(),
                    txt_msg.text.toString()
                )
            }
            else -> showMessage(response.data)
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
            R.id.txt_call -> {
                doCall()
            }
            R.id.tvemail -> {
                sendAnEmail()
            }
            R.id.txt_social -> {
                val i = Intent(this, ShowWebView::class.java)
                i.putExtra("page_name", getString(R.string.contactus))
                i.putExtra("page_url", txt_social.text)
                startActivity(i)
            }
            R.id.fb -> {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(fblink))
                startActivity(i)
            }
            R.id.insta -> {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(instalink))
                startActivity(i)
            }
            R.id.twitter -> {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(twitterlink))
                startActivity(i)
            }
            R.id.utube -> {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(utubelink))
                startActivity(i)
            }
            R.id.txt_send -> {
                if (txt_name.text.toString().isNotEmpty()) {
                    if (txt_familyname.text.toString().isNotEmpty()) {
                        if (txt_email.text.toString().isNotEmpty()) {
                            if (txt_email.text.toString()
                                    .matches(("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").toRegex())
                            ) {
                                if (txt_mobile_number.text.toString().isNotEmpty()) {
                                    if (txt_mobile_number.text.toString().length > 7) {
                                        if (txt_msg.text.toString().isNotEmpty()) {
                                            Utils.showProgressDialog(this, true)
                                            mPresenter.doContactUs(
                                                txt_name.text.toString() + " " + txt_familyname.text.toString(),
                                                txt_email.text.toString(),
                                                txt_mobile_number.text.toString(),
                                                txt_msg.text.toString()
                                            )
                                        } else {
                                            showMessage(getString(R.string.er_msg))
                                        }
                                    } else {
                                        showMessage(getString(R.string.er_inv_number))
                                    }
                                } else {
                                    showMessage(getString(R.string.er_mobile))
                                }
                            } else {
                                showMessage(getString(R.string.er_inv_email))
                            }
                        } else {
                            showMessage(getString(R.string.er_email))
                        }
                    } else {
                        showMessage(getString(R.string.enter_lastname))
                    }
                } else {
                    showMessage(getString(R.string.er_name))
                }
            }
        }
    }

    private fun doCall() {
        if (mobileNumber.isNotEmpty()) {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CALL_PHONE), CALL_PERMISSION_REQUEST_CODE
                )
                return
            } else {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber))
                startActivity(intent)
            }
        }

    }

    private fun sendAnEmail() {
        val emailintent = Intent(Intent.ACTION_SEND)
        emailintent.type = "plain/text"
        emailintent.putExtra(Intent.EXTRA_EMAIL, arrayOf("info@monoprixqa.com"))
        emailintent.putExtra(Intent.EXTRA_SUBJECT, "")
        emailintent.putExtra(Intent.EXTRA_TEXT, "")
        startActivity(Intent.createChooser(emailintent, "Send mail..."))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1000 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doCall()
                }
                return
            }
        }
    }
}