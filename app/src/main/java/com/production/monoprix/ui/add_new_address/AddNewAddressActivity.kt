package com.production.monoprix.ui.add_new_address

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.production.monoprix.MyApplication
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.*
import com.production.monoprix.mvp.BaseMvpActivity
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.activity_add_new_address.*
import kotlinx.android.synthetic.main.activity_myaccount.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.InvocationTargetException
import java.net.SocketException
import java.nio.charset.Charset
import java.util.*


class AddNewAddressActivity :
    BaseMvpActivity<AddNewAddressContractor.View, AddNewAddressContractor.Presenter>(),
    AddNewAddressContractor.View, View.OnClickListener {
    lateinit var sessionManager: SessionManager
    override var mPresenter: AddNewAddressContractor.Presenter = AddNewAddressPresenter()

    protected var mCityList = ArrayList<String>()
    protected var mCityID = ArrayList<String>()
    lateinit var strCityID: String

    protected var mAreaList = ArrayList<String>()
    protected var mAreaID = ArrayList<String>()
    lateinit var strAreaID: String

    var device_id: String? = null
    var strPageName: String = "0"
    var strAddressID: String? = null
    var strEmail: String? = null
    var strPhone: String? = null
    var strAddress: String? = null
    var strlocationAddress: String? = null
    var postcode: String? = null
    var strAreaName: String? = ""
    var strCityName: String? = null
    var strAddressType: String? = null
    var strDefaultAddress: String? = null
    var diff: String? = null
    var countryId: String? = null
    var strCountryName: String? = "Qatar"
    var municipalityName: String? = null
    var placeName: String? = null
    var stringZone: String? = null
    var municipality: String? = null
    var municipality_arabic: String? = null
    var place: String? = null
    var isSetDefault: Boolean = false

    var street: String? = null
    var building: String? = null
    var landMark: String? = null
    var house_no: String? = null
    var zone_no: String? = null

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    protected var mCountryList = ArrayList<String>()
    protected var mZoneStringList = ArrayList<String>()

    /*
        protected var mZoneList = ArrayList<String>()
    */
    protected var mPlacesList = ArrayList<String>()
    protected var mPlacesListArabic = ArrayList<String>()
    protected var mCList = ArrayList<Data>()
    protected var mZoneList = ArrayList<ZoneModelItem>()
    var mCountryId: String = "3"
    var cityIdR: String = ""
    var zoneId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_address)
        sessionManager = SessionManager(this)
        init()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }

    fun init() {

        device_id = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        strPageName = intent.getStringExtra("pageName").toString()
        strAddressID = intent.getStringExtra("addressID")
        strEmail = intent.getStringExtra("email")
        strPhone = intent.getStringExtra("phone")
        strAddress = intent.getStringExtra("address")
        strAreaName = intent.getStringExtra("area")
        strCityName = intent.getStringExtra("city")
        strAddressType = intent.getStringExtra("address_type")
        strDefaultAddress = intent.getStringExtra("isDefaultAddress")
        postcode = intent.getStringExtra("postcode")
        municipalityName = intent.getStringExtra("municipality")
        cityIdR = intent.getStringExtra("cityId") ?: ""
        placeName = intent.getStringExtra("place")
        diff = intent.getStringExtra("diff")
        house_no = intent.getStringExtra("house_no") ?: ""
        landMark = intent.getStringExtra("landMark") ?: ""
        building = intent.getStringExtra("building") ?: ""
        street = intent.getStringExtra("street") ?: ""
        zone_no = intent.getStringExtra("zone_no") ?: ""

        Log.d("27Nov:","RRR zone_no :"+ zone_no)
//        Log.d("27Nov:","cityIdR :"+ cityIdR)
//        Log.d("27Nov:","placeName :"+ placeName)

        if (strDefaultAddress == "true") {
            checkbox_def_address.isChecked = strDefaultAddress == "true"
            isSetDefault = true
        } else {
            strDefaultAddress = "false"
        }
        if (strAddressType != null) {
            if (strAddressType == "1") {
                checkbox_home_address.isChecked = true
            } else if (strAddressType == "2") {
                checkbox_office_address.isChecked = true
            } else if (strAddressType == "3") {
                checkbox_other_address.isChecked = true
            } else if (strAddressType == getString(R.string.label_home)) {
                checkbox_home_address.isChecked = true
            } else if (strAddressType == getString(R.string.label_office)) {
                checkbox_office_address.isChecked = true
            } else if (strAddressType == getString(R.string.label_other)) {
                checkbox_other_address.isChecked = true
            }
        }

        if (strPageName == "1") {
            txt_title.text = getString(R.string.editaddress)
            txt_locate_me.setText(strAddress)
            txt_locate_me.requestFocus()
            edt_mobile.setText(strPhone)
            edt_postal_code.setText(postcode)
            edt_street.setText(street)
            edt_landmark.setText(landMark)
            edt_building.setText(building)
            edt_house_no.setText(house_no)
            edt_zone.setText(zone_no)


        } else {
            txt_title.text = getString(R.string.addaddress)
        }

        mPresenter.getCityList(mCountryId.toInt())
        mPresenter.getzones()

        /*onclick listener*/
        img_left_arrow.setOnClickListener(this)
        tv_address_save.setOnClickListener(this)
        ivLocateMe.setOnClickListener(this)
        checkbox_home_address.setOnClickListener(this)
        checkbox_office_address.setOnClickListener(this)
        checkbox_other_address.setOnClickListener(this)
        checkbox_def_address.setOnClickListener(this)

        /*spinner for country*/

        sp_country_code.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                strCountryName = p0?.getItemAtPosition(p2).toString()
                if (strCountryName != getString(R.string.hint_country)) {
                    mCountryId = mCList[p2 - 1].countryId
                    mPresenter.getCityList(mCountryId.toInt())
                    relative_city.visibility = View.VISIBLE
                } else {
                    strCountryName = ""
                    relative_city.visibility = View.GONE
                }
            }

        }

        spinner_zone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                stringZone = p0?.getItemAtPosition(p2).toString()
                if (p2 != 0) {
                    zoneId = mZoneList[p2 - 1].zoneno
                    stringZone = mZoneList[p2 - 1].zoneno.toString()
                    municipality = mZoneList[p2 - 1].muncipality
                    municipality_arabic = mZoneList[p2 - 1].muncipalitY_Arabic

                    if (sessionManager.languageselect == "en") {
                        et_municiplaity.setText(municipality.toString())

                    } else {
                        et_municiplaity.setText(municipality_arabic.toString())

                    }

                    //visible Municipality.... 24 feb
                    rlmunicipality.visibility = View.VISIBLE
                    //set places spinner too...

                    mPlacesList.clear()
                    mPlacesListArabic.clear()
                    mPlacesList.add(getString(R.string.selectplace))
                    mPlacesListArabic.add(getString(R.string.selectplace))
                    for (item in mZoneList[p2 - 1].places.iterator()) {

                        mPlacesList.addAll(setOf(item))
                    }

                    for (item in mZoneList[p2 - 1].places_Arabic.iterator()) {

                        mPlacesListArabic.addAll(setOf(item))
                    }

                    if (sessionManager.languageselect == "en") {
                        setPlaceAdapter()
                    } else {
                        setPlaceAdapterArabic()
                    }

                    rlplaces.visibility = View.VISIBLE

                } else {
                    rlmunicipality.visibility = View.GONE
                    rlplaces.visibility = View.GONE

                }

            }

        }

        //Spinner for City
        edt_city.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedCity: String = parent.getItemAtPosition(position).toString()
                // strCityName = mCityList[position]
                strCityName = selectedCity
                if (strCityName != getString(R.string.slectcity)) {
                    strCityID = mCityID[position]
                    mPresenter.getAreaList(strCityID.toInt())
                    relative_location.visibility = View.GONE
                } else {
                    relative_location.visibility = View.GONE
                    strCityName = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        //Spinner for area
        edt_location.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedArea: String = parent.getItemAtPosition(position).toString()
                strAreaName = selectedArea
                //strAreaName = mAreaList[position]
                if (strAreaName != getString(R.string.selectarea)) {
                    strAreaID = mAreaID[position]
                } else {
                    strAreaName = ""
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


    }

    private fun setPlaceAdapter() {

        val adapter1 = ArrayAdapter(this, R.layout.item_spiner_black, mPlacesList)
//        Log.d("27Nov:","setPlaceAdapter() :")

        spinner_places.adapter = adapter1
        adapter1.setDropDownViewResource(R.layout.item_drop_down_spinner)

        for (i in 0 until mPlacesList.size) {
            if (placeName == mPlacesList[i]) {
//                Log.d("27Nov:","mPlacesList[i] :"+ mPlacesList[i])

                spinner_places.setSelection(i)
            }
        }

        spinner_places.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                place = p0?.getItemAtPosition(p2).toString()
                if (p2 != 0) {
                    place = mPlacesList[p2]
                    Log.d("PLace:", place!!)
                }

            }

        }

    }

    private fun setPlaceAdapterArabic() {

        val adapter1 = ArrayAdapter(this, R.layout.item_spiner_black, mPlacesListArabic)

        spinner_places.adapter = adapter1
        adapter1.setDropDownViewResource(R.layout.item_drop_down_spinner)

        for (i in 0 until mPlacesList.size) {
            if (placeName == mPlacesList[i]) {
                spinner_places.setSelection(i)
            }
        }

        spinner_places.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                place = p0?.getItemAtPosition(p2).toString()
                if (p2 != 0) {
                    place = mPlacesList[p2]
                    Log.d("PLace:", place!!)
                }

            }

        }

    }

    fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        } else {
            /*get lat and long using last known location*/
            val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val lastKnownLocationGPS =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocationGPS != null) {
                getAddress(lastKnownLocationGPS.latitude, lastKnownLocationGPS.longitude)
            } else {
                val loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                if (loc != null) {
                    getAddress(loc.latitude, loc.longitude)
                }
            }
        }

    }

    fun getAddress(latitude: Double, longitude: Double) {

        try {
            val addresses: List<Address>
            val geoCoder: Geocoder = Geocoder(this, Locale.getDefault())

            addresses = geoCoder.getFromLocation(
                latitude,
                longitude,
                1
            )!! // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            val address =
                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            val city = addresses[0].locality
            val state = addresses[0].adminArea
            val country = addresses[0].countryName
            val postalCode = addresses[0].postalCode
            val knownName = addresses[0].featureName // Only if available else return NULL
            edt_postal_code.setText(postalCode)
            txt_locate_me.setText(address)
            txt_locate_me.requestFocus()
            txt_locate_me.setSelection(txt_locate_me.text.length)
        } catch (e: InvocationTargetException) {
            e.targetException.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun showCountries(response: CountryModel) {
        when {
            response.status == 200 -> {
                mCList = response.data as ArrayList<Data>
                mCountryList.add(getString(R.string.hint_country))
                mCountryId = getString(R.string.hint_country)
                for (element in response.data) {
                    mCountryList.addAll(setOf(element.name))
                }
                val adapter = ArrayAdapter(this, R.layout.item_spiner_black, mCountryList)
                sp_country_code.adapter = adapter
                adapter.setDropDownViewResource(R.layout.item_drop_down_spinner)
                for (i in mCList.indices) {
                    if (strPageName == "1") {
                        if (strCountryName == mCList[i].name) {
                            sp_country_code.setSelection(adapter.getPosition(mCList[i].name))
                            strCountryName = mCList[i].name
                        }
                    } else {
                        if (sessionManager.countryId == mCList[i].countryId) {
                            sp_country_code.setSelection(adapter.getPosition(mCList[i].name))
                            strCountryName = sessionManager.countryName
                        }
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

    override fun showZones(response: ZoneModel) {
        when {
            response.status == 200 -> {

//                Log.d("27Nov:", "showZones")

                mZoneList = response.data as ArrayList<ZoneModelItem>
                mZoneStringList.add(getString(R.string.selectzone))
                for (element in response.data) {
                    mZoneStringList.addAll(setOf(element.zoneno.toString()))
                }
                val adapter = ArrayAdapter(this, R.layout.item_spiner_black, mZoneStringList)
                spinner_zone.adapter = adapter
                adapter.setDropDownViewResource(R.layout.item_drop_down_spinner)
                for (i in 0 until mZoneStringList.size) {
                    if (zone_no == mZoneStringList[i]) {

//                        Log.d("27Nov:", "mZoneStringList[i]"+ mZoneStringList[i])

                        spinner_zone.setSelection(i)
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

    //Get City List
    override fun setCityList(response: CountryModel?) {

        Log.d("27Nov:", "setCityList")

        Utils.pd.dismiss()
        mCityList = ArrayList<String>()
        mCityID = ArrayList<String>()
        mCityList.add(getString(R.string.slectcity))
        mCityID.add("0")
        if (response!!.status == 200) {
            for (i in 0 until response.data.size) {
                mCityList.addAll(setOf(response.data[i].name))
                mCityID.addAll(setOf(response.data[i].cityid))


            }
            val adapter = ArrayAdapter(this, R.layout.item_spiner_black, mCityList)
            edt_city.adapter = adapter
            adapter.setDropDownViewResource(R.layout.item_drop_down_spinner)

            for (i in 0 until mCityID.size) {
                if (cityIdR == mCityID[i]) {

//                    Log.d("27Nov:","mCityID[i] :"+ mCityID[i])

                    edt_city.setSelection(i)
                }
            }

        } else if (response.status == 402) {
            Utils.tokenExpire(this)
            Utils.showProgressDialog(this, true)
            mPresenter.getCityList(mCountryId.toInt())
        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    //Get Area List
    override fun setAreaList(response: CountryModel?) {
        Utils.pd.dismiss()
        mAreaList = ArrayList<String>()
        mAreaID = ArrayList<String>()
        mAreaList.add(getString(R.string.selectarea))
        mAreaID.add("0")
        if (response!!.status == 200) {
            for (i in 0 until response.data.size) {
                mAreaList.addAll(setOf(response.data[i].name))
                mAreaID.addAll(setOf(response.data[i].areaId))
            }
            val adapter = ArrayAdapter(this, R.layout.item_spiner_black, mAreaList)
            edt_location.adapter = adapter
            adapter.setDropDownViewResource(R.layout.item_drop_down_spinner)
            if (strAreaName != null) {
                val areaPosition = adapter.getPosition(strAreaName)
                edt_location.setSelection(areaPosition)
            }

        } else if (response.status == 402) {
            Utils.tokenExpire(this)
            Utils.showProgressDialog(this, true)
            mPresenter.getAreaList(strCityID.toInt())
        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }


    override fun setEditShippingAddress(response: StatusModel?) {
        Utils.pd.dismiss()
        if (response!!.status == 200) {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
            if (diff != null) {
                if (diff == "checkout" || diff == "payment") {
                    val intent = Intent()
                    intent.putExtra("phone", strPhone)

                    Log.d("16Nov: put Add"," strAddress "+strAddress)
                    Log.d("16Nov: put Add"," strAreaName "+strAreaName)
                    Log.d("16Nov: put Add"," strCityName "+strCityName)

                    intent.putExtra("address", strAddress)
                    intent.putExtra("area", strAreaName)
                    intent.putExtra("city", strCityName)

                    intent.putExtra("address_type", strAddressType)
                    intent.putExtra("addressID", strAddressID)
                    intent.putExtra("isDefaultAddress", strDefaultAddress)
                    intent.putExtra("country", strCountryName)
                    intent.putExtra("postcode", postcode)

                    intent.putExtra("zone_no", zoneId)
                    intent.putExtra("street", street)
                    intent.putExtra("building", building)
                    intent.putExtra("landMark", landMark)
                    intent.putExtra("house_no", house_no)
                    intent.putExtra("zone_no", zoneId)
                    intent.putExtra("muncipality", municipality)
                    intent.putExtra("place", placeName)

                    Log.d("22Nov:Sending","building "+building+ "houseNo :"+house_no+
                            " muncipality "+municipality+"place "+ place+"landMark "+landMark)

                    setResult(200, intent)
                    finish()
                }
            } else {
                callAddressListPage()
            }
        } else if (response.status == 402) {
            Utils.tokenExpire(this)
            Utils.showProgressDialog(this, true)
            zone_no = edt_zone.text.toString()
            street = edt_street.text.toString()
            building = edt_building.text.toString()
            landMark = edt_landmark.text.toString()
            house_no = edt_house_no.text.toString()
            mPresenter.getEditShippingAddress(
                strAddressID!!.toInt(),
                strAddressType.toString(),
                sessionManager.userId!!.toInt(),
                0,
                strAddress!!,
                strlocationAddress ?: "",
                postcode,
                strPhone!!,
                edt_landmark.text.toString(),
                edt_building.text.toString(),
                edt_house_no.text.toString(),
                edt_street.text.toString(),
                strCityID.toInt(),
                mCountryId.toInt(),
                strDefaultAddress!!.toBoolean(),
                zoneId.toString(),
                municipality,
                place,
                strAreaName.toString()
            )
        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    override fun setAddNewAddressDetails(response: StatusModel) {
        Utils.pd.dismiss()
        when {
            response.status == 200 -> {
                if (sessionManager.languageselect == "en") {
                    showMessage(response.statusMessage)
                } else {
                    showMessage(response.statusMsgArabic)
                }
                callAddressListPage()
            }
            response.status == 402 -> {
                Utils.tokenExpire(this)
                Utils.showProgressDialog(this, true)
                zone_no = edt_zone.text.toString()
                street = edt_street.text.toString()
                building = edt_building.text.toString()
                landMark = edt_landmark.text.toString()
                house_no = edt_house_no.text.toString()
                mPresenter.addNewAddressDetails(
                    strAddressType!!,
                    sessionManager.userId!!.toInt(),
                    0,
                    strAddress!!,
                    strlocationAddress,
                    postcode,
                    strPhone!!,
                    edt_landmark.text.toString(),
                    edt_building.text.toString(),
                    edt_house_no.text.toString(),
                    edt_street.text.toString(),
                    strCityID.toInt(),
                    mCountryId.toInt(),
                    strDefaultAddress!!.toBoolean(),
                    zoneId.toString(),
                    municipality,
                    place,
                    strAreaName.toString()
                )
            }
            else -> if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    private fun callAddressListPage() {
        this.setResult(200)
        this.finish()
    }

    override fun showReloadButton(throwable: Throwable, errorBody: ErrorBody?, network: Boolean) {
        Utils.pd.dismiss()
        when {
            network -> showMessage(getString(R.string.no_internet))
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

            R.id.tv_address_save -> {
                /*Api Call*/
                if (strPageName.equals("1")) {
                    getEditAddressValidation()
                } else {
                    getAddAddressValidation()
                }

            }
            R.id.checkbox_def_address -> {
                if (isSetDefault) {
                    strDefaultAddress = "false"
                    isSetDefault = false
                } else {
                    strDefaultAddress = "true"
                    isSetDefault = true
                }
            }
            R.id.ivLocateMe -> {
                getLocation()
                inp_address.visibility = View.GONE
            }
            R.id.checkbox_home_address -> {
                checkbox_office_address.isChecked = false
                checkbox_other_address.isChecked = false
            }
            R.id.checkbox_office_address -> {
                checkbox_home_address.isChecked = false
                checkbox_other_address.isChecked = false

            }
            R.id.checkbox_other_address -> {
                checkbox_home_address.isChecked = false
                checkbox_office_address.isChecked = false
            }

        }
    }

    private fun getEditAddressValidation() {
        if (edt_address.text.toString().isNotEmpty()) {
            strAddress = edt_address.text.toString()
            strlocationAddress = txt_locate_me.text.toString()
            postcode = edt_postal_code.text.toString()
        } else {
            strAddress = txt_locate_me.text.toString()
            strlocationAddress = ""
            postcode = edt_postal_code.text.toString()
        }
        strPhone = edt_mobile.text.toString()
        strAddressType = when {
            checkbox_home_address.isChecked -> "1"
            checkbox_office_address.isChecked -> "2"
            checkbox_other_address.isChecked -> "3"
            else -> ""
        }

        if (strPhone!!.isNotEmpty()) {
            if (strPhone!!.length > 7) {
                if (strCountryName != getString(R.string.hint_country) && !(TextUtils.isEmpty(
                        strCountryName
                    ))
                ) {
                    if (strCityName != getString(R.string.slectcity) && !(TextUtils.isEmpty(
                            strCityName
                        ))
                    ) {

                        if (strAddressType!!.isNotEmpty()) {

                            if (!strAddressType.isNullOrEmpty()) {

                                Utils.showProgressDialog(this, true)

                                zone_no = edt_zone.text.toString()
                                street = edt_street.text.toString()
                                building = edt_building.text.toString()
                                landMark = edt_landmark.text.toString()
                                house_no = edt_house_no.text.toString()
                                mPresenter.getEditShippingAddress(
                                    strAddressID!!.toInt(),
                                    strAddressType.toString(),
                                    sessionManager.userId!!.toInt(),
                                    0,
                                    strAddress ?: "",
                                    strlocationAddress ?: "",
                                    postcode,
                                    strPhone!!,
                                    edt_landmark.text.toString(),
                                    edt_building.text.toString(),
                                    edt_house_no.text.toString(),
                                    street.toString(),
                                    strCityID.toInt(),
                                    mCountryId.toInt(),
                                    strDefaultAddress!!.toBoolean(),
                                    zoneId.toString(),
                                    municipality,
                                    place,
                                    strAreaName.toString()
                                )

                            } else {
                                showAlertMessage(getString(R.string.selectzone), false)
                                edt_zone.requestFocus()
                                /*showMessage(getString(R.string.er_zoneno))*/
                            }
                        } else {
                            showAlertMessage(getString(R.string.er_addre_type), false)
                        }


                    } else {
                        showAlertMessage(getString(R.string.slectcity), true)
                        edt_street.requestFocus()
                        /*showMessage(getString(R.string.slectcity))*/
                    }

                } else {
                    showAlertMessage(getString(R.string.slectcity), true)
                    edt_street.requestFocus()
                    /*showMessage(getString(R.string.countryName))*/

                }
            } else {
                showAlertMessage(getString(R.string.er_inv_number), false)
                edt_mobile.requestFocus()
                /*showMessage(getString(R.string.er_inv_number))*/
            }
        } else {
            showAlertMessage(getString(R.string.er_mobile), true)
            edt_mobile.requestFocus()
            /*showMessage(getString(R.string.er_mobile))*/
        }
        /*} else {
            showMessage(getString(R.string.er_postal_code))
        }*/
        /* } else {
             showMessage(getString(R.string.er_address1))
         }*/

    }

    private fun showAlertMessage(alert: String, showAllFieldsReq: Boolean) {

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
            }
        /*   .setNegativeButton(resources.getString(R.string.txt_no)) { dialog, id ->
               // Dismiss the dialog

           }*/
        val alert = builder.create()
        alert.show()
    }

    private fun verifyZone(zoneNo: String?): Boolean {

        if (zoneNo.isNullOrEmpty())
            return false
        else {
            if (zoneNo.toInt() > 0 && zoneNo.toInt() <= 155)
                return true
            else
                return false
        }


    }

    private fun getAddAddressValidation() {
        if (edt_address.text.toString().isNotEmpty()) {
            strAddress = edt_address.text.toString()
            strlocationAddress = txt_locate_me.text.toString()
            postcode = edt_postal_code.text.toString()
        } else {
            strAddress = txt_locate_me.text.toString()
            strlocationAddress = ""
            postcode = edt_postal_code.text.toString()
        }
        strPhone = edt_mobile.text.toString()

        strAddressType = when {
            checkbox_home_address.isChecked -> "1"
            checkbox_office_address.isChecked -> "2"
            checkbox_other_address.isChecked -> "3"
            else -> ""
        }

        if (strPhone!!.isNotEmpty()) {
            if (strPhone!!.length > 7) {
                if (strCountryName != getString(R.string.hint_country) && !(TextUtils.isEmpty(
                        strCountryName
                    ))
                ) {
                    if (strCityName != getString(R.string.slectcity) && !(TextUtils.isEmpty(
                            strCityName
                        ))
                    ) {
                        if (stringZone != getString(R.string.selectzone)) {

                            if (!strAddressType.isNullOrEmpty()) {

                                Utils.showProgressDialog(this, true)
                                zone_no = edt_zone.text.toString()
                                street = edt_street.text.toString()
                                building = edt_building.text.toString()

                                landMark = edt_landmark.text.toString()
                                house_no = edt_house_no.text.toString()
                                mPresenter.addNewAddressDetails(
                                    strAddressType!!,
                                    sessionManager.userId!!.toInt(),
                                    0,
                                    strAddress ?: "",
                                    strlocationAddress ?: "",
                                    postcode,
                                    strPhone!!,
                                    edt_landmark.text.toString(),
                                    edt_building.text.toString(),
                                    edt_house_no.text.toString(),
                                    street.toString(),
                                    strCityID.toInt(),
                                    mCountryId.toInt(),
                                    strDefaultAddress!!.toBoolean(),
                                    zoneId.toString(),
                                    municipality,
                                    place,
                                    strAreaName.toString()
                                )
                            } else {
                                showAlertMessage(getString(R.string.er_addre_type), false)

                            }

                        } else {
                            showAlertMessage(getString(R.string.selectzone), false)
                            /*edt_zone.requestFocus()*/
                            /* showMessage(getString(R.string.er_zoneno))*/

                        }


                    } else {
                        showAlertMessage(getString(R.string.slectcity), true)
                        edt_street.requestFocus()
                        /*showMessage(getString(R.string.slectcity))*/
                    }

                } else {
                    showAlertMessage(getString(R.string.slectcity), true)
                    edt_street.requestFocus()
                    /*showMessage(getString(R.string.countryName))*/

                }
            } else {
                showAlertMessage(getString(R.string.er_inv_number), false)
                edt_mobile.requestFocus()
                /*showMessage(getString(R.string.er_inv_number))*/
            }
        } else {
            showAlertMessage(getString(R.string.er_mobile), true)
            edt_mobile.requestFocus()
            /*showMessage(getString(R.string.er_mobile))*/
        }
        /*} else {
            showMessage(getString(R.string.er_postal_code))
        }*/
        /*} else {
            showMessage(getString(R.string.er_address1))
        }*/
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
                    getLocation()
                }
                return
            }
        }
    }

}