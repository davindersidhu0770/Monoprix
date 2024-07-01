package com.production.monoprix.ui.home


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.production.monoprix.MyApplication
import com.production.monoprix.R
import com.production.monoprix.ui.login.LoginActivity
import com.production.monoprix.ui.loyalty.LoyaltyFragment
import com.production.monoprix.ui.navigation.DrawerFragment
import com.production.monoprix.ui.scanproducts.ScanProductsFragment
import com.production.monoprix.ui.shoplist.DeleteMyListActivity
import com.production.monoprix.ui.signup.SignUpActivity
import com.production.monoprix.ui.store_location.StoreLocationFragment
import com.production.monoprix.ui.webview.WebviewFragment
import com.production.monoprix.util.ForceUpdateChecker
import com.production.monoprix.util.GpsUtils
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import com.production.monoprix.util.Utils.Companion.iaDrawerOpen
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main_layout.*
import kotlinx.android.synthetic.main.activity_shop_list.*
import kotlinx.android.synthetic.main.dialog_language.*
import kotlinx.android.synthetic.main.include_home_header.*
import org.json.JSONObject
import java.util.*


class HomeActivity : AppCompatActivity(), View.OnClickListener,
    ForceUpdateChecker.OnUpdateNeededListener {

    private var drawerFragment: DrawerFragment? = null
    lateinit var sessionManager: SessionManager
    lateinit var loginAlertDialog: AlertDialog
    var LOCATION_PERMISSION_REQUEST_CODE: Int = 200
    var currentLatitude: Double = 0.0
    var currentLongitude: Double = 0.0
    var filtercustomDialog: Dialog? = null
    private var position: Int = 0
    public var comingFrom: String = ""
    private var isGPS = false
    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 3000 //3 seconds
    var flexiData: String = ""
    var flexiBoolean: Boolean = false
    private val REQUEST_CODE_UPDATE = 200
    var backarrow: Boolean = false

    var mFusedLocationClient: FusedLocationProviderClient? = null
    var mLocationRequest: LocationRequest? = null
    var txtlocation: TextView? = null
    lateinit var leftBackIcon: AppCompatImageView
    private val POST_NOTIFICATIONS_PERMISSION_REQUEST_CODE = 22

    companion object {
        private var instance: HomeActivity? = null

        fun getInstance(): HomeActivity? {
            return instance
        }
    }

    val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            Utils.pd.dismiss()
            val bundle = Bundle()
            bundle.putDouble("latitude", 0.0)
            bundle.putDouble("longitude", 0.0)
//            bundle.putDouble("latitude", 25.3239007)
//            bundle.putDouble("longitude", 51.5338199)
            val fragment = ScanProductsFragment().newInstance()
            fragment.arguments = bundle
            addFragment(fragment)
        }
    }

    var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                Log.e("MainActivity", "Location: " + location.latitude + " " + location.longitude)
                currentLongitude = location.longitude
                currentLatitude = location.latitude
            }
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this

        setContentView(R.layout.activity_main_layout)
        sessionManager = SessionManager(this)

        // Handle dynamic link in your activity

        init()


        /*flexi popup*/
        /* if (intent.getBooleanExtra("from_splash", false)) {
           flexiPopUp()
             ForceUpdateChecker.with(this).onUpdateNeeded(this).check()
              inAppUpdate()
         }*/

        /*  FacebookSdk.sdkInitialize(getApplicationContext())
          AppEventsLogger.activateApp(this)
  */
    }

    private fun handleDeepLink(intent: Intent) {
        if (Intent.ACTION_VIEW == intent.action) {
            val data = intent.data
            if (data != null) {
                val path = data.path
                // Handle the deep link path as needed
                Log.d("DeepLink", "Path: $path")

                // Example: Display a toast with the path
                Toast.makeText(this, "DeepLink Path: $path", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun inAppUpdate() {
        val updateManager = AppUpdateManagerFactory.create(this)
        val listener = InstallStateUpdatedListener {
            if (it.installStatus() == InstallStatus.DOWNLOADED) {
                Snackbar.make(
                    findViewById(R.id.drawer_layout),
                    getString(R.string.updatejust),
                    Snackbar.LENGTH_INDEFINITE
                ).apply {
                    setAction(getString(R.string.restart)) { updateManager.completeUpdate() }
                    setActionTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    show()
                }
            }

        }
        updateManager.registerListener(listener)
        updateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                it.isUpdateTypeAllowed(IMMEDIATE)
            ) {
                updateManager.startUpdateFlowForResult(
                    it,
                    IMMEDIATE,
                    this,
                    REQUEST_CODE_UPDATE
                )
            }

        }

        updateManager.unregisterListener(listener)
    }

    private fun flexiPopUp() {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)//this refresh the remoteconfig cache
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.fetch(0)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    remoteConfig.activate()
                } else {
/*
                    Toast.makeText(
                        this, "Fetch failed",
                        Toast.LENGTH_SHORT
                    ).show()
*/
                }
            }
        flexiData = remoteConfig.getString("flexi_popup_details")
        flexiBoolean = remoteConfig.getBoolean("show_flexi_popup")
        val dialog = Dialog(this)
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.flexi_popup_layout)
        dialog.setCancelable(true)
        val heading = dialog.findViewById(R.id.heading) as TextView
        val desc = dialog.findViewById(R.id.description) as TextView
        val img = dialog.findViewById(R.id.image) as ImageView
        val positiveBtn = dialog.findViewById(R.id.positive_btn) as TextView
        val negativeBtn = dialog.findViewById(R.id.negative_btn) as TextView
        val img_cross = dialog.findViewById(R.id.img_cross) as ImageView

        if (flexiBoolean) {
            try {
                val responseData = JSONObject(flexiData)
                if (responseData.getString("heading").isNotEmpty()) {
                    heading.visibility = View.VISIBLE
                    heading.text = responseData.getString("heading")
                } else {
                    heading.visibility = View.GONE
                }
                if (responseData.getString("description").isNotEmpty()) {
                    desc.text = responseData.getString("description")
                    desc.visibility = View.VISIBLE
                } else {
                    desc.visibility = View.GONE
                }
                if (responseData.getString("posstive_btn").isNotEmpty()) {
                    positiveBtn.text = responseData.getString("posstive_btn")
                    positiveBtn.visibility = View.VISIBLE
                } else {
                    positiveBtn.visibility = View.GONE
                }
                if (responseData.getString("negative_btn").isNotEmpty()) {
                    negativeBtn.text = responseData.getString("negative_btn")
                    negativeBtn.visibility = View.VISIBLE
                } else {
                    negativeBtn.visibility = View.GONE
                }
                if (responseData.getString("img_url").isNotEmpty()) {
                    img.visibility = View.VISIBLE
                    Picasso.with(this)
                        .load(responseData.getString("img_url"))
                        .into(img)
                } else {
                    img.visibility = View.GONE
                }
                if (responseData.getString("show_cross_button").isNotEmpty()) {
                    img_cross.visibility = View.VISIBLE
                } else {
                    img_cross.visibility = View.GONE
                }
                dialog.show()
                positiveBtn.setOnClickListener {
                    dialog.dismiss()
                }
                negativeBtn.setOnClickListener {
                    dialog.dismiss()
                }
                img_cross.setOnClickListener {
                    dialog.dismiss()
                }
            } catch (e: Exception) {

            }
        }

    }

    fun init() {
        //txt_location.text = sessionManager.storeLocation
        txtlocation = txt_location
        position = intent.getIntExtra("position", 0)
        /*bottom navigation*/

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        if (position == 1) {
            Log.d("24Jan", "position" + position)

            navigation.selectedItemId = R.id.navigation_visit
        } else {
            Log.d("24Jan", "position" + position)

            navigation.selectedItemId = R.id.navigation_home
        }

        /*navigation drawer*/
        drawerFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_navigation_drawer) as DrawerFragment
        drawerFragment!!.setUpDrawer(
            R.id.fragment_navigation_drawer,
            this.findViewById(R.id.drawer_layout)
        )

        /*onclick listener*/
        img_left_arrow.setOnClickListener(this)
        img_three_dots.setOnClickListener(this)
        txt_lan.setOnClickListener(this)
        if (sessionManager.languageselect == "مع") {
            txt_lan.text = "مع"
        } else {
            txt_lan.text = "En"
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationUpdates()

        if (sessionManager.notiAsked.equals("no")) {

            sessionManager.notiAsked = "Yes"

            if (Build.VERSION.SDK_INT >= 33) {
                checkAndRequestNotificationPermission()

                Log.d("16Jan", "checkAndRequest")

            }
        }

    }

    fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
//                showExplanationDialog()
                requestPostNotificationsPermission()
            } else {

                Log.d("10Jan", "ELSE")

            }
        }
    }

    private fun requestPostNotificationsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
        ) {
            // Show another explanation dialog or handle it accordingly
//            showSettingsDialog()
            showExplanationDialog()

            Log.d("10Jan", "showExplanationDialog")

        } else {

            Log.d("10Jan", "else requestcode")

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                POST_NOTIFICATIONS_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun showExplanationDialog() {
        val customDialogView = layoutInflater.inflate(R.layout.custom_alert_title, null)
        val customTitleTextView = customDialogView.findViewById<TextView>(R.id.customTitleTextView)
        val customMessageTextView =
            customDialogView.findViewById<TextView>(R.id.customMessageTextView)
        val customSkipButton = customDialogView.findViewById<TextView>(R.id.customSkipButton)
        val customAllowButton = customDialogView.findViewById<TextView>(R.id.customAllowButton)

        customTitleTextView.text = getString(R.string.notificationtitle)
        customMessageTextView.text = getString(R.string.notificationmessage)

        var alertDialog: AlertDialog;
        alertDialog = AlertDialog.Builder(this)
            .setView(customDialogView)
            .setCancelable(false)
            .show()

        customSkipButton.setOnClickListener(View.OnClickListener {
            alertDialog.dismiss()
        })

        customAllowButton.setOnClickListener(View.OnClickListener {
            alertDialog.dismiss()
            navigateToAppSettings()

        })
    }

    fun requestLocationUpdates() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 100 // two minute interval
        mLocationRequest!!.fastestInterval = 100
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                Looper.myLooper()
            )
        }
    }

    override fun onPause() {
        super.onPause()


//      sessionManager.notiAsked= "no"

/*
        Toast.makeText(
            this, "ONPAUSEEEEEE.",
            Toast.LENGTH_SHORT
        ).show()
*/

        mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_left_arrow -> {
                /*navigation drawer*/
                if (backarrow) {
                    onBackPressed()
                } else {
                    drawer_layout.openDrawer(GravityCompat.START)
                }

            }
            R.id.img_three_dots -> {
                popupWindow(R.style.DialogAnimation1)
            }
            R.id.linear_delete_list -> {
                if (sessionManager.totalCart != "0") {
                    filtercustomDialog!!.dismiss()
                    include.visibility = View.VISIBLE
                    txt_location.visibility = View.INVISIBLE
                    txt_lan.visibility = View.GONE
                    img_three_dots.visibility = View.GONE
                    val fragment = DeleteMyListActivity()
                    addFragment(fragment)
                    /* val i = Intent(this, DeleteMyListActivity::class.java)
                     startActivityForResult(i, 300)*/
                }
            }
            R.id.linear_unchecklist -> {
                filtercustomDialog!!.dismiss()
            }
            R.id.linear_monoprix -> {
                content.removeAllViewsInLayout()
//                navigation.selectedItemId = R.id.navigation_scan //todo 13oct
                include.visibility = View.GONE
                filtercustomDialog!!.dismiss()
                // getLocationPermission()
            }
            R.id.txt_lan -> {
                langPopupwindow(R.style.DialogAnimation)
            }
            R.id.c1 -> {
                filtercustomDialog!!.readio_button.isChecked = true
                filtercustomDialog!!.readio_button1.isChecked = false
                filtercustomDialog!!.dismiss()
                sessionManager.languageselect = "en"
//              selection("en")
                setNewLocale("en", true)
//              restartActivity()
//              val i = Intent(this@HomeActivity, HomeActivity::class.java)
//              i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//              startActivity(i)
            }
            R.id.c2 -> {
                filtercustomDialog!!.readio_button1.isChecked = true
                filtercustomDialog!!.readio_button.isChecked = false
                filtercustomDialog!!.txt_english.text = getString(R.string.english)
                filtercustomDialog!!.dismiss()
                sessionManager.languageselect = "مع"
//              selection("ar")
//              restartActivity()
                setNewLocale("ar", true)
//              val i = Intent(this@HomeActivity, HomeActivity::class.java)
//              i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//              startActivity(i)
            }

        }
    }

    private fun popupWindow(animationSource: Int) {
        val inflater = layoutInflater
        val customView = inflater.inflate(R.layout.activity_shop_list, null)
        // Build the dialog
        filtercustomDialog = Dialog(this, R.style.MyDialogTheme)
        filtercustomDialog!!.setContentView(customView)
        // dialog with bottom and with out padding
        val window = filtercustomDialog!!.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.TOP or if (sessionManager.languageselect == "مع") {
            Gravity.START
        } else {
            Gravity.END
        }
        window.attributes = wlp
        //animation
        // filtercustomDialog!!.getWindow()!!.getAttributes().windowAnimations = animationSource //style id
        filtercustomDialog!!.show()
        filtercustomDialog!!.linear_delete_list.setOnClickListener(this)
        filtercustomDialog!!.linear_unchecklist.setOnClickListener(this)
        filtercustomDialog!!.linear_monoprix.setOnClickListener(this)
        if (sessionManager.totalCart == "0") {
            filtercustomDialog!!.linear_delete_list.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorgray
                )
            )
        } else {
            filtercustomDialog!!.linear_delete_list.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.delete
                )
            )
        }
    }


    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    backarrow = false
                    include.visibility = View.VISIBLE
                    img_left_arrow.setImageResource(R.drawable.menu)
                    txt_title.text = getString(R.string.home_st)
                    txt_title.setTextColor(ContextCompat.getColor(this, R.color.white))
                    //txt_location.visibility = View.VISIBLE
                    txt_lan.visibility = View.VISIBLE
                    img_three_dots.visibility = View.GONE
                    val fragment = HomeFragment()
                    addFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_visit -> {
                    include.visibility = View.VISIBLE
//                    txt_title.text = getString(R.string.loyalbalance)
                    txt_title.text = getString(R.string.home_st)
                    txt_title.setTextColor(ContextCompat.getColor(this, R.color.white))
                    txt_location.visibility = View.INVISIBLE
                    txt_lan.visibility = View.VISIBLE
                    img_three_dots.visibility = View.GONE
                    val fragment = StoreLocationFragment()
                    addFragment(fragment)
//                  startActivity(Intent(this@HomeActivity, StoreLocation::class.java))

                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_shop -> {
                    include.visibility = View.VISIBLE
//                  txt_title.text = getString(R.string.loyalbalance)
                    txt_title.text = getString(R.string.home_st)
                    txt_title.setTextColor(ContextCompat.getColor(this, R.color.white))
                    txt_location.visibility = View.INVISIBLE
                    txt_lan.visibility = View.VISIBLE
                    img_three_dots.visibility = View.GONE
                    val fragment = WebviewFragment()
                    //sessionManager.shopOnline
                    val args = Bundle()
                    args.putString("weblink", sessionManager.shopOnline)
                    fragment.setArguments(args)
                    addFragment(fragment)

                    // open webview here...

                    return@OnNavigationItemSelectedListener true
                }

/*
                R.id.navigation_shlist -> {
                    include.visibility = View.VISIBLE
                    txt_title.text = getString(R.string.home_st)
                    txt_title.setTextColor(ContextCompat.getColor(this, R.color.white))
                    txt_location.visibility = View.INVISIBLE
                    txt_lan.visibility = View.VISIBLE
                    img_three_dots.visibility = View.GONE
                    if(comingFrom.contentEquals("promotions")){
                        val fragment = PromotionsFragment()
                        addFragment(fragment)

                    }

                    else{

                        val fragment = WebviewFragment()
                        val args = Bundle()
                        args.putString("weblink", ApiManager.MRESTAURANTWEBLINK)
                        fragment.setArguments(args)
                        addFragment(fragment)

                    }

                    // open webview here...

                    return@OnNavigationItemSelectedListener true
                }
*/


                //pending....

                //todo13oct
/*
                R.id.navigation_shlist -> {

                    if (sessionManager.userId.toString().isNotEmpty()) {//shyam implement this 24/05/2021
                        include.visibility = View.GONE
                        backarrow = true
                        img_left_arrow.setImageResource(R.drawable.ic_arrow_left)
                        txt_title.text = getString(R.string.myshopping)
                        txt_title.setTextColor(ContextCompat.getColor(this, R.color.black))
                       /// txt_location.visibility = View.INVISIBLE
                        txt_lan.visibility = View.INVISIBLE
                        img_three_dots.visibility = View.GONE
//                        val fragment = ShopListFragment()//1
                        val fragment = MyShopListFragment()
                        addFragment(fragment)
                    }
                    else {

                        val loginIntent = Intent(this, LoginActivity::class.java)
                        startActivityForResult(loginIntent, 1)
                        // loginAlertBox(getString(R.string.alert_shoplist))
                    }
                        return@OnNavigationItemSelectedListener true


                }
*/

/*
                R.id.navigation_scan -> {
                    content.removeAllViewsInLayout()
                    if (sessionManager.userId.toString().isNotEmpty()) {
                        include.visibility = View.GONE
                        checkGooglePlayServices()

                    } else {
                        val loginIntent = Intent(this, LoginActivity::class.java)
                        startActivityForResult(loginIntent, 1)
                        // loginAlertBox(getString(R.string.alert_shoplist))
                    }
                    return@OnNavigationItemSelectedListener true
                }
*/

                R.id.navigation_loylitybalance -> {
                    if (sessionManager.userId.toString().isNotEmpty()) {
                        include.visibility = View.VISIBLE
                        txt_title.visibility = View.GONE
                        ivheader_icon.visibility = View.VISIBLE

                        txt_title.text = getString(R.string.home_st)
                        txt_title.setTextColor(ContextCompat.getColor(this, R.color.white))
                        txt_location.visibility = View.INVISIBLE
                        txt_lan.visibility = View.VISIBLE
                        img_three_dots.visibility = View.GONE
                        val fragment = LoyaltyFragment()
                        addFragment(fragment)
                    } else {
                        val loginIntent = Intent(this, LoginActivity::class.java)
                        startActivityForResult(loginIntent, 1)
                        // loginAlertBox(getString(R.string.alert_shoplist))
                    }
                    return@OnNavigationItemSelectedListener true
                }
            }
            false

        }

    fun openURL(strUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(strUrl)
        startActivity(Intent.createChooser(intent, "Open with"))
    }

    @SuppressLint("MissingPermission")
    private fun getLatLang() {
        /*get lat and long using last known location*/
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val lastKnownLocationGPS =
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (currentLatitude != 0.0 && currentLongitude != 0.0) {
//            currentLatitude = lastKnownLocationGPS.latitude
//            currentLongitude = lastKnownLocationGPS.longitude

            val bundle = Bundle()
            bundle.putDouble("latitude", currentLatitude)
            bundle.putDouble("longitude", currentLongitude)
//            Toast.makeText(this@HomeActivity, "home \n "+currentLatitude.toString()+" ========= "+currentLongitude.toString(), Toast.LENGTH_SHORT).show()
//            bundle.putDouble("latitude", 25.3239007)
//            bundle.putDouble("longitude", 51.5338199)

            val fragment = ScanProductsFragment().newInstance()
            fragment.arguments = bundle
            addFragment(fragment)


        } else {
            val loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            if (loc != null) {
                currentLongitude = loc.longitude
                currentLatitude = loc.latitude
                val bundle = Bundle()
                bundle.putDouble("latitude", currentLatitude)
                bundle.putDouble("longitude", currentLongitude)
//                bundle.putDouble("latitude", 25.3239007)
//                bundle.putDouble("longitude", 51.5338199)

                val fragment = ScanProductsFragment().newInstance()
                fragment.arguments = bundle
                addFragment(fragment)
            } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Utils.showProgressDialog(this, true)
                //Initialize the Handler
                mDelayHandler = Handler()
                //Navigate with delay
                mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)

            } else {
                GpsUtils(this).turnGPSOn { isGPSEnable ->
                    // turn on GPS
                    isGPS = isGPSEnable
                }
            }
        }
    }


    /*location-permission*/
    private fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return

        } else {
            getLatLang()

        }

    }

    private fun addFragment(fragment: Fragment) {
        content.removeAllViewsInLayout()
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.design_bottom_sheet_slide_in,
                R.anim.design_bottom_sheet_slide_out
            )
            .replace(R.id.content, fragment, fragment.javaClass.simpleName)
            .commit()
    }

    override fun onBackPressed() {
        when {
            iaDrawerOpen -> {
                drawer_layout.closeDrawers()
                iaDrawerOpen = false
            }
            /*bottom navigation*/
            R.id.navigation_home != navigation.selectedItemId -> navigation.selectedItemId =
                R.id.navigation_home
            else -> {
                backarrow = false
                val fragmentInstance = supportFragmentManager.findFragmentById(R.id.content)

                if (fragmentInstance is ScanProductsFragment) {
                    val fragment = HomeFragment()
                    addFragment(fragment)
                    Log.d("24Nov:", "ScanProductsFragment fragg")

                } else if (fragmentInstance is HomeFragment) {
                    finishAffinity();
                    Log.d("24Nov:", "HomeFragment fragg")

                } else {
                    Log.d("24Nov:", "else homefrag")
                    super.onBackPressed()
                }


            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (Utils.isContactUs) {
            drawer_layout.closeDrawers()
            iaDrawerOpen = false
            Utils.isContactUs = false

        }
    }

    /*private fun loginAlertBox(msg: String) {
        content.removeAllViewsInLayout()
        val li = LayoutInflater.from(this)
        val popupDialog = li.inflate(R.layout.login_alert_dialogue, null)
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(popupDialog)
        loginAlertDialog = alertDialogBuilder.create()
        loginAlertDialog.setCancelable(false)
        loginAlertDialog.setCanceledOnTouchOutside(false)
        loginAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        popupDialog.txt_alert.text = msg
        popupDialog.txt_sign_in.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivityForResult(loginIntent, 1)
            loginAlertDialog.dismiss()
        }

        popupDialog.txt_sign_in_required.setOnClickListener { loginAlertDialog.dismiss() }
        alertDialogBuilder.setCancelable(true)
        loginAlertDialog.setCancelable(true)
        loginAlertDialog.show()
    }*/

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocationPermission()
                }
                return
            }
            POST_NOTIFICATIONS_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    showSettingsDialog()
                    Log.d("10Jan", "IF Packagee manager ")

//                    navigateToAppSettings()
                } else {
                    Log.d("10Jan", "else settings ")

                }
            }

        }
    }

    private fun navigateToAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", "com.production.monoprix", null)
        intent.data = uri
        startActivity(intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // check if the request code is same as what is passed  here it is 200 after delete
        if (requestCode == 1 && resultCode == -1) {
            if (navigation.selectedItemId == R.id.navigation_home) {
                include.visibility = View.VISIBLE
                txt_title.text = getString(R.string.home_st)
                txt_title.setTextColor(ContextCompat.getColor(this, R.color.white))
                // txt_location.visibility = View.VISIBLE
                txt_lan.visibility = View.VISIBLE
                img_three_dots.visibility = View.GONE
                val fragment = HomeFragment()
                addFragment(fragment)
            }

            /* else if (navigation.selectedItemId == R.id.navigation_shlist) {
                 include.visibility = View.VISIBLE
                 txt_title.text = getString(R.string.myshopping)
                 txt_title.setTextColor(ContextCompat.getColor(this, R.color.black))
                 txt_location.visibility = View.INVISIBLE
                 txt_lan.visibility = View.INVISIBLE
                 img_three_dots.visibility = View.GONE
                 val fragment = MyShopListFragment()
                 addFragment(fragment)

             } */
            /*else if (navigation.selectedItemId == R.id.navigation_scan) {
                include.visibility = View.GONE
                *//*location-permission*//*
                getLocationPermission()
            }*/ else if (navigation.selectedItemId == R.id.navigation_loylitybalance) {
                include.visibility = View.VISIBLE
                txt_title.text = getString(R.string.loyalbalance)
                txt_title.setTextColor(ContextCompat.getColor(this, R.color.white))
                txt_location.visibility = View.INVISIBLE
                txt_lan.visibility = View.GONE
                img_three_dots.visibility = View.GONE
                val fragment = LoyaltyFragment()
                addFragment(fragment)
            }

        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1001) {
                isGPS = true
                getLatLang()
            }
        } else if (requestCode == 300) {
//            navigation.selectedItemId = R.id.navigation_shlist
        } else if (resultCode == 1000) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(
                    this, "Google Play Services must be installed.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            return
        } else if (requestCode == REQUEST_CODE_UPDATE) {
            if (requestCode != RESULT_OK) {
                inAppUpdate()
            }
        } else {
            navigation.selectedItemId = R.id.navigation_home
        }

    }

/*
    private fun checkGooglePlayServices() {
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        if (status != ConnectionResult.SUCCESS) {
            Log.e("googleplayservice", GoogleApiAvailability.getInstance().getErrorString(status))
            // ask user to update google play services.
            val dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, status, 1000)
            dialog?.show()
        } else {
            if (status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
                Log.e(
                    "googleplayservice",
                    GoogleApiAvailability.getInstance().getErrorString(status)
                )
                // ask user to update google play services.
                val dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, status, 1000)
                dialog?.show()
            } else {
                Log.i(
                    "googleplayservice",
                    GoogleApiAvailability.getInstance().getErrorString(status)
                )
                // google play services is updated.
                */
/*location-permission*//*

                getLocationPermission()
            }

        }
    }
*/

    private fun dialogBox() {
        // Late initialize an alert dialog object
        lateinit var dialog: AlertDialog
        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(this)
        // Set a message for alert dialog
        builder.setMessage(getString(R.string.turnon))
        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    dialog.dismiss()
                    getLatLang()
                }
            }
        }
        // Set the alert dialog positive/yes button
        builder.setPositiveButton("OK", dialogClickListener)
        dialog = builder.create()
        // Finally, display the alert dialog
        dialog.show()
    }

    private fun langPopupwindow(animationSource: Int) {
        val inflater = layoutInflater
        val customView = inflater.inflate(R.layout.dialog_language, null)
        // Build the dialog
        filtercustomDialog = Dialog(this, R.style.MyDialogTheme)
        filtercustomDialog!!.setContentView(customView)
        // dialog with bottom and with out padding
        val window = filtercustomDialog!!.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.TOP
        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT
        window.attributes = wlp
        var closeDialog = filtercustomDialog!!.findViewById(R.id.closeArrow) as ImageView
        closeDialog.setOnClickListener {
            filtercustomDialog!!.dismiss()
        }
        filtercustomDialog!!.txt_arbic.text = "عربى"
        filtercustomDialog!!.txt_english.text = getString(R.string.english)

        //animation
        filtercustomDialog!!.window!!.attributes.windowAnimations = animationSource //style id
        filtercustomDialog!!.show()
        filtercustomDialog!!.c1.setOnClickListener(this)
        filtercustomDialog!!.c2.setOnClickListener(this)
        if (sessionManager.languageselect == "مع") {
            filtercustomDialog!!.readio_button1.isChecked = true
            filtercustomDialog!!.txt_arbic.setTextColor(ContextCompat.getColor(this, R.color.theme))

        } else {
            filtercustomDialog!!.readio_button.isChecked = true
            filtercustomDialog!!.txt_english.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.theme
                )
            )
        }

    }

    private fun setNewLocale(language: String, restartProcess: Boolean): Boolean {
        MyApplication.localeManager?.setNewLocale(this, language)
        restartApplication(this)
        return true
    }

    fun restartApplication(@NonNull activity: Activity) {
        val pm = activity.packageManager
        val intent = pm.getLaunchIntentForPackage(activity.packageName)
        activity.finishAffinity() // Finishes all activities.
        activity.startActivity(intent) // Start the launch activity
        System.exit(0) // System finishes and automatically relaunches us.
    }

    private fun selection(s: String) {
        val activityRes = resources
        val activityConf = activityRes.configuration
        val newLocale = Locale(s)
        activityConf.setLocale(newLocale)
        activityRes.updateConfiguration(activityConf, activityRes.displayMetrics)

        val applicationRes = applicationContext.resources
        val applicationConf = applicationRes.configuration
        applicationConf.setLocale(newLocale)
        applicationRes.updateConfiguration(
            applicationConf, applicationRes.displayMetrics
        )

    }

    override fun onResume() {
        super.onResume()

        /*app update*/
/*
        Toast.makeText(
            this, "onResume.",
            Toast.LENGTH_SHORT
        ).show()
*/

        Log.d("2Jan", "onResume")

        if (intent.getBooleanExtra("from_splash", false)) {
            ForceUpdateChecker.with(this).onUpdateNeeded(this).check()
            Log.d("2Jan", "ForceUpdateChecker")

        }
        if (mFusedLocationClient != null) {
            requestLocationUpdates()
        }
        val updateManager = AppUpdateManagerFactory.create(this)
        updateManager.appUpdateInfo
            .addOnSuccessListener {
                if (it.updateAvailability() ==
                    UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    updateManager.startUpdateFlowForResult(
                        it,
                        IMMEDIATE,
                        this,
                        REQUEST_CODE_UPDATE
                    )
                }
            }
        if (sessionManager.languageselect == "en") {
            txt_location.text = sessionManager.storeLocation
        } else {
            txt_location.text = sessionManager.storelocationarabic
        }
    }

    override fun onUpdateNeeded(
        updateMsg: String,
        softUpdate: Boolean,
        forceUpdate: Boolean,
        storeUrl: String
    ) {

        if (forceUpdate) {
            val dialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.updateavail))
                .setMessage(updateMsg)
                .setCancelable(false)
                .setPositiveButton(
                    getString(R.string.update_avail)
                ) { dialog, which -> redirectStore(storeUrl) }.create()
            dialog.show()
        } else if (softUpdate) {
            val dialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.updateavail))
                .setMessage(updateMsg)
                .setCancelable(false)
                .setPositiveButton(
                    getString(R.string.update_avail)
                ) { dialog, which -> redirectStore(storeUrl) }.setNegativeButton(
                    getString(R.string.skip)
                ) { dialog, which -> dialog.dismiss() }.create()
            dialog.show()
        }
    }

    private fun redirectStore(storeUrl: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(storeUrl)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


}