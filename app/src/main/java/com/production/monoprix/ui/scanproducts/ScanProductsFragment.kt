package com.production.monoprix.ui.scanproducts


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.model.CountryModel
import com.production.monoprix.model.Data
import com.production.monoprix.model.StatusModel
import com.production.monoprix.mvp.BaseMvpFragment
import com.production.monoprix.room.AppDatabase
import com.production.monoprix.ui.barcodescanner.BarcodeScannerActivity
import com.production.monoprix.ui.home.HomeFragment
import com.production.monoprix.ui.howitworks.HowItWorksActivity
import com.production.monoprix.util.GPSTracker
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main_layout.*
import kotlinx.android.synthetic.main.fragment_scan_products.view.*
import kotlinx.android.synthetic.main.include_toolbar.view.*
import java.net.SocketException
import java.util.*


class ScanProductsFragment :
    BaseMvpFragment<ShopListFragmentContractor.View, ShopListFragmentContractor.Presenter>(),
    ShopListFragmentContractor.View, OnMapReadyCallback, View.OnClickListener,
    GoogleMap.OnMarkerClickListener, com.google.android.gms.location.LocationListener {

    private var staticLatLang: Boolean = false
    private var distance: Double = 0.0
    lateinit var mView: View
    lateinit var sessionManager: SessionManager
    override var mPresenter: ShopListFragmentContractor.Presenter = ShopListFragmentPresenter()
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null
    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    lateinit var storeId: String
    var lat: Double = 0.0
    var long: Double = 0.0

    var currentlat1: Double = 0.0
    var currentlog1: Double = 0.0

    private lateinit var googleApiClient: GoogleApiClient
    var sList: ArrayList<Data> = ArrayList<Data>()
    lateinit var db: AppDatabase
    lateinit var location: String
    lateinit var tvhowitworks: TextView
    lateinit var navigation: BottomNavigationView
    var mFusedLocationClient: FusedLocationProviderClient? = null
    var mLocationRequest: LocationRequest? = null
//    private var floatingMarkersOverlay: FloatingMarkerTitlesOverlay? = null

    fun newInstance(): ScanProductsFragment {
        val args = Bundle()

        val fragment = ScanProductsFragment()
        fragment.arguments = args
        return fragment
    }

    val MY_PERMISSIONS_REQUEST_LOCATION = 99

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_scan_products, container, false)
        init()
        return mView
    }

    var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                Log.e("MainActivity", "Location: " + location.latitude + " " + location.longitude)
                currentlat1 = location.latitude
                currentlog1 = location.longitude
//              Toast.makeText(context, getCompleteAddressString(location.latitude,location.longitude), Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun init() {

        db = AppDatabase(requireActivity())
        googleApiClient =
            GoogleApiClient.Builder(requireActivity()).addApi(LocationServices.API).build()
        sessionManager = SessionManager(requireActivity())
        currentLatitude = requireArguments().getDouble("latitude")
        currentLongitude = requireArguments().getDouble("longitude")
        /*currentLatitude = 25.286116 //todo change me
        currentLongitude = 51.526930 //todo change me*/
        checkLocationPermission()
//        floatingMarkersOverlay = mView.findViewById(R.id.map_floating_markers_overlay);

        navigation = requireActivity().findViewById(R.id.navigation) as BottomNavigationView
        requireActivity().include.visibility = View.GONE
        requireActivity().navigation.visibility = View.GONE
        if (currentLatitude == 0.0 && currentLongitude == 0.0) {
            val locationManager =
                requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val lastKnownLocationGPS =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocationGPS != null) {
                currentLatitude = lastKnownLocationGPS.latitude
                currentLongitude = lastKnownLocationGPS.longitude
                currentlat1 = lastKnownLocationGPS.latitude
                currentlog1 = lastKnownLocationGPS.longitude

            } else {
                val loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                if (loc != null) {
                    currentLongitude = loc.longitude
                    currentLatitude = loc.latitude
                    currentlat1 = loc.longitude
                    currentlog1 = loc.latitude
                }
            }
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        requestLocationUpdates();

        mView.img_left_arrow.setOnClickListener(this)
        mView.tv_store_start_scan.setOnClickListener(this)
        mView.tvhowitworks.setOnClickListener(this)

        // mPresenter.getStoreDetails("12.9718, 77.6552")
        mView.img_left_arrow.setOnClickListener {
//            activity!!.onBackPressed()
            //send it to home...
            val fragment = HomeFragment()
            addFragment(fragment)
        }


    }

    fun checkLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(requireActivity())
                    .setTitle("Locatin permission")
                    .setMessage("Location Permission")
                    .setPositiveButton(R.string.ok,
                        DialogInterface.OnClickListener { dialogInterface, i -> //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                MY_PERMISSIONS_REQUEST_LOCATION
                            )
                        })
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
            false
        } else {
            true
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {

                        //Request location updates:
                        requestLocationUpdates()
                    }
                }
                return
            }
        }
    }

    private fun addFragment(fragment: Fragment) {
//      mView.content.removeAllViewsInLayout()
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.design_bottom_sheet_slide_in,
                R.anim.design_bottom_sheet_slide_out
            )
            ?.replace(R.id.content, fragment, fragment.javaClass.simpleName)
            ?.commit()
    }

    override fun onResume() {
        super.onResume()
        mPresenter.getStoreLocation()
        if (mFusedLocationClient != null) {
            requestLocationUpdates()
        }
        Utils.showProgressDialog(requireActivity(), true)
        //     mPresenter.getStoreDetails("$currentLatitude,$currentLongitude")
    }

    fun requestLocationUpdates() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 100 // two minute interval
        mLocationRequest!!.fastestInterval = 100
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            mFusedLocationClient!!.getLastLocation().addOnSuccessListener { location ->
                if (location != null) {
                    currentlat1 = location.latitude //here
                    currentlog1 = location.longitude
                    /*currentlat1 = 25.285430908203125
                    currentlog1 = 51.52846908569336*/
                } else {
                    val gpsTracker = GPSTracker(activity)
                    currentlat1 = gpsTracker.latitude
                    currentlog1 = gpsTracker.longitude
                }

            }

        }
    }

    override fun onPause() {
        super.onPause()
        mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
    }

    override fun showStoreLocation(response: StatusModel?) {
        Utils.pd.dismiss()
        if (response!!.status == 200) {
            Log.e("stor_response", "data= " + response.data);
            mView.store_location_nested.visibility = View.VISIBLE
            mView.tv_store_location_no_data.visibility = View.GONE
            sessionManager.storeLocation = response.data.address
            sessionManager.storelocationarabic = response.data.address_Arabic
            if (sessionManager.languageselect!! == "en" || sessionManager.languageselect!!.isEmpty()) {
                mView.txt_title.text = response.data.userName + " - " + response.data.address
                mView.tv_store_location_name.text = response.data.address
            } else {
                mView.txt_title.text =
                    response.data.name_Arabic + " - " + response.data.address_Arabic
                mView.tv_store_location_name.text = response.data.address_Arabic
            }
            location = currentLatitude.toString() + "," + currentLongitude.toString()
//            location = "25.285430908203125,51.52846908569336"//Jaideep
            if (response.data.storeImg.isNotEmpty()) {
                var s: String = response.data.storeImg.replace(" ", "%20")
                Picasso.with(activity).load(s).into(mView.image_store_location)
            }

            storeId = response.data.clientStoreId
            val s = response.data.location.split(",")
            lat = s[0].toDouble()
            long = s[1].toDouble()
            val supportMapFragment =
                childFragmentManager.findFragmentById(R.id.store_location_map) as SupportMapFragment?
            supportMapFragment!!.getMapAsync(this)

        } else if (response.status == 402) {
            Utils.tokenExpire(requireActivity())
            Utils.showProgressDialog(requireActivity(), true)
            var latlog: String = currentlat1.toString() + "," + currentlog1.toString()
//            var latlog:String= "25.285430908203125,51.52846908569336"
            mPresenter.getStoreDetails(latlog)
            // mPresenter.getStoreDetails("12.9718, 77.6552")
        } else if (response.status == 401) {
            mView.store_location_nested.visibility = View.GONE
            mView.tv_store_location_no_data.visibility = View.VISIBLE
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
            requireActivity().onBackPressed()
        } else {
            mView.store_location_nested.visibility = View.GONE
            mView.tv_store_location_no_data.visibility = View.VISIBLE
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    override fun showStoreLocation(response: CountryModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {
            sList = response.data as ArrayList<Data>

            for (i in sList.indices) {
                if (sList[i].clientStoreId == 10002) {
                    val locationString = sList[i].location
                    val strArray = locationString.split(",").toTypedArray()
                    if (strArray.size > 0) {
                        val lat = strArray[0]
                        val log = strArray[1]
                        // val distance = distance(lat.toDouble(), log.toDouble(), latitude, longitude)
                        // val lalog=LatLng(lat.toDouble(),log.toDouble())
                        val startPoint = Location("locationA")
                        startPoint.latitude = currentlat1
                        startPoint.longitude = currentlog1

//                        startPoint.latitude = (activity as? HomeActivity)!!.currentLatitude
//                        startPoint.longitude = (activity as? HomeActivity)!!.currentLongitude

                        /*startPoint.latitude = 23.858423378092805
                          startPoint.longitude = 78.77985384317046*/

                        val endPoint = Location("locationA")
                        endPoint.latitude = lat.toDouble()
                        endPoint.longitude = log.toDouble()
                        distance = startPoint.distanceTo(endPoint).toDouble()

                        /* val currentLatlog=LatLng(currentlat1,currentlog1)
                         val distance2 = CalculationByDistance(lalog,currentLatlog)*/
                        Log.d("TAG", "distancePramod: " + distance)
                        Log.d("TAG", "currentLatlog: " + currentlat1 + " " + currentlog1)
                        Log.d("TAG", "storeLocation: " + lat + " " + log)

                        //todo change here...

//                        withStaticLatLang()
                        withDynamicLatLang(distance)

                    }
                }
            }
        } else if (response.status == 402) {
            Utils.tokenExpire(context)
            Utils.showProgressDialog(context, true)
            mPresenter.getStoreLocation()
        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    private fun withStaticLatLang() {

        staticLatLang = true
        mPresenter.getStoreDetails("25.285430908203125,51.52846908569336")
        mView.mainRl.visibility = View.VISIBLE
    }

    private fun withDynamicLatLang(distance: Double) {

        staticLatLang = false
        Log.d("24feb", currentlat1.toString() + "," + currentlog1.toString())

        if (distance > 100) {
            /*mView.mainRl.visibility = View.GONE*/
            mView.mainRl.visibility = View.VISIBLE
            Picasso.with(activity).load(ApiManager.STOREIMAGE).into(mView.image_store_location)
            showCurrentPlace(currentlat1, currentlog1)

            mView.tv_store_start_scan.background =
                resources.getDrawable(R.drawable.grey_rectangle_corner_back)
            dialog()
        } else {
            mView.tv_store_start_scan.background =
                resources.getDrawable(R.drawable.red_rectangle_corner_back)

            var latlog: String =
                currentlat1.toString() + "," + currentlog1.toString()
            mPresenter.getStoreDetails(latlog)
            // mPresenter.getStoreDetails("22.7383483,75.8322018")
            mView.mainRl.visibility = View.VISIBLE
        }
    }

    private fun showCurrentPlace(currentlat1: Double, currentlog1: Double) {

        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(currentlat1, currentlog1, 1)!!
        /* val stateName: String = addresses[1].getAddressLine(0)
         val countryName: String = addresses[1].getAddressLine(2)*/

        if (addresses.size > 0) {
            val cityName: String = addresses.get(0).getLocality()
            mView.tv_store_location_name.text =
                cityName + " - " + addresses.get(0).postalCode + " " + addresses.get(0).countryName
            mView.txt_title.text = cityName + " - " + addresses.get(0).postalCode
            System.out.println();
            Log.d("24feb", "addresses :" + addresses.get(0).getLocality())
            Log.d("24feb", "postalCode :" + addresses.get(0).postalCode)
            Log.d("24feb", "countryName :" + addresses.get(0).countryName)

        }


    }

    private fun dialog() {

        val builder = AlertDialog.Builder(context)
        builder.setMessage(R.string.this_feature_not)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, id ->
                /*activity!!.onBackPressed()*/

                /*val fragment = HomeFragment()
                addFragment(fragment)*/
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
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

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        mMap.setOnMarkerClickListener(this)
        mMap.uiSettings.isZoomControlsEnabled = true

        val location = LatLng(lat, long)
        val builder = LatLngBounds.Builder()
        builder.include(location);
        val bounds = builder.build()
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, 200)

        try {
            mMap.moveCamera(cu)
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f), 2000, null)
        } catch (e: Exception) {
        }
        moveToCurrentLocation(location)


    }

/*
    private fun createMarker(

        latitude: Double, longitude: Double, locationName: String
    ): Marker {
        return mMap.addMarker(
            MarkerOptions()
                .position(LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(locationName)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        createCustomMarker(activity!!, R.drawable.ic_loc)
                    )
                )
                .snippet("")
        )
//        floatingMarkersOverlay?.addMarker(id, mi);

    }
*/

    fun createCustomMarker(context: Context, @DrawableRes resource: Int): Bitmap {
        val marker = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(R.layout.custom_marker_layout, null)
        val markerImage = marker.findViewById(R.id.user_dp) as ImageView
        markerImage.setImageResource(resource)
        val txt_name = marker.findViewById(R.id.name) as TextView

        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        marker.setLayoutParams(ViewGroup.LayoutParams(40, ViewGroup.LayoutParams.WRAP_CONTENT))
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        val bitmap = Bitmap.createBitmap(
            marker.getMeasuredWidth(),
            marker.getMeasuredHeight(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        marker.draw(canvas)
        return bitmap
    }

    private fun moveToCurrentLocation(currentLocation: LatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17.0f))
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn())
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f), 2000, null)
        Log.e(
            "location",
            currentLocation.latitude.toString() + " ======= " + currentLocation.longitude.toString()
        )
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        latitude = p0?.position?.latitude!!
        longitude = p0.position?.longitude!!
        Log.v("position", p0.position.toString())
        Log.v("long", longitude.toString())
        mView.store_location_nested.visibility = View.VISIBLE
        mView.tv_store_location_no_data.visibility = View.GONE
//         Picasso.with(activity).load(sList.get(i).shop_image).into(mView.image_store_location)
        mView.tv_store_location_name.text = mView.tv_store_location_name.text.toString()
        return false
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_left_arrow -> {
                /*activity!!.onBackPressed()*/
                val fragment = HomeFragment()
                addFragment(fragment)
            }
            R.id.tvhowitworks -> {

                startActivity(Intent(requireActivity(), HowItWorksActivity::class.java))

            }

/*
            R.id.tv_store_start_scan -> {
                try {

                    if (distance > 100) {
                        */
/*dialog()*//*

                        //disable button color as well..


                    } else {

                        if (sessionManager.shopId!!.isNotEmpty()) {
                            if (sessionManager.shopId != storeId) {
                                alert()
                            } else {
                                sessionManager.shopId = storeId
                                val i =
                                    Intent(requireActivity(), BarcodeScannerActivity::class.java)
                                i.putExtra("title", mView.txt_title.text.toString())
                                i.putExtra("location", location)
                                startActivity(i)
                            }
                        } else {
                            sessionManager.shopId = storeId
                            val i = Intent(requireActivity(), BarcodeScannerActivity::class.java)
                            i.putExtra("title", mView.txt_title.text.toString())
                            i.putExtra("location", location)
                            startActivity(i)
                        }
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
*/

            // for static
            R.id.tv_store_start_scan -> {
                try {

                    if (sessionManager.shopId!!.isNotEmpty()) {

                        Log.d("25Nov:", "shopId :" + sessionManager.shopId.toString())
                        Log.d("25Nov:", "storeId :" + storeId)

                        if (sessionManager.shopId != storeId) {
                            alert()
                        } else {
                            val startPoint = Location("locationA")
                            startPoint.latitude = currentlat1
                            startPoint.longitude = currentlog1

                            val endpoint = Location("locationB")
                            endpoint.latitude = lat
                            endpoint.longitude = long

                            val distance = startPoint.distanceTo(endpoint).toDouble()

                            if (!staticLatLang) {

                                if (distance < 100) {
                                    performScan()
                                } else {
                                    alert()
                                }
                            } else {
                                performScan()
                            }


                        }
                    } else {
                        sessionManager.shopId = storeId
                        val i = Intent(requireActivity(), BarcodeScannerActivity::class.java)
                        i.putExtra("title", mView.txt_title.text.toString())
                        i.putExtra("location", location)
                        startActivity(i)
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }

    }

    private fun performScan() {

        sessionManager.shopId = storeId
        val i = Intent(requireActivity(), BarcodeScannerActivity::class.java)
        i.putExtra("title", mView.txt_title.text.toString())
        i.putExtra("location", location)
        startActivity(i)
    }

    fun alert() {
        val dialogBuilder = AlertDialog.Builder(requireActivity())
        // set message of alert dialog
        dialogBuilder.setMessage(getString(R.string.er_barcode))
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(
                getString(R.string.ok),
                DialogInterface.OnClickListener { dialog, id ->
                    db.todoDao().deleteAll()
                    sessionManager.shopId = storeId
                    val i = Intent(requireActivity(), BarcodeScannerActivity::class.java)
                    i.putExtra("title", mView.txt_title.text.toString())
                    i.putExtra("location", location)
                    startActivity(i)

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
    }

    override fun onLocationChanged(location: Location?) {
        Log.e(
            "location",
            location?.latitude.toString() + " ======= " + location?.longitude.toString()
        )
    }

}
