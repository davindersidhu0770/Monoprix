package com.production.monoprix.ui.signup

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.TwitterAuthProvider
import com.production.monoprix.MyApplication
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.*
import com.production.monoprix.mvp.BaseMvpActivity
import com.production.monoprix.ui.home.HomeActivity
import com.production.monoprix.ui.login.AuthenticationDialog
import com.production.monoprix.ui.login.AuthenticationListener
import com.production.monoprix.ui.login.FacebookActivity
import com.production.monoprix.ui.login.LoginActivity
import com.production.monoprix.ui.otp.OTPActivity
import com.production.monoprix.ui.webview.BenefitWebviewActivity
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import com.twitter.sdk.android.core.*
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_myaccount.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.ccp
import kotlinx.android.synthetic.main.activity_signup.img_insta
import kotlinx.android.synthetic.main.activity_signup.radioButton1
import kotlinx.android.synthetic.main.activity_signup.radioButton2
import kotlinx.android.synthetic.main.activity_signup.radioButton3
import kotlinx.android.synthetic.main.activity_signup.spinner
import kotlinx.android.synthetic.main.activity_signup.spinner_area
import kotlinx.android.synthetic.main.activity_signup.txt_email
import kotlinx.android.synthetic.main.activity_signup.txt_familyname
import kotlinx.android.synthetic.main.activity_signup.txt_name
import kotlinx.android.synthetic.main.activity_signup.txt_password
import kotlinx.android.synthetic.main.dialog_login_alert.*
import kotlinx.android.synthetic.main.dialog_signup.*
import kotlinx.android.synthetic.main.dialog_signup.txt_mobile_number
import kotlinx.android.synthetic.main.include_toolbar.*
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.SocketException
import java.text.SimpleDateFormat
import java.util.*


class SignUpActivity : BaseMvpActivity<SignUpContractor.View, SignUpContractor.Presenter>(),
    SignUpContractor.View,
    View.OnClickListener, AuthenticationListener {

    override var mPresenter: SignUpContractor.Presenter = SignUpPresenter()
    lateinit var sessionManager: SessionManager
    var countryCode: String = "+974"
    private var isexception = false
    var iAgree: Boolean = false
    var iConfirm: Boolean = false
    lateinit var gender: String
    var deviceID: String = ""
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth
    val RC_SIGN_IN: Int = 1
    lateinit var id: String
    var fEmail: String? = ""
    lateinit var name: String
    lateinit var loginType: String
    var customDialog: Dialog? = null
    lateinit var accessToken: String
    protected var mCountryList = ArrayList<String>()
    var mCountryId: String = "3"
    protected var mCList = ArrayList<Data>()
    var isLogin: Boolean = false
    var lName: String = ""
    private var authenticationDialog: AuthenticationDialog? = null
    lateinit var config: TwitterConfig
    var UserID: String? = ""
    var loginAlertcustomDialog: Dialog? = null
    lateinit var txt_dateofbirth: EditText
    var cal = Calendar.getInstance()
    lateinit var ivdrop: ImageView
    var area: String = ""
    protected var mAreaList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        init()
    }

    fun init() {
        ivdrop = findViewById(R.id.ivdrop)
        deviceID = Utils.getDeviceId(this).toString()
        sessionManager = SessionManager(this)
        txt_dateofbirth = findViewById(R.id.txt_dateofbirth)
        // create an OnDateSetListener
        addDataToAreaSpinner()

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                updateDateInView()
            }
        }

        txt_dateofbirth.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {

                val datePickerDialog = DatePickerDialog(
                    this@SignUpActivity,
                    dateSetListener, cal
                        .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                )

                //following line to restrict future date selection
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                datePickerDialog.show()
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

/*
                DatePickerDialog(
                    this@SignUpActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
*/

            }

        })

        img_left_arrow.setOnClickListener(this)
        txt_register.setOnClickListener(this)
        signin.setOnClickListener(this)
        txt_title.text = getString(R.string.registration)
        img_google.setOnClickListener(this)
        img_facebook.setOnClickListener(this)
        iagree_text.setOnClickListener(this)
        img_insta.setOnClickListener(this)
        twitter_sign_in_button1.setOnClickListener(this)
        signin_twitter1.setOnClickListener(this)
        radioButton1.setOnClickListener(this)
        radioButton2.setOnClickListener(this)
        radioButton3.setOnClickListener(this)
        /*country list*/

        if (Utils.isNetworkAvailable(this))
            mPresenter.getCountries()
        else
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show()

        /*google sign in*/
        FirebaseApp.initializeApp(this)
        configureGoogleSignIn()
        firebaseAuth = FirebaseAuth.getInstance()
        /*twitter sign in*/
        config = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))
            .twitterAuthConfig(
                TwitterAuthConfig(
                    getString(R.string.twitter_api_key),
                    getString(R.string.twitter_secret_key)
                )
            )
            .debug(true)
            .build()
        Twitter.initialize(config)

        twitter_sign_in_button1.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                handleTwitterSession(result!!.data)
            }

            override fun failure(exception: TwitterException) {

            }
        }

        ivdrop.setOnClickListener(View.OnClickListener {

            ccp.showCountryCodePickerDialog()
        })

    }

    private fun addDataToAreaSpinner() {

        /*mAreaList.add(resources.getString(R.string.selectarea))
        mAreaList.add(resources.getString(R.string.westbay))
        mAreaList.add(resources.getString(R.string.aijasra))
        mAreaList.add(resources.getString(R.string.thepearl))
        mAreaList.add(resources.getString(R.string.albaaye))
        mAreaList.add(resources.getString(R.string.oldairport))
        mAreaList.add(resources.getString(R.string.dohaport))
        mAreaList.add(resources.getString(R.string.lusail))
        mAreaList.add(resources.getString(R.string.diplomaticarea))*/

        mAreaList.add(resources.getString(R.string.selectarea))
        mAreaList.add(resources.getString(R.string.doha))
        mAreaList.add(resources.getString(R.string.al_rayan))
        mAreaList.add(resources.getString(R.string.al_daayen))
        mAreaList.add(resources.getString(R.string.umm_salal))
        mAreaList.add(resources.getString(R.string.al_shani))
        mAreaList.add(resources.getString(R.string.al_khor))
        mAreaList.add(resources.getString(R.string.al_shamal))
        mAreaList.add(resources.getString(R.string.al_wakra))

//        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.spinner_item, mAreaList)

        val adapter = ArrayAdapter(this, R.layout.item_spiner_white, mAreaList)
        spinner_area.adapter = adapter
        adapter.setDropDownViewResource(R.layout.item_drop_down_spinner)

        spinner_area.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                // Check if p0 is not null and has child views
                if (p0 != null && p0.childCount > 0) {

                    if (p2 != 0) {
                        // Assuming mAreaList is a list of Strings
                        area = mAreaList[p2].trim()
                        Log.d("3feb", area)
                    } else {
                        Log.d("3feb", getString(R.string.selectarea))
                        area = getString(R.string.selectarea)
                    }
                } else {
                    Log.e("3feb", "AdapterView or child views are null")
                }
            }
        }

    }

    private fun updateDateInView() {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        txt_dateofbirth.setText(sdf.format(cal.getTime()))

    }

    override fun showSignUp(response: StatusModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
            val intent = Intent(this, OTPActivity::class.java)
            intent.putExtra("mobile_no", et_mobile.text.toString())
            intent.putExtra("password", txt_password.text.toString())
            intent.putExtra("countrycode", countryCode)
            intent.putExtra("emailId", txt_email.text.toString())
            intent.putExtra("signUpUserid", response.data.userId)
            startActivity(intent)
        } else if (response.status == 402) {

            if (Utils.isNetworkAvailable(this)) {
                Utils.tokenExpire(this)
                Utils.showProgressDialog(this, true)

                mPresenter.loadSignUp(
                    gender,
                    txt_name.text.toString(),
                    txt_familyname.text.toString(),
                    txt_email.text.toString(),
                    mCountryId.toInt(),
                    et_mobile.text.toString(),
                    txt_password.text.toString(),
                    iConfirm,
                    iAgree, deviceID,
                    txt_dateofbirth.text.toString(),
                    ccp.selectedCountryName,
                    area
                )

            } else
                Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show()

        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    fun loginAlertDialog() {
        val inflater = layoutInflater as LayoutInflater
        val customView = inflater.inflate(R.layout.dialog_login_alert, null)
        loginAlertcustomDialog = Dialog(this, R.style.MyDialogTheme)
        loginAlertcustomDialog!!.setContentView(customView)

        loginAlertcustomDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        loginAlertcustomDialog!!.show()
        loginAlertcustomDialog!!.tv_register.setOnClickListener(this)
        loginAlertcustomDialog!!.txt_cancel.setOnClickListener(this)
        loginAlertcustomDialog!!.setCanceledOnTouchOutside(false)
        loginAlertcustomDialog!!.setCancelable(true)

    }

    override fun showSocialLogin(response: StatusModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }

            var deviceId = response.data.deviceId
            UserID = response.data.userId
            if (deviceId == null) {
                deviceId = "";
            }

            /*if (deviceId.equals("") || deviceId.equals("null") || deviceId.equals(
                    Utils.getDeviceId(
                        this
                    )
                )
            ) {*/
            if (isLogin) {
                sessionManager.userId = response.data.userId
                sessionManager.mobileNumber = response.data.mobileNumber
                sessionManager.name = response.data.userName
                sessionManager.email = response.data.userEmail
                sessionManager.countryCode = response.data.countryCode
                sessionManager.countryId = response.data.countryId
                sessionManager.gender = response.data.gender
                sessionManager.familyName = response.data.familyName
                if (this.intent.getBooleanExtra("from_splash", false)) {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    this.setResult(Activity.RESULT_OK)
                    this.finish()
                }
            } else {
                customDialog!!.dismiss()
                val intent = Intent(this, OTPActivity::class.java)
                intent.putExtra("mobile_no", response.data.mobileNumber)
                intent.putExtra("countrycode", response.data.countryCode)
                intent.putExtra("emailId", response.data.userEmail)
                intent.putExtra("userId", response.data.userId)
                intent.putExtra("name", response.data.userName)
                intent.putExtra("gender", response.data.gender)
                intent.putExtra("familyName", response.data.familyName)
                intent.putExtra("countryId", response.data.countryId)
                intent.putExtra("logintype", loginType)
                startActivity(intent)
            }

            /* }else{
                 loginAlertDialog()
             }*/


        } else if (response.status == 202) {
            val intent = Intent(this, OTPActivity::class.java)
            intent.putExtra("mobile_no", response.data.mobileNumber)
            intent.putExtra("countrycode", response.data.countryCode)
            intent.putExtra("emailId", response.data.userEmail)
            intent.putExtra("userId", response.data.userId)
            intent.putExtra("name", response.data.userName)
            intent.putExtra("gender", response.data.gender)
            intent.putExtra("familyName", response.data.familyName)
            intent.putExtra("countryId", response.data.countryId)
            intent.putExtra("logintype", loginType)
            startActivity(intent)
        } else if (response.status == 404) {
            dialogSignup()
        } else if (response.status == 402) {
            Utils.tokenExpire(this)
        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    override fun showDeviceIDChangeRequestEmail(response: DeviceUpdateStatusModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {
            if (sessionManager.languageselect == "en") {
                showMessage(resources.getString(R.string.email_send))
            } else {
                showMessage(resources.getString(R.string.email_send))
            }
        }
    }

    override fun setCMSDetails(response: TermsAndConditionsModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {
//            val i = Intent(this, WebviewActivity::class.java)
            val i = Intent(this, BenefitWebviewActivity::class.java)

            if (sessionManager.languageselect == "en") {
                i.putExtra("description", response.data.getLoyaltyProgram_T_C_Eng)

            } else {
                i.putExtra("description", response.data.getLoyaltyProgram_T_C_Arabic)
            }

            i.putExtra("page_name", getString(R.string.termsandconditions))
            startActivity(i)
        } else if (response.status == 402) {

            if (Utils.isNetworkAvailable(this)) {
                Utils.tokenExpire(this)
                Utils.showProgressDialog(this, true)
                mPresenter.getCMSDetails("1")
            } else
                Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show()

        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    override fun setTermsAndConditions(response: TermsAndConditionsModel) {

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

    override fun onBackPressed() {

        if (this.intent.getBooleanExtra("from_splash", false)) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
            super.onBackPressed()

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_left_arrow -> {
                onBackPressed()
            }
            R.id.radioButton1 -> {
//              radioButton2.setTextColor(ContextCompat.getColor(this,R.color.black))
                radioButton1.setTextColor(ContextCompat.getColor(this, R.color.white))
//              radioButton3.setTextColor(ContextCompat.getColor(this,R.color.black))
            }
            R.id.radioButton2 -> {
                radioButton2.setTextColor(ContextCompat.getColor(this, R.color.white))
//              radioButton1.setTextColor(ContextCompat.getColor(this,R.color.black))
//              radioButton3.setTextColor(ContextCompat.getColor(this,R.color.black))
            }
            R.id.radioButton3 -> {
//              radioButton2.setTextColor(ContextCompat.getColor(this,R.color.black))
//              radioButton1.setTextColor(ContextCompat.getColor(this,R.color.black))
                radioButton3.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
            R.id.radioButton11 -> {
//              customDialog!!.radioButton21.setTextColor(ContextCompat.getColor(this,R.color.black))
                customDialog!!.radioButton11.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
//                customDialog!!.radioButton31.setTextColor(ContextCompat.getColor(this,R.color.black))
            }
            R.id.radioButton21 -> {
                customDialog!!.radioButton21.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
//                customDialog!!.radioButton11.setTextColor(ContextCompat.getColor(this,R.color.black))
//                customDialog!!.radioButton31.setTextColor(ContextCompat.getColor(this,R.color.black))
            }

            R.id.radioButton31 -> {
//              customDialog!!.radioButton21.setTextColor(ContextCompat.getColor(this,R.color.black))
//              customDialog!!.radioButton11.setTextColor(ContextCompat.getColor(this,R.color.black))
                customDialog!!.radioButton31.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
            }

            R.id.tv_register -> {
                loginAlertcustomDialog!!.dismiss()

                if (Utils.isNetworkAvailable(this))
                    mPresenter.DeviceIDChangeRequestEmail(
                        UserID!!,
                        Utils.getDeviceId(applicationContext).toString()
                    )
                else
                    Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show()


            }
            R.id.txt_cancel -> {
                loginAlertcustomDialog!!.dismiss()
            }

            R.id.txt_register -> {

                Log.d("27Nov:", "txt_dateofbirth:" + txt_dateofbirth.text.toString())

                if (radioButton1.isChecked) {
                    gender = getString(R.string.mr)
                } else if (radioButton2.isChecked) {
                    gender = getString(R.string.mrs)
                } else if (radioButton3.isChecked) {
                    gender = getString(R.string.ms)
                }
                iAgree = cvagree.isChecked
                iConfirm = cvconfirm.isChecked
                if (radioButton1.isChecked || radioButton2.isChecked || radioButton3.isChecked) {
                    if (txt_name.text.toString().isNotEmpty()) {
                        if (txt_name.text.toString().matches(Utils.nameValidation.toRegex())) {
                            if (txt_familyname.text.toString().isNotEmpty()) {
                                if (txt_familyname.text.toString()
                                        .matches(Utils.nameValidation.toRegex())
                                ) {
                                    if (txt_email.text.toString().isNotEmpty()) {
                                        if (txt_email.text.toString()
                                                .matches(("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").toRegex())
                                        ) {

                                            /*  if (txt_dateofbirth.text.toString().isNotEmpty()) {

                                              } else {
                                                  showAlertMessage(
                                                      getString(R.string.er_dob),
                                                      true,
                                                      0
                                                  )
                                                  txt_dateofbirth.requestFocus()

                                              }*/

                                            if (et_mobile.text.toString()
                                                    .isNotEmpty()
                                            ) {
                                                if (isValidPhoneNumber(et_mobile.text.toString())) {
                                                    val status =
                                                        validateUsing_libphonenumber(
                                                            countryCode.trim(),
                                                            et_mobile.text.toString()
                                                        )
                                                    if (isexception) run {
                                                        isexception = false
                                                    } else {
                                                        if (!area.equals(getString(R.string.selectarea))) {

                                                            if (status) {

                                                                if (txt_password.text.toString()
                                                                        .isNotEmpty()
                                                                ) {
                                                                    if (txt_password.text.toString().length >= 6) {

                                                                        if (txt_confirm_password.text.toString()
                                                                                .isNotEmpty()
                                                                        ) {
                                                                            if (txt_password.text.toString() == txt_confirm_password.text.toString()) {
                                                                                if (iConfirm) {
                                                                                    if (iAgree) {

                                                                                        if (Utils.isNetworkAvailable(
                                                                                                this
                                                                                            )
                                                                                        ) {

                                                                                            Utils.showProgressDialog(
                                                                                                this,
                                                                                                true
                                                                                            )
                                                                                            mPresenter.loadSignUp(
                                                                                                gender,
                                                                                                txt_name.text.toString(),
                                                                                                txt_familyname.text.toString(),
                                                                                                txt_email.text.toString(),
                                                                                                mCountryId.toInt(),
                                                                                                et_mobile.text.toString(),
                                                                                                txt_password.text.toString(),
                                                                                                iConfirm,
                                                                                                iAgree,
                                                                                                deviceID,
                                                                                                txt_dateofbirth.text.toString(),
                                                                                                ccp.selectedCountryName,
                                                                                                area
                                                                                            )
                                                                                        } else
                                                                                            Toast.makeText(
                                                                                                this,
                                                                                                getString(
                                                                                                    R.string.no_internet
                                                                                                ),
                                                                                                Toast.LENGTH_LONG
                                                                                            ).show()


                                                                                    } else {
                                                                                        showAlertMessage(
                                                                                            getString(
                                                                                                R.string.er_agree
                                                                                            ),
                                                                                            false,
                                                                                            0
                                                                                        )
//                                                                                              showAlertMessage()
/*
                                                                                                showMessage(getString(R.string.er_agree))
*/

                                                                                    }
                                                                                } else {
                                                                                    showAlertMessage(
                                                                                        getString(
                                                                                            R.string.er_confirm
                                                                                        ),
                                                                                        false,
                                                                                        0
                                                                                    )
                                                                                    txt_confirm_password.requestFocus()
/*
                                                                                            showMessage(
                                                                                                getString(
                                                                                                    R.string.er_confirm
                                                                                                )
                                                                                            )
*/
                                                                                }
                                                                            } else {
                                                                                showAlertMessage(
                                                                                    getString(
                                                                                        R.string.et_up_password
                                                                                    ),
                                                                                    false,
                                                                                    0
                                                                                )
                                                                                txt_password.requestFocus()
/*
                                                                                        showMessage(
                                                                                            getString(
                                                                                                R.string.et_up_password
                                                                                            )
                                                                                        )
*/
                                                                            }
                                                                        } else {
                                                                            showAlertMessage(
                                                                                getString(R.string.er_con_password),
                                                                                true, 0
                                                                            )
                                                                            txt_confirm_password.requestFocus()
/*
                                                                                    showMessage(
                                                                                        getString(R.string.er_con_password)
                                                                                    )
*/
                                                                        }
                                                                    } else {
                                                                        showAlertMessage(
                                                                            getString(R.string.minimumlen),
                                                                            false, 0
                                                                        )
                                                                        txt_password.requestFocus()
                                                                        /*showMessage(R.string.minimumlen)*/
                                                                    }
                                                                } else {
                                                                    showAlertMessage(
                                                                        getString(R.string.er_password),
                                                                        true, 0
                                                                    )
                                                                    txt_password.requestFocus()
/*
                                                                            showMessage(getString(R.string.er_password))
*/
                                                                }

                                                            } else {
                                                                showAlertMessage(
                                                                    getString(R.string.er_inv_number),
                                                                    false, 0
                                                                )
                                                                et_mobile.requestFocus()
                                                                /*showMessage(getString(R.string.er_inv_number))*/
                                                            }
                                                        } else {
                                                            showAlertMessage(
                                                                getString(R.string.er_inv_area),
                                                                true, 0
                                                            )
                                                            txt_password.requestFocus()
                                                            /*showMessage(getString(R.string.er_inv_area))*/
                                                        }

                                                    }
                                                }

                                            } else {
                                                showAlertMessage(
                                                    getString(R.string.er_mobile),
                                                    true, 0
                                                )
                                                et_mobile.requestFocus()
                                                /*showMessage(getString(R.string.er_mobile))*/
                                            }


                                        } else {
                                            showAlertMessage(
                                                getString(R.string.er_inv_email),
                                                false, 0
                                            )
                                            txt_email.requestFocus()
                                            /*showMessage(getString(R.string.er_inv_email))*/
                                        }
                                    } else {
                                        showAlertMessage(getString(R.string.er_email), true, 0)
                                        txt_email.requestFocus()
/*
                                        showMessage(getString(R.string.er_email))
*/
                                    }
                                } else {
                                    showAlertMessage(getString(R.string.er_valid_name), false, 1)

                                    /*showMessage(getString(R.string.er_valid_name))*/
                                }
                            } else {
                                showAlertMessage(getString(R.string.er_familyname), true, 0)
                                txt_familyname.requestFocus()
                                /*showMessage(getString(R.string.er_familyname))*/
                            }
                        } else {
                            showAlertMessage(getString(R.string.er_valid_name), false, 0)
                            txt_name.requestFocus()
                            /*showMessage(getString(R.string.er_valid_name))*/
                        }
                    } else {
                        showAlertMessage(getString(R.string.er_name), true, 0)
                        txt_name.requestFocus()
                        /*showMessage(getString(R.string.er_name))*/
                    }
                } else {
                    showAlertMessage(getString(R.string.er_gender), false, 0)

                    /*showMessage(getString(R.string.er_gender))*/
                }
            }
            R.id.signin -> {
                val i = Intent(this, LoginActivity::class.java)
                i.putExtra("from_splash", true)
                startActivity(i)
            }
            R.id.img_google -> {
                signIn()
            }
            R.id.img_facebook -> {
                val i = Intent(this, FacebookActivity::class.java)
                startActivityForResult(i, 300)
            }
            R.id.txt_continue -> {
                if (customDialog!!.radioButton11.isChecked) {
                    gender = getString(R.string.mr)
                } else if (customDialog!!.radioButton21.isChecked) {
                    gender = getString(R.string.mrs)
                } else if (customDialog!!.radioButton31.isChecked) {
                    gender = getString(R.string.ms)
                }
                iAgree = customDialog!!.iagree1.isChecked
                iConfirm = customDialog!!.iconfirm1.isChecked
                if (customDialog!!.radioButton11.isChecked || customDialog!!.radioButton21.isChecked || customDialog!!.radioButton31.isChecked) {
                    if (customDialog!!.txt_email.text.toString().isNotEmpty()) {
                        if (customDialog!!.txt_email.text.toString()
                                .matches(("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").toRegex())
                        ) {

                            if (customDialog!!.txt_mobile_number.text.toString().isNotEmpty()) {
                                if (isValidPhoneNumber(customDialog!!.txt_mobile_number.text.toString())) {
                                    val status =
                                        validateUsing_libphonenumber(
                                            countryCode.trim(),
                                            customDialog!!.txt_mobile_number.text.toString()
                                        )

                                    if (isexception) run { isexception = false } else {
                                        if (status) {
                                            if (iConfirm) {
                                                if (iAgree) {

                                                    if (Utils.isNetworkAvailable(this)) {

                                                        isLogin = false
                                                        Utils.showProgressDialog(this, true)
                                                        mPresenter.loadSocialLogin(
                                                            gender,
                                                            name,
                                                            lName,
                                                            customDialog!!.txt_email.text.toString(),
                                                            mCountryId.toInt(),
                                                            customDialog!!.txt_mobile_number.text.toString(),
                                                            true,
                                                            true,
                                                            id,
                                                            accessToken,
                                                            false,
                                                            loginType,
                                                            sessionManager.fcmToken.toString(),
                                                            Utils.getDeviceId(applicationContext)
                                                                .toString()
                                                        )
                                                    } else
                                                        Toast.makeText(
                                                            this,
                                                            getString(R.string.no_internet),
                                                            Toast.LENGTH_LONG
                                                        ).show()


                                                } else {
                                                    showMessage(getString(R.string.er_agree))
                                                }
                                            } else {
                                                showMessage(getString(R.string.er_confirm))
                                            }
                                        } else {
                                            showMessage(getString(R.string.er_inv_number))
                                        }
                                    }
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
                    showMessage(getString(R.string.er_gender))
                }
            }
            R.id.img_close -> {
                customDialog!!.dismiss()
            }
            R.id.iagree_text -> {

                if (Utils.isNetworkAvailable(this)) {
                    Utils.showProgressDialog(this, true)
                    mPresenter.getCMSDetails("1")

                } else
                    Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show()


            }
            R.id.iagree_text1 -> {
                if (Utils.isNetworkAvailable(this)) {
                    Utils.showProgressDialog(this, true)
                    mPresenter.getCMSDetails("1")

                } else
                    Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show()

            }
            R.id.img_insta -> {
                authenticationDialog = AuthenticationDialog(this, this)
                authenticationDialog!!.setCancelable(true)
                authenticationDialog!!.show()

            }
            R.id.signin_twitter1 -> {
                twitter_sign_in_button1!!.performClick()
            }

        }
    }

    private fun showAlertMessage(alert: String, showAllFieldsReq: Boolean, count: Int) {

        var message: String
        if (showAllFieldsReq)
            message = resources.getString(R.string.allfieldsreq)
        else
            message = alert
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.app_name))
        builder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, id ->
                if (count == 1) {
                    txt_name.requestFocus()
                    /* showSoftKeyboard(txt_name)*/
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(
                        InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY
                    )
                }

                dialog.dismiss()
            }
        /*   .setNegativeButton(resources.getString(R.string.txt_no)) { dialog, id ->
               // Dismiss the dialog

           }*/
        val alert = builder.create()
        alert.show()
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

            // here is one more tricky issue
            // imm.showSoftInputMethod doesn't work well
            // and imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0) doesn't work well for all cases too
            imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        accessToken = acct.idToken!!
        id = acct.id!!
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = firebaseAuth.getCurrentUser()
                name = user!!.getDisplayName()!!
                try {
                    name = user.displayName!!.split(" ")[0]
                    lName = user.displayName!!.split(" ")[1]
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                fEmail = user.email!!
                loginType = "3"
                isLogin = true

                if (Utils.isNetworkAvailable(this)) {
                    Utils.showProgressDialog(this, true)
                    mPresenter.loadSocialLogin(
                        "",
                        name,
                        lName,
                        fEmail!!,
                        0,
                        "",
                        false,
                        false,
                        id,
                        accessToken,
                        true,
                        loginType,
                        sessionManager.fcmToken.toString(),
                        Utils.getDeviceId(applicationContext).toString()
                    )

                } else
                    Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show()


            } else {
                showMessage(getString(R.string.e_google_sign_in))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {

                Log.d("10Jan:", e.localizedMessage)

                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == 300 && data != null) {
            loginType = "1"
            id = data.getStringExtra("id")!!
            name = data.getStringExtra("fname")!!
            lName = data.getStringExtra("lname")!!

            Log.d("28dec", "fEmail" + data.getStringExtra("fEmail"))
            fEmail = data.getStringExtra("fEmail")
            accessToken = data.getStringExtra("accessToken")!!
            isLogin = true



            if (Utils.isNetworkAvailable(this)) {

                Utils.showProgressDialog(this, true)
                mPresenter.loadSocialLogin(
                    "", name, lName, fEmail!!, 0, "", false, false,
                    id, accessToken, true, loginType,
                    sessionManager.fcmToken.toString(),
                    Utils.getDeviceId(applicationContext).toString()
                )

            } else
                Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show()


        } else {
            twitter_sign_in_button1.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun dialogSignup() {
        val customView = layoutInflater.inflate(R.layout.dialog_signup, null)
        // Build the dialog
        customDialog = Dialog(this)
        customDialog!!.setContentView(customView)
        val window = customDialog?.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)
        customDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog!!.show()
        customDialog!!.txt_email.setText(fEmail.toString())
        customDialog!!.txt_email.isEnabled = customDialog!!.txt_email.text.toString().isEmpty()
        customDialog!!.txt_continue.setOnClickListener(this)
        customDialog!!.img_close.setOnClickListener(this)
        customDialog!!.iagree_text1.setOnClickListener(this)
        customDialog!!.radioButton11.setOnClickListener(this)
        customDialog!!.radioButton21.setOnClickListener(this)
        customDialog!!.radioButton31.setOnClickListener(this)
        if (sessionManager.languageselect == "en") {
            val styledText =
                "I have read &amp; agree to the <font color='blue'>terms &amp; conditions</font>."
            customDialog!!.iagree_text1.setText(
                Html.fromHtml(styledText),
                TextView.BufferType.SPANNABLE
            )
        } else {
            val styledText =
                "الرجاء التأكيد بأني إطلعت و وافقت على <font color='blue'>شروط الخدمة</font."
            customDialog!!.iagree_text1.setText(
                Html.fromHtml(styledText),
                TextView.BufferType.SPANNABLE
            )
        }

        val adapter = ArrayAdapter(this, R.layout.item_spiner, mCountryList)
        adapter.setDropDownViewResource(R.layout.item_drop_down_spinner)
        // Set the drop down view resource
        adapter.setDropDownViewResource(R.layout.item_drop_down_spinner)
        // Finally, data bind the spinner object with dapter
        customDialog!!.spinner_dialog.adapter = adapter;
        // Set an on item selected listener for spinner object
        customDialog!!.spinner_dialog.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (p2 != 0) {
//                        mCountryId = mCList[p2 - 1].countryId
                        countryCode = mCList[p2 - 1].code
                    } else {
//                        mCountryId = getString(R.string.scountrycode)

                    }
                }

            }

    }


    private fun isValidPhoneNumber(phoneNumber: CharSequence): Boolean {
        return if (!TextUtils.isEmpty(phoneNumber)) {
            Patterns.PHONE.matcher(phoneNumber).matches()
        } else false
    }

    private fun validateUsing_libphonenumber(countryCode: String, phNumber: String): Boolean {
        var phoneNumberUtil: PhoneNumberUtil? = null
        var isoCode: String? = null
        var phoneNumber: Phonenumber.PhoneNumber? = null
        try {
            phoneNumberUtil = PhoneNumberUtil.createInstance(this)
            isoCode = phoneNumberUtil!!.getRegionCodeForCountryCode(Integer.parseInt(countryCode))

        } catch (e: Exception) {
            isexception = true
            e.printStackTrace()
        }

        try {
            //phoneNumber = phoneNumberUtil.parse(phNumber, "IN");  //if you want to pass region code
            phoneNumber = phoneNumberUtil!!.parse(phNumber, isoCode)
            val isValid = phoneNumberUtil.isValidNumber(phoneNumber)
            if (isValid) {
                val internationalFormat =
                    phoneNumberUtil.format(
                        phoneNumber!!,
                        PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL
                    )
                return true
            } else {
                Toast.makeText(this, resources.getString(R.string.er_inv_number), Toast.LENGTH_LONG)
                    .show()
                return false
            }
        } catch (e: NumberParseException) {
            System.err.println(e)
            Toast.makeText(this, resources.getString(R.string.er_inv_number), Toast.LENGTH_LONG)
                .show()
        }


        return false
    }

    override fun showCountries(response: CountryModel) {
        mCountryList.clear()
        if (response.status == 200) {
            mCountryList.add(getString(R.string.scountrycode))
            mCList = response.data as ArrayList<Data>
            for (i in 0 until response.data.size) {
                mCountryList.addAll(setOf(response.data[i].code))
                /* mCountryId.addAll(setOf(response.data[i].countryId))*/
            }
            val adapter = ArrayAdapter(this, R.layout.item_spiner, mCountryList)
            spinner.adapter = adapter
//          spinner.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);


            adapter.setDropDownViewResource(R.layout.item_drop_down_spinner)
//            mCountryId = getString(R.string.scountrycode)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (p2 != 0) {
//                      mCountryId = mCList[p2 - 1].countryId
                        countryCode = mCList[p2 - 1].code.trim()
                    } else {
//                      mCountryId = getString(R.string.scountrycode)

                    }
                }

            }
        } else if (response.status == 402) {
            Utils.tokenExpire(this)

            if (Utils.isNetworkAvailable(this))
                mPresenter.getCountries()
            else
                Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show()


        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    override fun onTokenReceived(auth_token: String) {
        accessToken = auth_token
        getUserInfoByAccessToken(auth_token)
    }

    private fun getUserInfoByAccessToken(token: String) {
        RequestInstagramAPI().execute()
    }

    private inner class RequestInstagramAPI : AsyncTask<Void, String, String>() {

        override fun doInBackground(vararg params: Void): String? {
            val httpClient = DefaultHttpClient()
            val httpGet = HttpGet(resources.getString(R.string.get_user_info_url) + accessToken!!)
            try {
                val response = httpClient.execute(httpGet)
                val httpEntity = response.getEntity()
                return EntityUtils.toString(httpEntity)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(response: String?) {
            super.onPostExecute(response)
            if (response != null) {
                try {
                    val jsonObject = JSONObject(response)
                    Log.e("response", jsonObject.toString())
                    val jsonData = jsonObject.getJSONObject("data")
                    if (jsonData.has("id")) {
                        id = jsonData.getString("id")
                        name = jsonData.getString("full_name")
                        try {
                            name = jsonData.getString("full_name").split(" ")[0]
                            lName = jsonData.getString("full_name").split(" ")[1]
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        loginType = "4"
                        isLogin = true

                        if (Utils.isNetworkAvailable(this@SignUpActivity)) {

                            Utils.showProgressDialog(this@SignUpActivity, true)
                            mPresenter.loadSocialLogin(
                                "",
                                name,
                                lName,
                                fEmail!!,
                                0,
                                "",
                                false,
                                false,
                                id,
                                accessToken,
                                true,
                                loginType,
                                sessionManager.fcmToken.toString(),
                                Utils.getDeviceId(applicationContext).toString()
                            )
                        } else
                            Toast.makeText(
                                this@SignUpActivity,
                                getString(R.string.no_internet),
                                Toast.LENGTH_LONG
                            ).show()


                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            } else {
                val toast = Toast.makeText(applicationContext, "no response", Toast.LENGTH_LONG)
                toast.show()
            }
        }
    }

    private fun handleTwitterSession(session: TwitterSession) {
        val credential = TwitterAuthProvider.getCredential(
            session.authToken.token,
            session.authToken.secret
        )
        accessToken = session.authToken.token
        id = session.id.toString()
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    name = user!!.displayName!!
                    try {
                        name = user.displayName!!.split(" ")[0]
                        lName = user.displayName!!.split(" ")[1]
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (user.email != null) {
                        fEmail = user.email
                    } else {
                        fEmail = ""
                    }
                    loginType = "2"
                    isLogin = true
                    if (Utils.isNetworkAvailable(this)) {
                        Utils.showProgressDialog(this, true)
                        mPresenter.loadSocialLogin(
                            "",
                            name,
                            lName,
                            fEmail!!,
                            0,
                            "",
                            false,
                            false,
                            id,
                            accessToken,
                            true,
                            loginType,
                            sessionManager.fcmToken.toString(),
                            Utils.getDeviceId(applicationContext).toString()
                        )
                    } else
                        Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG)
                            .show()

                } else {
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

}