package com.production.monoprix.ui.login

import android.annotation.SuppressLint
import android.app.Activity
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
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
import com.production.monoprix.ui.forgotpassword.ForgotPasswordActivity
import com.production.monoprix.ui.home.HomeActivity
import com.production.monoprix.ui.otp.OTPActivity
import com.production.monoprix.ui.signup.SignUpActivity
import com.production.monoprix.ui.webview.BenefitWebviewActivity
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import com.twitter.sdk.android.core.*
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.txt_email
import kotlinx.android.synthetic.main.dialog_login_alert.*
import kotlinx.android.synthetic.main.dialog_signup.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.SocketException
import java.util.concurrent.Executors


class LoginActivity : BaseMvpActivity<LoginContractor.View, LoginContractor.Presenter>(),
    LoginContractor.View,
    View.OnClickListener, AuthenticationListener {

    var loginAlertcustomDialog: Dialog? = null
    override var mPresenter: LoginContractor.Presenter = LoginPresenter()
    lateinit var sessionManager: SessionManager
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth
    val RC_SIGN_IN: Int = 1
    lateinit var id: String
    var fEmail: String? = ""
    var UserID: String? = ""
    lateinit var name: String
    var lName: String = ""
    lateinit var loginType: String
    var customDialog: Dialog? = null
    var countryCode: String = "+974"
    var mCountryId: String = "3"
    lateinit var accessToken: String
    private var isexception = false
    protected var mCountryList = ArrayList<String>()
    protected var mCList = ArrayList<Data>()
    var iAgree: Boolean = false
    var iConfirm: Boolean = false
    lateinit var gender: String
    var isLogin: Boolean = false
    private var authenticationDialog: AuthenticationDialog? = null
    lateinit var config: TwitterConfig
    var stayLoggedIn: Boolean = true
    var isFingerLogin: Boolean = false

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Twitter Kit (if not already done)
        val twitterConfig = TwitterConfig.Builder(this)
            .twitterAuthConfig(
                TwitterAuthConfig(
                    getString(R.string.twitter_api_key),
                    getString(R.string.twitter_secret_key)
                )
            )
            .debug(true) // Set to false in production
            .build()
        Twitter.initialize(twitterConfig)

        setContentView(R.layout.activity_login)
        init()
    }

    private fun init() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        sessionManager = SessionManager(this)
        /*onclick listener*/
        img_left_arrow.setOnClickListener(this)
        txt_signin.setOnClickListener(this)
        txt_signup.setOnClickListener(this)
        txt_forgot_password.setOnClickListener(this)
        img_google_login.setOnClickListener(this)
        img_facebook_login.setOnClickListener(this)
        img_insta.setOnClickListener(this)
        twitter_sign_in_button.setOnClickListener(this)
        signin_twitter.setOnClickListener(this)
        txt_stay_logged_in.setOnClickListener(this)
        var s = sessionManager.fcmToken.toString()
        /*title*/
        txt_title.text = getString(R.string.signin)
        FirebaseApp.initializeApp(this)
        configureGoogleSignIn()
        firebaseAuth = FirebaseAuth.getInstance()
        /*twitter sign in*/
        /*config = TwitterConfig.Builder(this)
             .logger(DefaultLogger(Log.DEBUG))

             .twitterAuthConfig(
                 TwitterAuthConfig(
                     getString(R.string.twitter_api_key),
                     getString(R.string.twitter_secret_key)
                 )
             )
             .debug(true)
             .build()
         Twitter.initialize(config)*/

        twitter_sign_in_button.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                handleTwitterSession(result!!.data)
            }

            override fun failure(exception: TwitterException) {
                var e = exception.toString()
                Log.d("19April: ", e.toString())
                showMessage(e)
            }
        }
        /*fingerprint*/
        if (sessionManager.fingerprint) {
            if (sessionManager.email!!.isNotEmpty() && sessionManager.password!!.isNotEmpty()) {
                fingerPrint()
            }
        }

        /*stay logged in or not*/
        if (sessionManager.stayLoggedIn) {
            txt_stay_logged_in.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_checkbox_on,
                0,
                0,
                0
            )
        } else {
            txt_stay_logged_in.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_checkbox_off,
                0,
                0,
                0
            )
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

    fun fingerPrint() {
        val executor = Executors.newSingleThreadExecutor()
        val activity: FragmentActivity = this // reference to activity
        val biometricPrompt =
            BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {

                @SuppressLint("RestrictedApi")
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {

                    } else {
                        runOnUiThread { showMessage(errString.toString()) }
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d("ciper", result.cryptoObject?.signature.toString())
                    runOnUiThread {
                        isFingerLogin = true
                        Utils.showProgressDialog(this@LoginActivity, true)
                        mPresenter.loadLogin(
                            sessionManager.email!!,
                            sessionManager.password!!,
                            sessionManager.deviceId.toString(),
                            sessionManager.fcmToken.toString(),
                            sessionManager.fcmToken.toString(),
                            Utils.getDeviceId(applicationContext).toString(),
                            isFingerLogin
                        )
                    }

                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    runOnUiThread {
                        showMessage(getString(R.string.validnotreconized))
                    }
                }
            })
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.authen))
            .setSubtitle(getString(R.string.finger))
            // .setDescription("Set the description to display")
            .setNegativeButtonText(getString(R.string.cancel))
            .build()
        biometricPrompt.authenticate(promptInfo)

    }


    fun LoginUser(response: StatusModel) {
        if (sessionManager.languageselect == "en") {
            showMessage(response.statusMessage)
        } else {
            showMessage(response.statusMsgArabic)
        }

        Log.d("8Dec:", "UserID LoginUser: " + response.data.userId)
//        Log.d("8Dec:", "UUID Login: "+ UUID.randomUUID().toString())

        sessionManager.userId = response.data.userId
        sessionManager.mobileNumber = response.data.mobileNumber
        sessionManager.name = response.data.userName
        sessionManager.email = response.data.userEmail
        sessionManager.countryCode = response.data.countryCode
        sessionManager.countryId = response.data.countryId
        sessionManager.gender = response.data.gender
        sessionManager.familyName = response.data.familyName
        sessionManager.password = response.data.password
        sessionManager.countryName = response.data.countryname

        val i = Intent(this, HomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        this.startActivity(i)
    }

    fun OTPVerify(response: StatusModel) {
        val intent = Intent(this, OTPActivity::class.java)
        intent.putExtra("emailId", response.data.userEmail)
        intent.putExtra("password", response.data.password)
        intent.putExtra("signUpUserid", response.data.userId)
        startActivity(intent)
    }

    override fun showLogin(response: StatusModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {
            var deviceId = response.data.deviceId
            UserID = response.data.userId

            Log.d("8Dec:", "deviceId showLogin: " + deviceId)

            if (deviceId == null) {
                deviceId = "";
            }

/*
            Toast.makeText(
                this,
                Utils.uniqueID,
                Toast.LENGTH_LONG
            ).show()
*/

            Log.d("8Dec:", "Utils.getDeviceId(this)): " + Utils.getDeviceId(this))

            if (deviceId.equals("") || deviceId.equals("null") || deviceId.equals(
                    Utils.getDeviceId(this)
                )
            ) {
                LoginUser(response)

            } else {
                if (response.data.isActive && !response.data.consumerid.equals("") && !response.data.consumerid.equals(
                        "0"
                    ) && !response.data.consumerid.equals("null")
                ) {
                    loginAlertDialog()
                } else {
                    LoginUser(response)
                }
            }


        } else if (response.status == 202) {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
            var deviceId = response.data.deviceId
            UserID = response.data.userId

            Log.d("8Dec:", "UserID status == 202: " + response.data.userId)

            if (deviceId == null) {
                deviceId = "";
            }
            if (deviceId.equals(Utils.getDeviceId(this))) {
                OTPVerify(response)
            } else {
                if (response.data.isActive && !response.data.consumerid.equals("") && !response.data.consumerid.equals(
                        "0"
                    ) && !response.data.consumerid.equals("null")
                ) {
                    loginAlertDialog()
                } else {
                    OTPVerify(response)
                }
            }
        } else if (response.status == 402) {
            Utils.tokenExpire(this)
            Utils.showProgressDialog(this, true)
            //  mPresenter.loadLogin(txt_email.text.toString(),txt_password.text.toString(),sessionManager.token.toString())
            mPresenter.loadLogin(
                txt_email.text.toString(),
                txt_password.text.toString(),
                sessionManager.deviceId.toString(),
                sessionManager.fcmToken.toString(),
                sessionManager.fcmToken.toString(),
                Utils.getDeviceId(applicationContext).toString(),
                isFingerLogin
            )
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

    override fun setTermsAndConditions(response: TermsAndConditionsModel) {

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

    override fun setPrivacy(response: PrivacyPolicyModel) {

    }

    override fun setFaq(response: FaqModel) {

    }

    override fun setHelp(response: HelpModel) {

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
            R.id.radioButton11 -> {
                customDialog!!.radioButton21.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
                customDialog!!.radioButton11.setTextColor(ContextCompat.getColor(this, R.color.red))
                customDialog!!.radioButton31.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
            }
            R.id.radioButton21 -> {
                customDialog!!.radioButton21.setTextColor(ContextCompat.getColor(this, R.color.red))
                customDialog!!.radioButton11.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
                customDialog!!.radioButton31.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
            }
            R.id.radioButton31 -> {
                customDialog!!.radioButton21.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
                customDialog!!.radioButton11.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
                customDialog!!.radioButton31.setTextColor(ContextCompat.getColor(this, R.color.red))
            }
            R.id.tv_register -> {
                loginAlertcustomDialog!!.dismiss()

                Log.d("8Dec:", "UserID tv_register: " + UserID)
                Log.d("8Dec:", "Device Id : " + Utils.getDeviceId(applicationContext).toString())
//              Log.d("8Dec:", "UUID tv_register: "+ UUID.randomUUID().toString())

                mPresenter.DeviceIDChangeRequestEmail(
                    UserID!!,
                    Utils.getDeviceId(applicationContext).toString()
                )
            }
            R.id.txt_cancel -> {
                loginAlertcustomDialog!!.dismiss()
            }
            R.id.txt_signin -> {
                if (txt_email.text.toString().isNotEmpty()) {
                    if (txt_email.text.toString()
                            .matches(("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").toRegex())
                    ) {
                        if (txt_password.text.toString().isNotEmpty()) {
                            if (txt_password.text.toString().length >= 6) {
                                isFingerLogin = false
                                Utils.showProgressDialog(this, true)
                                mPresenter.loadLogin(
                                    txt_email.text.toString(),
                                    txt_password.text.toString(),
                                    sessionManager.deviceId.toString(),
                                    sessionManager.fcmToken.toString(),
                                    sessionManager.fcmToken.toString(),
                                    Utils.getDeviceId(applicationContext).toString(),
                                    isFingerLogin
                                )
                            } else {
                                showMessage(R.string.minimumlen)
                            }
                        } else {
                            showMessage(getString(R.string.er_password))
                        }
                    } else {
                        showMessage(getString(R.string.er_inv_email))
                    }
                } else {
                    showMessage(getString(R.string.er_email))
                }
            }
            R.id.txt_signup -> {
                val i = Intent(this, SignUpActivity::class.java)
                i.putExtra("from_splash", true)
                startActivity(i)
            }
            R.id.txt_forgot_password -> {
                startActivity(Intent(this, ForgotPasswordActivity::class.java))
            }
            R.id.img_facebook_login -> {
                if (Utils.fLogin) {

                    LoginManager.getInstance().logOut()
                    Utils.fLogin = false
                }
                val i = Intent(this, FacebookActivity::class.java)
                startActivityForResult(i, 300)
            }

            R.id.sign_in_button -> {
                signIn()

            }


            R.id.img_google_login -> {
                /* signIn()*///4april

                signIn();
            }
            R.id.iagree_text1 -> {
                Utils.showProgressDialog(this, true)
//              mPresenter.getCMSDetails("1")
                mPresenter.getTermAndConditions("1")
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
            R.id.img_insta -> {
                authenticationDialog = AuthenticationDialog(this, this)
                authenticationDialog!!.setCancelable(true)
                authenticationDialog!!.show()

            }
            R.id.signin_twitter -> {
                twitter_sign_in_button!!.performClick()
            }
            R.id.txt_stay_logged_in -> {
                if (sessionManager.stayLoggedIn) {
                    sessionManager.stayLoggedIn = false
                    txt_stay_logged_in.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_checkbox_off,
                        0,
                        0,
                        0
                    )
                } else {
                    sessionManager.stayLoggedIn = true
                    txt_stay_logged_in.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_checkbox_on,
                        0,
                        0,
                        0
                    )
                }
            }
        }
    }

    private fun signIn() {
        /* val signInIntent: Intent = mGoogleSignInClient.signInIntent
         startActivityForResult(signInIntent, RC_SIGN_IN)*/
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)

        /*mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
              .requestEmail()
              .build()
          mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)*/
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("17Feb:", acct.toString())

        accessToken = acct.idToken!!
        id = acct.id!!
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
//              val user = firebaseAuth.getCurrentUser()
                val user = FirebaseAuth.getInstance().currentUser
                name = user!!.displayName!!
                try {
                    name = user.displayName!!.split(" ")[0]
                    lName = user.displayName!!.split(" ")[1]
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                fEmail = acct.email!!
                loginType = "3"
                isLogin = true
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

            } else {
                showMessage(getString(R.string.e_google_sign_in))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        /*  if (requestCode == RC_SIGN_IN) {
              // The Task returned from this call is always completed, no need to attach
              // a listener.
              val task = GoogleSignIn.getSignedInAccountFromIntent(data)
              handleSignInResult(task)
          }*/
        if (requestCode == RC_SIGN_IN) {


            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
                Log.d(
                    "4april:",
                    "Google Sign-In failed with exception: ${e.statusCode} ${e.localizedMessage}"
                )

                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == 300 && data != null) {
            loginType = "1"
            id = data.getStringExtra("id")!!
            name = data.getStringExtra("fname")!!
            lName = data.getStringExtra("lname")!!
            fEmail = data.getStringExtra("fEmail")

            Log.d("28dec", "fEmail Login : " + data.getStringExtra("fEmail"))

            accessToken = data.getStringExtra("accessToken")!!
            isLogin = true
            Utils.showProgressDialog(this, true)
            mPresenter.loadSocialLogin(
                "", name, lName, fEmail!!, 0, "", false, false,
                id, accessToken, true, loginType,
                sessionManager.fcmToken.toString(), Utils.getDeviceId(applicationContext).toString()
            )

        } else {
            twitter_sign_in_button.onActivityResult(requestCode, resultCode, data)
        }
    }


    fun SocialLogin(response: StatusModel) {
        if (isLogin) {
            if (customDialog != null) {
                customDialog!!.dismiss()
            }

            Log.d("8Dec:", "UserID SocialLogin " + response.data.userId)

            sessionManager.userId = response.data.userId
            sessionManager.mobileNumber = response.data.mobileNumber
            sessionManager.name = response.data.userName
            sessionManager.email = response.data.userEmail
            sessionManager.countryCode = response.data.countryCode
            sessionManager.countryId = response.data.countryId
            sessionManager.gender = response.data.gender
            sessionManager.familyName = response.data.familyName
            if (this.intent.getBooleanExtra("from_splash", false)) {
                val i = Intent(this, HomeActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                this.startActivity(i)
            } else {
                this.setResult(Activity.RESULT_OK)
                this.finish()
            }
            if (loginType == "3") {
                Utils.gLogin = true
            } else if (loginType == "1") {
                Utils.fLogin = true
            }

        } else {

            Log.d("8Dec:", "UserID OTPActivity " + response.data.userId)

            customDialog!!.dismiss()
            val intent = Intent(this, OTPActivity::class.java)
            intent.putExtra("mobile_no", response.data.mobileNumber)
            intent.putExtra("countrycode", response.data.countryCode)
            intent.putExtra("emailId", response.data.userEmail)
            intent.putExtra("userId", response.data.userId)
            intent.putExtra("name", response.data.userName)
            intent.putExtra("gender", response.data.gender)
            intent.putExtra("logintype", loginType)
            intent.putExtra("familyName", response.data.familyName)
            intent.putExtra("countryId", response.data.countryId)
            intent.putExtra("countryName", response.data.countryname)
            startActivity(intent)
        }
    }

    fun SocialLoginOTP(response: StatusModel) {
        Log.d("8Dec:", "UserID SocialLoginOTP: " + response.data.userId)

        val intent = Intent(this, OTPActivity::class.java)
        intent.putExtra("mobile_no", response.data.mobileNumber)
        intent.putExtra("countrycode", response.data.countryCode)
        intent.putExtra("emailId", response.data.userEmail)
        intent.putExtra("userId", response.data.userId)
        intent.putExtra("name", response.data.userName)
        intent.putExtra("gender", response.data.gender)
        intent.putExtra("logintype", loginType)
        intent.putExtra("familyName", response.data.familyName)
        intent.putExtra("countryId", response.data.countryId)
        intent.putExtra("countryName", response.data.countryname)
        startActivity(intent)
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

            if (deviceId.equals("") || deviceId.equals("null") || deviceId.equals(
                    Utils.getDeviceId(
                        this
                    )
                )
            ) {
                SocialLogin(response)

            } else {
                if (response.data.isActive && !response.data.consumerid.equals("") && !response.data.consumerid.equals(
                        "0"
                    ) && !response.data.consumerid.equals("null")
                ) {
                    loginAlertDialog()
                } else {
                    SocialLogin(response)
                }
            }
        } else if (response.status == 202) {
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
            if (deviceId.equals("") || deviceId.equals("null") || deviceId.equals(
                    Utils.getDeviceId(
                        this
                    )
                )
            ) {
                SocialLoginOTP(response)
            } else {
                if (response.data.isActive && !response.data.consumerid.equals("") && !response.data.consumerid.equals(
                        "0"
                    ) && !response.data.consumerid.equals("null")
                ) {
                    loginAlertDialog()
                } else {
                    SocialLogin(response)
                }
            }

        } else if (response.status == 404) {
            Utils.showProgressDialog(this, true)
            mPresenter.getCountries()

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

    override fun setCMSDetails(response: TermsAndConditionsModel) {

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
        customDialog!!.img_close.setOnClickListener(this)
        customDialog!!.radioButton11.setOnClickListener(this)
        customDialog!!.radioButton21.setOnClickListener(this)
        customDialog!!.radioButton31.setOnClickListener(this)
        customDialog!!.setCancelable(false)
        customDialog!!.setCanceledOnTouchOutside(false)
        customDialog!!.iagree_text1.setOnClickListener(this)

        val adapter = ArrayAdapter(this, R.layout.item_spiner, mCountryList)
        adapter.setDropDownViewResource(R.layout.item_drop_down_spinner)
        customDialog!!.spinner_dialog.adapter = adapter
        // Set an on item selected listener for spinner object
        customDialog!!.spinner_dialog.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (p2 != 0) {
                        mCountryId = mCList[p2 - 1].countryId
                        countryCode = mCList[p2 - 1].code.trim()
                    } else {
                        mCountryId = getString(R.string.er_countrycode)

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
        Utils.pd.dismiss()
        mCountryList.clear()
        if (response.status == 200) {
            mCountryList.add(getString(R.string.er_countrycode))
            mCList = response.data as ArrayList<Data>
            for (i in 0 until response.data.size) {
                mCountryList.addAll(setOf(response.data[i].code))
                /* mCountryId.addAll(setOf(response.data[i].countryId))*/
            }
            dialogSignup()
        } else if (response.status == 402) {
            Utils.tokenExpire(this)
            Utils.showProgressDialog(this, true)
            mPresenter.getCountries()
        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    override fun onSuccessfullyDeletionOfAccount(response: DelUserModel) {


    }

    override fun onTokenReceived(auth_token: String) {
        if (auth_token == null)
            return
        accessToken = auth_token
        sessionManager.instaToken = accessToken
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
            Log.e("response insta===", "${response}")
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
                        Utils.showProgressDialog(this@LoginActivity, true)
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

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.e("response", e.localizedMessage)
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
                } else {

                    val exception = task.exception
                    // Log or display the error message
                    // Log or display the error message
                    Log.d("ISSUE: ", "signInWithCredential:failure", exception)
                    Toast.makeText(
                        this, exception.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


}
