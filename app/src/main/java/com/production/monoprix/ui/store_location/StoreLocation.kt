//package com.production.monoprix.ui.store_location
//
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.app.Dialog
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.drawable.ColorDrawable
//import android.location.LocationManager
//import android.net.Uri
//import android.os.Bundle
//import android.text.BidiFormatter
//import android.util.DisplayMetrics
//import android.util.Log
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.annotation.DrawableRes
//import androidx.core.app.ActivityCompat
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.*
//import com.production.monoprix.R
//import com.production.monoprix.api.ErrorBody
//import com.production.monoprix.model.CountryModel
//import com.production.monoprix.model.Data
//import com.production.monoprix.mvp.BaseMvpActivity
//import com.production.monoprix.ui.home.HomeActivity
//import com.production.monoprix.ui.home.HomeFragment
//import com.production.monoprix.util.SessionManager
//import com.production.monoprix.util.Utils
//import com.squareup.picasso.Picasso
//import kotlinx.android.synthetic.main.activity_loyalty.view.*
//import kotlinx.android.synthetic.main.activity_main_layout.*
//import kotlinx.android.synthetic.main.activity_main_layout.include
//import kotlinx.android.synthetic.main.activity_store_location.*
//import kotlinx.android.synthetic.main.dialog_map.*
//import kotlinx.android.synthetic.main.include_toolbar.*
//import java.net.SocketException
//
//class StoreLocation : BaseMvpActivity<StoreContractor.View, StoreContractor.Presenter>(),
//    StoreContractor.View, OnMapReadyCallback, View.OnClickListener,
//    GoogleMap.OnMarkerClickListener {
//
//    private lateinit var mMap: GoogleMap
//    var customDialog: Dialog? = null
//    var latitude: Double = 0.0
//    var longitude: Double = 0.0
//    var currentLatitude: Double = 0.0
//    var currentLongitude: Double = 0.0
//    var openTime: String = ""
//    var phoneCall: String = ""
//    var email: String = ""
//    var LOCATION_PERMISSION_REQUEST_CODE: Int = 200
//    var CALL_REQUEST_CODE: Int = 100
//    override var mPresenter: StoreContractor.Presenter = StorePresenter()
//    lateinit var sessionManager: SessionManager
//    var sList: ArrayList<Data> = ArrayList<Data>()
//    var address: String? = null
//    var shopImage: String = ""
//    var location: String = ""
//    lateinit var result: List<String>
//    lateinit var strval: List<String>
//    var show: String = ""
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_store_location)
//        init()
//    }
//
//    fun init() {
//        sessionManager = SessionManager(this)
//        ivback.setOnClickListener(this)
//        /*location*/
//        Utils.showProgressDialog(this, true)
//        mPresenter.getStoreLocation()
//        rltop.visibility = View.VISIBLE
//        /* if (intent.hasExtra("Show")) {
//
//             show = intent.hasExtra("Show").toString()
//
//         }
//         if (show.isNotEmpty()){
//
//             statusbar.visibility= View.VISIBLE
//            *//*include.visibility= View.GONE
//            navigation.visibility= View.GONE*//*
//        }
//        else{
//            statusbar.visibility= View.GONE
//           *//* include.visibility= View.VISIBLE
//            navigation.visibility= View.VISIBLE*//*
//        }*/
//
//        // getLocation()
//    }
//
//    private fun createMarker(
//        latitude: Double, longitude: Double, locationName: String, sno: Int
//    ): Marker {
//
//        return mMap.addMarker(
//            MarkerOptions()
//                .position(LatLng(latitude, longitude))
//                .anchor(0.5f, 0.5f)
//                .title(locationName)
//                .icon(
//                    BitmapDescriptorFactory.fromBitmap(
//                        createCustomMarker(this@StoreLocation, R.drawable.map_pin3, "")
//                    )
//
//                )
//                .snippet("")
//
//        )
//    }
//
//    fun createCustomMarker(context: Context, @DrawableRes resource: Int, _name: String): Bitmap {
//        val marker = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
//            .inflate(R.layout.custom_marker_layout, null)
//
//        val markerImage = marker.findViewById(R.id.user_dp) as ImageView
//        markerImage.setImageResource(resource)
//        val txt_name = marker.findViewById(R.id.name) as TextView
//        txt_name.text = _name
//        val displayMetrics = DisplayMetrics()
//        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
//        marker.layoutParams = ViewGroup.LayoutParams(22, ViewGroup.LayoutParams.WRAP_CONTENT)
//        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
//        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
//        val bitmap = Bitmap.createBitmap(
//            marker.measuredWidth,
//            marker.measuredHeight,
//            Bitmap.Config.ARGB_8888
//        )
//        val canvas = Canvas(bitmap)
//        marker.draw(canvas)
//        return bitmap
//    }
//
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//        mMap.setOnMarkerClickListener(this)
//        mMap.uiSettings.isZoomControlsEnabled = true
//        var sno: Int = 0
//        for (i in sList.indices) {
//            val input = sList[i].location
//            // 12.9718° N:77.6552° E
//            // val input = "12.972442, 77.580643"
//            println("=== String into List ===")
//            result = input.split(",").map { it.trim() }
//            sno += 1
//            if (sessionManager.languageselect == "en") {
//                createMarker(
//                    result[0].toDouble(),
//                    result[1].toDouble(), sList[i].name, sno
//                )
//            } else {
//                createMarker(
//                    result[0].toDouble(),
//                    result[1].toDouble(), sList[i].name_Arabic, sno
//                )
//            }
//            val location = LatLng(result[0].toDouble(), result[1].toDouble())
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
//            val builder = LatLngBounds.Builder()
//            builder.include(location)
//            val bounds = builder.build()
//            val cu = CameraUpdateFactory.newLatLngBounds(bounds, 200)
//            mMap.moveCamera(cu)
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(9.0f), 2000, null)
//            moveToCurrentLocation(location)
//            /* if(i == sList.size - 1){
//                 getLocation()
//             }*/
//        }
//        /**initialize the padding for map boundary*/
//
//    }
//
//    private fun moveToCurrentLocation(currentLocation: LatLng) {
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 11f))
//        // Zoom in, animating the camera.
//        mMap.animateCamera(CameraUpdateFactory.zoomIn())
//        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(11f), 2000, null)
//    }
//
//    override fun showStoreLocation(response: CountryModel) {
//        Utils.pd.dismiss()
//        if (response.status == 200) {
//            sList = response.data as ArrayList<Data>
//            val mapFragment =
//                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//            mapFragment.getMapAsync(this)
//        } else if (response.status == 402) {
//            Utils.tokenExpire(this)
//            Utils.showProgressDialog(this, true)
//            mPresenter.getStoreLocation()
//        } else {
//            if (sessionManager.languageselect == "en") {
//                showMessage(response.statusMessage)
//            } else {
//                showMessage(response.statusMsgArabic)
//            }
//        }
//    }
//
//    override fun showReloadButton(throwable: Throwable, errorBody: ErrorBody?, network: Boolean) {
//        Utils.pd.dismiss()
//        when {
//            network -> showMessage(getString(R.string.no_internet))
//            errorBody != null -> showMessage(errorBody.toString())
//            throwable is SocketException -> showMessage(throwable.message!!)
//            else -> showMessage(throwable.message!!)
//        }
//    }
//
//    override fun onClick(v: View?) {
//        when (v?.id) {
//            R.id.ivback -> {
//                onBackPressed()
//            }
//            R.id.tv_store_mobile -> {
//
////                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneCall"))
////                startActivity(intent)
//                callEmailIntent()
//            }
//
//            R.id.img_dialog_close -> {
//                customDialog!!.dismiss()
//            }
//        }
//    }
//
//    private fun callEmailIntent() {
//
//        val mIntent = Intent(Intent.ACTION_SEND)
//        /*To send an email you need to specify mailto: as URI using setData() method
//        and data type will be to text/plain using setType() method*/
//        mIntent.data = Uri.parse("mailto:")
//        mIntent.type = "text/plain"
//
//        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
//        //put the Subject in the intent
//        mIntent.putExtra(Intent.EXTRA_SUBJECT, "")
//        //put the message in the intent
//        mIntent.putExtra(Intent.EXTRA_TEXT, "")
//
//        try {
//            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
//        } catch (e: Exception) {
//            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
//        }
//    }
//
//    fun getLocation() {
//        if (ActivityCompat.checkSelfPermission(
//                this, android.Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//            return
//
//        } else {
//            /*get lat and long using last known location*/
//            val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//            val lastKnownLocationGPS =
//                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//            if (lastKnownLocationGPS != null) {
//                currentLatitude = lastKnownLocationGPS.latitude
//                currentLongitude = lastKnownLocationGPS.longitude
//                Utils.showProgressDialog(this, true)
//                mPresenter.getStoreLocation()
//
//            } else {
//                val loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
//                /*if (lastKnownLocationGPS != null) {*/
//                if (loc != null) {
//                    currentLongitude = loc.longitude
//                    currentLatitude = loc.latitude
//                    mPresenter.getStoreLocation()
//                }
//                /* }*/
//            }
//
//        }
//
//    }
//
//    override fun onMarkerClick(marker: Marker?): Boolean {
//        latitude = marker?.position?.latitude!!
//        longitude = marker.position?.longitude!!
//        Log.v("position", marker.position.toString())
//        Log.v("long", longitude.toString())
//        for (i in sList.indices) {
//            val input = sList[i].location
//            // val input = "12.972442, 77.580643"
//            println("=== String into List ===")
//            result = input.split(",").map { it.trim() }
//            if (result[0].toDouble() == latitude && result[1].toDouble() == longitude) {
//                phoneCall = if (sList[i].phoneno == null) "" else sList[i].phoneno
//                email = if (sList[i].email == null) "" else sList[i].email
//                shopImage = if (sList[i].logo_Url == null) "" else sList[i].logo_Url
//                address = if (sessionManager.languageselect == "en") {
//                    sList[i].address
//                } else {
//                    sList[i].address_Arabic
//                }
//                openTime = if (sessionManager.languageselect == "en") {
//                    sList[i].storeTimings
//                } else {
//                    if (sList[i].storeArabicTimings == null) "" else sList[i].storeArabicTimings
//                }
//
//
//            }
//        }
//        dialogPopUp(R.style.DialogAnimation, shopImage, latitude, longitude)
//        return true
//    }
//
//    private fun dialogPopUp(
//        dialogAnimation: Int,
//        shopImage: String,
//        latitude: Double,
//        longitude: Double
//    ) {
//        val layoutInflater: LayoutInflater = LayoutInflater.from(this)
//        val customView = layoutInflater.inflate(R.layout.dialog_map, null)
//        // Build the dialog
//        customDialog = Dialog(this, R.style.MyDialogTheme)
//        customDialog!!.setContentView(customView)
//        customDialog!!.setCancelable(false)
//        customDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        // dialog with bottom and with out padding
//        val window = customDialog!!.window
//        val wlp = window!!.attributes
//        wlp.gravity = Gravity.BOTTOM
//        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT
//        window.attributes = wlp
//        //animation
//        customDialog!!.window!!.attributes.windowAnimations = dialogAnimation //style id
//        customDialog!!.show()
//        Picasso.with(this).load(shopImage).into(customDialog!!.ivLogo)
//        val phone: String = BidiFormatter.getInstance().unicodeWrap(phoneCall)
//        customDialog!!.tv_store_mobile.text = email
//        customDialog!!.tv_store_timings.text =
//            getString(R.string.shoptiming) + " " + if (openTime.isNullOrEmpty()) "" else openTime
//
//        customDialog!!.tv_store_location.text = if (address.isNullOrEmpty()) "" else address
//        customDialog!!.btnDirection.setOnClickListener {
//            val intent = Intent(
//                Intent.ACTION_VIEW,
//                Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=" + latitude + "," + longitude)
//            )
//            startActivity(intent)
//        }
//        customDialog!!.img_dialog_close.setOnClickListener(this)
//        customDialog!!.tv_store_mobile.setOnClickListener(this)
//
//    }
//
//    @SuppressLint("MissingPermission")
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        when (requestCode) {
//            100 -> {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+91 5067236390"))
//                    startActivity(intent)
//                }
//                return
//            }
//            200 -> {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    getLocation()
//                }
//                return
//            }
//        }
//    }
//
//}
//
