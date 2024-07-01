package com.production.monoprix.ui.account

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.production.monoprix.MyApplication
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.CountryModel
import com.production.monoprix.model.Data
import com.production.monoprix.model.MyAccountModel
import com.production.monoprix.model.StatusModel
import com.production.monoprix.mvp.BaseMvpActivity
import com.production.monoprix.ui.changepassword.ChangePasswordActivity
import com.production.monoprix.ui.otp.OTPActivity
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber
import kotlinx.android.synthetic.main.activity_myaccount.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.net.SocketException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


class MyAccountActivity :
    BaseMvpActivity<MyActivityContractor.View, MyActivityContractor.Presenter>(),
    MyActivityContractor.View, View.OnClickListener {

    lateinit var sessionManager: SessionManager
    override var mPresenter: MyActivityContractor.Presenter = MyActivityPresenter()
    protected var mCountryList = ArrayList<String>()
    protected var mCList = ArrayList<Data>()
    protected var mAreaList = ArrayList<String>()
    var mCountryId: String = "3"
    lateinit var gender: String
    private var isexception = false
    var code: String = ""
    var area: String = ""
    lateinit var txt_dateofbirth: EditText
    lateinit var ivdrop: ImageView
    lateinit var rlm: RelativeLayout

    //    lateinit var ivdroparea: ImageView
    var cal = Calendar.getInstance()
    var strcomingfrom: String? = ""

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myaccount)
        sessionManager = SessionManager(this)
        init()
    }

    fun init() {
        txt_dateofbirth = findViewById(R.id.txt_dateofbirth)
        ivdrop = findViewById(R.id.ivdrop)
        rlm = findViewById(R.id.rlm)
//        ivdroparea = findViewById(R.id.ivdroparea)

        addDataToAreaSpinner()

        txt_title.text = getString(R.string.myaccount)
        /*onclicklistener*/
        img_left_arrow.setOnClickListener(this)
        txt_update.setOnClickListener(this)
        relative_change_pass.setOnClickListener(this)
        image_switch.setOnClickListener(this)
        radioButton1.setOnClickListener(this)
        radioButton2.setOnClickListener(this)
        radioButton3.setOnClickListener(this)
        /*Account details*/
        Utils.showProgressDialog(this, true)
        if (!sessionManager.userId.isNullOrBlank())
            mPresenter.getAccountDetails(sessionManager.userId.toString().toInt())

        if (!sessionManager.name.isNullOrEmpty())
            txt_name.setText(sessionManager.name.toString())
        if (!sessionManager.familyName.isNullOrEmpty())
            txt_familyname.setText(sessionManager.familyName.toString())
        if (!sessionManager.email.isNullOrEmpty())
            txt_email.setText(sessionManager.email.toString())
        if (!sessionManager.mobileNumber.isNullOrEmpty())
            txt_mobile_number.setText(sessionManager.mobileNumber.toString())
        if (!sessionManager.cardno.isNullOrEmpty())
            txt_loyality_number.setText(sessionManager.cardno.toString())
        /* if (sessionManager.gender.toString() == "Mr") {
             radioButton2.isChecked = true
             radioButton2.setTextColor(ContextCompat.getColor(this, R.color.black))
         } else if (sessionManager.gender.toString() == "Ms") {
             radioButton3.isChecked = true
             radioButton3.setTextColor(ContextCompat.getColor(this, R.color.black))
         } else if (sessionManager.gender.toString() == "Mrs") {
             radioButton1.isChecked = true
             radioButton1.setTextColor(ContextCompat.getColor(this, R.color.black))
         }*/
        if (!sessionManager.countryCode.isNullOrEmpty())
            code = sessionManager.countryCode.toString()
        /*get country*/
        mPresenter.getCountries()

        if (!intent.getStringExtra("comingfrom").isNullOrEmpty())
            strcomingfrom = intent.getStringExtra("comingfrom")

        spinner_area.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                area = mAreaList[p2].trim()
                Log.d("3feb", area)

            }

        }

        if (sessionManager.fingerprint) {
            /*image_switch.setImageResource(R.drawable.ic_switch)*/
            image_switch.setChecked(true);

        } else {
            image_switch.setChecked(false);

            /*image_switch.setImageResource(R.drawable.ic_switch_gray)*/
        }

        // create an OnDateSetListener
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
                    this@MyAccountActivity,
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

        tvcountry.setOnClickListener(View.OnClickListener {
            ccp.showCountryCodePickerDialog()

        })

        rlm.setOnClickListener(View.OnClickListener {
            ccp.showCountryCodePickerDialog()

        })

        ivdrop.setOnClickListener(View.OnClickListener {

            ccp.showCountryCodePickerDialog()
        })

        ccp.setOnCountryChangeListener { selectedCountry ->
/*
            Toast.makeText(
                getContext(),
                "Updated " + selectedCountry.name,
                Toast.LENGTH_SHORT
            ).show()
*/

            tvcountry.setText(selectedCountry.name)
        }


        /*  ivdroparea.setOnClickListener(View.OnClickListener {
              spinner_area.performClick()
          })*/
    }

    private fun showAlertMessage() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.app_name))
        builder.setMessage(resources.getString(R.string.please_fill_allthedetails))
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, id ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }

    private fun addDataToAreaSpinner() {

//      mAreaList.add(resources.getString(R.string.selectarea))
        mAreaList.add(resources.getString(R.string.doha))
        mAreaList.add(resources.getString(R.string.al_rayan))
        mAreaList.add(resources.getString(R.string.al_daayen))
        mAreaList.add(resources.getString(R.string.umm_salal))
        mAreaList.add(resources.getString(R.string.al_shani))
        mAreaList.add(resources.getString(R.string.al_khor))
        mAreaList.add(resources.getString(R.string.al_shamal))
        mAreaList.add(resources.getString(R.string.al_wakra))
        val adapter = ArrayAdapter(this, R.layout.item_spiner_black, mAreaList)
        spinner_area.adapter = adapter
        adapter.setDropDownViewResource(R.layout.item_drop_down_spinner)
    }

    private fun updateDateInView() {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        txt_dateofbirth.setText(sdf.format(cal.getTime()))
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
                        runOnUiThread { //change me
                            if (sessionManager.fingerprint) {
                                image_switch.setChecked(true);

/*
                                image_switch.setImageResource(R.drawable.ic_switch)
*/
                            } else {
                                image_switch.setChecked(false);

/*
                                image_switch.setImageResource(R.drawable.ic_switch_gray)
*/
                            }
                            showMessage(errString.toString())
                        }

                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d("ciper", result.cryptoObject?.signature.toString())
                    runOnUiThread {
                        sessionManager.fingerprint = !sessionManager.fingerprint //change me
                        if (sessionManager.fingerprint) {
                            image_switch.setChecked(true);

/*
                            image_switch.setImageResource(R.drawable.ic_switch)
*/
                        } else {
                            image_switch.setChecked(false);

/*
                            image_switch.setImageResource(R.drawable.ic_switch_gray)
*/
                        }
                    }

                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    runOnUiThread {
                        showMessage(getString(R.string.validnotreconized))//change me
                        if (sessionManager.fingerprint) {
                            image_switch.setChecked(true);

/*
                            image_switch.setImageResource(R.drawable.ic_switch)
*/
                        } else {
                            image_switch.setChecked(false);

/*
                            image_switch.setImageResource(R.drawable.ic_switch_gray)
*/
                        }
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

    override fun onBackPressed() {

        if (strcomingfrom.equals("home")) {

            // just keep the user on this screen
        } else
            super.onBackPressed()

    }

    override fun onResume() {
        super.onResume()
        if (strcomingfrom.equals("home")) {

            //show dialog here...
            showAlertMessage();
            img_left_arrow.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_left_arrow -> {
                onBackPressed()
            }
            R.id.radioButton1 -> {
                radioButton2.setTextColor(ContextCompat.getColor(this, R.color.black))
                radioButton1.setTextColor(ContextCompat.getColor(this, R.color.black))
                radioButton3.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
            R.id.radioButton2 -> {
                radioButton2.setTextColor(ContextCompat.getColor(this, R.color.black))
                radioButton1.setTextColor(ContextCompat.getColor(this, R.color.black))
                radioButton3.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
            R.id.radioButton3 -> {
                radioButton2.setTextColor(ContextCompat.getColor(this, R.color.black))
                radioButton1.setTextColor(ContextCompat.getColor(this, R.color.black))
                radioButton3.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
            R.id.txt_update -> {

                Log.d("27Nov:", "Set country :" + ccp.selectedCountryName)
                Log.d("27Nov:", "txt_dateofbirth :" + txt_dateofbirth.text.toString())
                if (radioButton1.isChecked) {
                    gender = radioButton1.text.toString().replace(".", "")
                } else if (radioButton2.isChecked) {
                    gender = radioButton2.text.toString().replace(".", "")
                } else if (radioButton3.isChecked) {
                    gender = radioButton3.text.toString().replace(".", "")
                }
                if (radioButton1.isChecked || radioButton2.isChecked || radioButton3.isChecked) {
                    if (txt_name.text.toString().isNotEmpty()) {
                        if (txt_name.text.toString().matches(Utils.nameValidation.toRegex())) {
                            if (txt_familyname.text.toString().isNotEmpty()) {
                                if (txt_familyname.text.toString()
                                        .matches(Utils.nameValidation.toRegex())
                                ) {
                                    if (txt_email.text.toString().isNotEmpty()) {

                                        /* if (txt_dateofbirth.text.toString().isNotEmpty()) {

                                         } else {
                                             showAlertMessage(
                                                 getString(
                                                     R.string.er_dob
                                                 ),
                                                 true, 1
                                             )

                                         }*/


                                        if (txt_email.text.toString()
                                                .matches(("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").toRegex())
                                        ) {
                                            if (mCountryId != getString(R.string.countrycode)) {
                                                if (txt_mobile_number.text.toString()
                                                        .isNotEmpty()
                                                ) {
                                                    if (isValidPhoneNumber(txt_mobile_number.text.toString())) {
                                                        val status =
                                                            validateUsing_libphonenumber(
                                                                code.trim(),
                                                                txt_mobile_number.text.toString()
                                                            )
                                                        if (isexception) run {
                                                            isexception = false
                                                        } else {

                                                            if (!area.equals(getString(R.string.selectarea))) {

                                                                if (status) {
                                                                    Utils.showProgressDialog(
                                                                        this,
                                                                        true
                                                                    )
                                                                    mPresenter.doUpdateAccount(
                                                                        sessionManager.userId.toString()
                                                                            .toInt(),
                                                                        gender,
                                                                        txt_name.text.toString(),
                                                                        txt_familyname.text.toString(),
                                                                        txt_email.text.toString(),
                                                                        mCountryId.toInt(),
                                                                        ccp.selectedCountryName,
                                                                        txt_mobile_number.text.toString(),
                                                                        "",
                                                                        txt_loyality_number.text.toString(),
                                                                        area,
                                                                        txt_dateofbirth.text.toString()
                                                                    )
                                                                } else {
                                                                    showAlertMessage(
                                                                        getString(
                                                                            R.string.er_inv_number
                                                                        ),
                                                                        false, 0
                                                                    )

                                                                    /*showMessage(getString(R.string.er_inv_number))*/
                                                                }
                                                            } else {
                                                                showAlertMessage(
                                                                    getString(
                                                                        R.string.er_inv_area
                                                                    ),
                                                                    true, 2
                                                                )

                                                                /*showMessage(getString(R.string.er_inv_area))*/
                                                            }


                                                        }
                                                    } else {
                                                        showAlertMessage(
                                                            getString(
                                                                R.string.er_inv_number
                                                            ),
                                                            false, 0
                                                        )

                                                        /*showMessage(getString(R.string.er_inv_number))*/
                                                    }

                                                } else {
                                                    showAlertMessage(
                                                        getString(
                                                            R.string.er_mobile
                                                        ),
                                                        true, 0
                                                    )

                                                    /*showMessage(getString(R.string.er_mobile))*/
                                                }

                                            } else {
                                                showAlertMessage(
                                                    getString(
                                                        R.string.er_countrycode
                                                    ),
                                                    true, 0
                                                )

                                                /*showMessage(getString(R.string.er_countrycode))*/
                                            }
                                        } else {
                                            showAlertMessage(
                                                getString(
                                                    R.string.er_inv_email
                                                ),
                                                false, 0
                                            )

                                            /*showMessage(getString(R.string.er_inv_email))*/
                                        }

                                    } else {
                                        showAlertMessage(
                                            getString(
                                                R.string.er_email
                                            ),
                                            true, 0
                                        )

                                        /*showMessage(getString(R.string.er_email))*/
                                    }
                                } else {
                                    showAlertMessage(
                                        getString(
                                            R.string.er_valid_name
                                        ),
                                        false, 0
                                    )

                                    /*showMessage(getString(R.string.er_valid_name))*/
                                }

                            } else {
                                showAlertMessage(
                                    getString(
                                        R.string.er_familyname
                                    ),
                                    true, 0
                                )

                                /*showMessage(getString(R.string.er_familyname))*/
                            }
                        } else {
                            showAlertMessage(
                                getString(
                                    R.string.er_valid_name
                                ),
                                false, 0
                            )

                            /*showMessage(getString(R.string.er_valid_name))*/
                        }
                    } else {
                        showAlertMessage(
                            getString(
                                R.string.er_name
                            ),
                            true, 0
                        )

                        /*showMessage(getString(R.string.er_name))*/
                    }
                } else {
                    showAlertMessage(
                        getString(
                            R.string.er_gender
                        ),
                        true, 0
                    )

                    /* showMessage(getString(R.string.er_gender))*/
                }
            }
            R.id.relative_change_pass -> {
                startActivity(Intent(this, ChangePasswordActivity::class.java))
            }
            R.id.image_switch -> {
                if (sessionManager.fingerprint) {
                    val dialogBuilder = android.app.AlertDialog.Builder(this)
                    // set message of alert dialog
                    dialogBuilder.setTitle(getString(R.string.fingerprint))
                    dialogBuilder.setMessage(getString(R.string.msg_fingerprint))
                        // if the dialog is cancelable
                        .setCancelable(false)
                        // positive button text and action
                        .setPositiveButton(
                            getString(R.string.ok),
                            DialogInterface.OnClickListener { dialog, id ->
                                sessionManager.fingerprint = false //change me
                                if (sessionManager.fingerprint) {
                                    image_switch.setChecked(true);

/*
                                    image_switch.setImageResource(R.drawable.ic_switch)
*/
                                } else {
                                    image_switch.setChecked(false);

/*
                                    image_switch.setImageResource(R.drawable.ic_switch_gray)
*/
                                }

                            })
                        // negative button text and action
                        .setNegativeButton(
                            getString(R.string.cancel),
                            DialogInterface.OnClickListener { dialog, id ->
                                dialog.cancel()
                            })
                    // create dialog box
                    val alert = dialogBuilder.create()
                    // show alert dialog
                    alert.show()
                } else {
                    sessionManager.fingerprint = true //change me
                    if (sessionManager.fingerprint) {
                        image_switch.setChecked(true);

/*
                        image_switch.setImageResource(R.drawable.ic_switch)
*/
                    } else {
                        image_switch.setChecked(false);

/*
                        image_switch.setImageResource(R.drawable.ic_switch_gray)
*/
                    }
                }

                //  fingerPrint()
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

                dialog.dismiss()
                if (count == 1) {
                    txt_dateofbirth.performClick()
                } else if (count == 2) {
                    Log.d("Valueeee", area)
                    if (area == getString(R.string.selectarea)) {
                        spinner_area.performClick()

                    }
                }
            }
        /*   .setNegativeButton(resources.getString(R.string.txt_no)) { dialog, id ->
               // Dismiss the dialog

           }*/
        val alert = builder.create()
        alert.show()
    }

    override fun showAccountDetails(response: StatusModel) {
        Utils.pd.dismiss()
        when {
            response.status == 200 -> {
                /*store user details to local storage*/
                sessionManager.email = response.data.userEmail
                sessionManager.name = response.data.userName
                sessionManager.familyName = response.data.familyName
                sessionManager.mobileNumber = response.data.mobileNumber
                sessionManager.gender = response.data.gender
                sessionManager.countryId = response.data.countryId
//              txt_name.setText(sessionManager.gender + " " + sessionManager.name.toString())
                txt_name.setText(sessionManager.name.toString())
                txt_familyname.setText(sessionManager.familyName.toString())
                txt_email.setText(sessionManager.email.toString())
                txt_mobile_number.setText(sessionManager.mobileNumber.toString())
                txt_loyality_number.setText(sessionManager.cardno.toString())
                if (response.data.country != null)
                    tvcountry.setText(response.data.country)
                else
                    tvcountry.setText("Qatar")

                Log.d("23Nov", "Expected : " + response.data.country)
//              ccp.setDefaultCountryUsingNameCode(response.data.country)

                if (response.data.dob != null)
                    txt_dateofbirth.setText(response.data.dob)

                if (response.data.area != null) {
                    for (i in 0 until mAreaList.size) {
                        if (response.data.area.trim() == mAreaList[i]) {
                            spinner_area.setSelection(i)
                        }
                    }

                }

                val adapter = ArrayAdapter(this, R.layout.item_spiner_black, mCountryList)
                spinner.adapter = adapter
                adapter.setDropDownViewResource(R.layout.item_drop_down_spinner)


                if (sessionManager.gender.toString() == "Mr") {
                    radioButton2.isChecked = true
                    radioButton2.setTextColor(ContextCompat.getColor(this, R.color.black))
                } else if (sessionManager.gender.toString() == "Ms") {
                    radioButton3.isChecked = true
                    radioButton3.setTextColor(ContextCompat.getColor(this, R.color.black))
                } else if (sessionManager.gender.toString() == "Mrs") {
                    radioButton1.isChecked = true
                    radioButton1.setTextColor(ContextCompat.getColor(this, R.color.black))
                }
                code = sessionManager.countryCode.toString()
            }
            response.status == 402 -> {
                Utils.tokenExpire(this)
                /*Account details*/
                Utils.showProgressDialog(this, true)
                mPresenter.getAccountDetails(sessionManager.userId.toString().toInt())
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

    override fun showCountries(response: CountryModel) {
        when {
            response.status == 200 -> {
                mCList = response.data as ArrayList<Data>
                mCountryList.add(getString(R.string.countrycode))
//                mCountryId = getString(R.string.countrycode)
                mCountryId = "3"
                for (i in 0 until response.data.size) {
                    mCountryList.addAll(setOf(response.data[i].code))
                }
                val adapter = ArrayAdapter(this, R.layout.item_spiner_black, mCountryList)
                spinner.adapter = adapter
                adapter.setDropDownViewResource(R.layout.item_drop_down_spinner)
                for (i in mCList.indices) {
                    if (sessionManager.countryId == mCList[i].countryId) {
//                        code = mCList[i].code
                        code = "974"
                        spinner.setSelection(adapter.getPosition(mCList[i].code))
                    }
                }

            }
            response.status == 402 -> {
                Utils.tokenExpire(this)
                mPresenter.getCountries()
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

    override fun showUpdateAccount(response: MyAccountModel) {
        Utils.pd.dismiss()
        when {
            response.status == 200 -> {
                if (sessionManager.languageselect == "en") {
                    showMessage(response.statusMessage)
                } else {
                    showMessage(response.statusMsgArabic)
                }
                sessionManager.mobileNumber = txt_mobile_number.text.toString()
                sessionManager.email = txt_email.text.toString()
                sessionManager.name = txt_name.text.toString()
                sessionManager.familyName = txt_familyname.text.toString()
                sessionManager.countryId = mCountryId
                sessionManager.gender = gender
                sessionManager.cardno = txt_loyality_number.text.toString()

                finish()
            }
            response.status == 503 -> {
                val i = Intent(this, OTPActivity::class.java)
                i.putExtra("signUpUserid", sessionManager.userId.toString())
                i.putExtra("myaccount", "myaccount")
                i.putExtra("emailId", txt_email.text.toString())
                i.putExtra("mobile_no", txt_mobile_number.text.toString())
                startActivityForResult(i, 200)
            }
            response.status == 402 -> {
                Utils.tokenExpire(this)
                Utils.showProgressDialog(this, true)
                mPresenter.doUpdateAccount(
                    sessionManager.userId.toString().toInt(),
                    gender,
                    txt_name.text.toString(),
                    txt_familyname.text.toString(),
                    txt_email.text.toString(),
                    mCountryId.toInt(),
                    ccp.selectedCountryName,
                    txt_mobile_number.text.toString(),
                    "",
                    txt_loyality_number.text.toString(),
                    area,
                    txt_dateofbirth.text.toString()

                )
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
/*
                Toast.makeText(this, resources.getString(R.string.er_inv_number), Toast.LENGTH_LONG)
                    .show()
*/
                return false
            }
        } catch (e: NumberParseException) {
            System.err.println(e)
            Toast.makeText(this, resources.getString(R.string.er_inv_number), Toast.LENGTH_LONG)
                .show()
        }


        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 200 && resultCode == 200) {
            sessionManager.mobileNumber = txt_mobile_number.text.toString()
            sessionManager.email = txt_email.text.toString()
            sessionManager.name = txt_name.text.toString()
            sessionManager.familyName = txt_familyname.text.toString()
            sessionManager.countryId = mCountryId
            sessionManager.gender = gender
        }
    }

}