package com.production.monoprix.ui.home


import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.daimajia.slider.library.Animations.DescriptionAnimation
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.daimajia.slider.library.Tricks.ViewPagerEx
import com.facebook.login.LoginManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.production.monoprix.MyApplication
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.model.*
import com.production.monoprix.mvp.BaseMvpFragment
import com.production.monoprix.room.AppDatabase
import com.production.monoprix.ui.account.MyAccountActivity
import com.production.monoprix.ui.imageview.ImageActivity
import com.production.monoprix.ui.login.LoginActivity
import com.production.monoprix.ui.promotions.PromotionsActivity
import com.production.monoprix.ui.scanproducts.ScanProductsFragment
import com.production.monoprix.ui.video.FullvideoActivity
import com.production.monoprix.ui.webview.CommonWebviewActivity
import com.production.monoprix.ui.webview.WebviewFragment
import com.production.monoprix.util.*
import kotlinx.android.synthetic.main.activity_homepage.view.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.SocketException
import java.net.URL


class HomeFragment : BaseMvpFragment<HomeContractor.View, HomeContractor.Presenter>(),
    HomeContractor.View,
    BaseSliderView.OnSliderClickListener,
    ViewPagerEx.OnPageChangeListener, MyMediaController.MediaPlayerControl {

    var mediaC: MediaController? = null
    var player: MediaPlayer? = null
    var controller: MyMediaController? = null
    var videoVolume: Int = 6
    lateinit var audio: AudioManager
    override var mPresenter: HomeContractor.Presenter = HomePresenter()

    lateinit var mLinearLayoutManager: LinearLayoutManager
    lateinit var mAdapter: HomeAdapter
    var mBannerList: MutableList<BannerModel> = java.util.ArrayList<BannerModel>()
    lateinit var sessionManager: SessionManager
    lateinit var mView: View
    lateinit var loginAlertDialog: AlertDialog
    lateinit var navigation: BottomNavigationView
    lateinit var textSliderView: DefaultSliderView
    lateinit var db: AppDatabase
    var isFistTime: Boolean = true
    public val WRITE_REQUEST_CODE = 300
    var folder: String = ""
    var LOCATION_PERMISSION_REQUEST_CODE: Int = 200
    var currentLatitude: Double = 0.0
    var currentLongitude: Double = 0.0
    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 3000 //3 seconds
    private var isGPS = false
    var promotionLink: String = ""

    //    private val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
    private val MY_PERMISSIONS_REQUEST_LOCATION = 99

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mView = inflater.inflate(R.layout.activity_homepage, container, false)
        init()
        return mView
    }
/*

    override fun onDestroy() {
        super.onDestroy()

    }*/

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun init() {
//      calculateDiffTime()
        folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString() + "/MONOPRIX/"
        sessionManager = SessionManager(requireActivity())
        audio = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, videoVolume, 0)
        val include = requireActivity().findViewById(R.id.include) as View
        val txt_title = requireActivity().findViewById(R.id.txt_title) as TextView
        val txt_location = requireActivity().findViewById(R.id.txt_location) as TextView
        val txt_lan = requireActivity().findViewById(R.id.txt_lan) as TextView
        val img_three_dots = requireActivity().findViewById(R.id.img_three_dots) as ImageView
        navigation = requireActivity().findViewById(R.id.navigation) as BottomNavigationView
        navigation.visibility = View.VISIBLE
        include.visibility = View.VISIBLE
        /*recyclerview*/
//      mLinearLayoutManager = LinearLayoutManager(requireActivity())

        // show only one time ...


        mLinearLayoutManager = GridLayoutManager(requireActivity(), 2)
//        request_notification_api13_permission()
        mView.home_recyclerview.layoutManager = mLinearLayoutManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            populateList()
        }

//      checkAndOpenNotificationSettings("channel-01")

        /*Utils.showProgressDialogBanner(this.requireContext(), true)*/
        mPresenter.promotionOnline()
        /*slider api call*/
        /*
        mPresenter.loadBanner()*/

        if ((sessionManager.userId.toString().isNotEmpty())) {
            Utils.showProgressDialogBanner(this.requireContext(), true)
            mPresenter.getLoyalty(sessionManager.userId.toString().toInt())
        }

        /*on item click listener*/
        mView.home_recyclerview.addOnItemTouchListener(
            RecyclerItemClickListener(requireActivity(), mView.home_recyclerview,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {

                        /*  if (position == 4) {]
                              if (sessionManager.userId.toString().isNotEmpty()) {
                                  include.visibility = View.GONE
                                  navigation.selectedItemId = R.id.navigation_scan
                              } else {
                                  val loginIntent =
                                      Intent(requireActivity(), LoginActivity::class.java)
                                  requireActivity().startActivityForResult(loginIntent, 1)
                                  // loginAlertBox(getString(R.string.alert_easy_journey))
                              }

                          }*/

                        if (position == 0) {
                            //loyalty balance....(rewards...)

                            /* if (sessionManager.userId.toString().isNotEmpty()) {

                                 // pending...
                                 val fragment = LoyaltyFragment()
                                 val args = Bundle()
                                 args.putString("from", "home")
                                 fragment.setArguments(args)
                                 addFragment(fragment)
                             } else {
                                 val loginIntent =
                                     Intent(requireActivity(), LoginActivity::class.java)
                                 requireActivity().startActivityForResult(loginIntent, 1)
                                 //loginAlertBox(getString(R.string.alert_shoplist))
                             }
 */
                            navigation.selectedItemId = R.id.navigation_loylitybalance

                        } else if (position == 1) {
//                            changed by Jaideep on 12-jan-2022.
//                            startActivity(Intent(requireActivity(), PromotionsActivity::class.java))
//                            openURL("" + sessionManager.shopOnline)

                            val fragment = WebviewFragment()
                            //sessionManager.shopOnline
                            val args = Bundle()
                            args.putString("weblink", sessionManager.shopOnline)
                            fragment.setArguments(args)
                            addFragment(fragment)
                            navigation.selectedItemId =
                                R.id.navigation_shop //to highlight bottom bar

                        } else if (position == 2) {
                            /*  val intent = Intent(requireActivity(), StoreLocation::class.java)
                              intent.putExtra("Show", "true")
                              startActivity(intent)*/
                            /*  val fragment = StoreLocationFragment()
                              addFragment(fragment)*/
                            navigation.selectedItemId = R.id.navigation_visit

                        } else if (position == 3) {
                            //promotions...
                            /*  (activity as HomeActivity).comingFrom="promotions"

                              navigation.selectedItemId = R.id.navigation_shlist*/
                            var intent = Intent(requireActivity(), PromotionsActivity::class.java)
//                            intent.putExtra("PLink", promotionLink)
                            startActivity(intent)
//                            startActivity(Intent(requireActivity(), PromotionsActivity::class.java))

                        } else if (position == 4) {

                            //scanandpay... 19th October...

                            if (sessionManager.userId.toString().isNotEmpty()) {

                                getLocationPermission()
//                              checkLocationPermission()

                            } else {
                                val loginIntent =
                                    Intent(requireActivity(), LoginActivity::class.java)
                                requireActivity().startActivityForResult(loginIntent, 1)
                                //loginAlertBox(getString(R.string.alert_shoplist))
                            }

                        } else if (position == 5) {

                            //don't highlight any of the options from bottom bar as(It doesn't make sense either)

                            /* val fragment = WebviewFragment()
                             //sessionManager.shopOnline
                             val args = Bundle()
                             args.putString("weblink", ApiManager.MRESTAURANTWEBLINK)
                             fragment.setArguments(args)
                             addFragment(fragment)*/
                            /*   (activity as HomeActivity).comingFrom="mrestaurant"

                               navigation.selectedItemId = R.id.navigation_shlist*/

                            val intent =
                                Intent(requireActivity(), CommonWebviewActivity::class.java)
                            intent.putExtra("weblink", ApiManager.MRESTAURANTWEBLINK)
                            startActivity(intent)

//                          openURL("" + ApiManager.MRESTAURANTWEBLINK)
                        }
                        /* else if (position == 0) {
                             *//*if (sessionManager.userId.toString().isNotEmpty()) {*//*
                            txt_title.text = getString(R.string.myshopping)
                            txt_title.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.black
                                )
                            )
                            txt_location.visibility = View.INVISIBLE
                            txt_lan.visibility = View.INVISIBLE
                            img_three_dots.visibility = View.GONE
                            navigation.selectedItemId = R.id.navigation_shlist
                            *//*} else {
                                val loginIntent = Intent(requireActivity(), LoginActivity::class.java)
                                requireActivity().startActivityForResult(loginIntent, 1)
                                //loginAlertBox(getString(R.string.alert_shoplist))
                            }*//*

                        }*/ /*else if (position == 3) {
                            startActivity(Intent(requireActivity(), HowItWorksActivity::class.java))
                        }*/

                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                }
            ))

        if (sessionManager.userId.toString().isNotEmpty()) {
            var gender = ""
            if (sessionManager.gender.toString() == "Mr") {
                gender = context.resources.getString(R.string.mr)
            } else if (sessionManager.gender.toString() == "Ms") {
                gender = context.resources.getString(R.string.ms)

            } else if (sessionManager.gender.toString() == "Mrs") {
                gender = context.resources.getString(R.string.mrs)

            }
            mView.txt_name.text =
                getString(R.string.hello) + " " + gender + " " + sessionManager.name + "!"


        } else
            mView.txt_name.text = getString(R.string.hello1)

        if (sessionManager.userId != null && !sessionManager.userId.equals("")) {
            mPresenter.DeviceIDUpdated(
                sessionManager.userId.toString(),
                Utils.getDeviceId(context).toString()
            )
        }
//      mPresenter.getStoreDetails((context as HomeActivity).currentLatitude.toString() + "," + (context as HomeActivity).currentLongitude)
        mView.con_card.setOnClickListener {
            navigation = requireActivity().findViewById(R.id.navigation) as BottomNavigationView
            if (sessionManager.userId.toString().isNotEmpty()) {
                include.visibility = View.GONE
                txt_title.text = getString(R.string.loyalbalance)
                txt_title.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                txt_location.visibility = View.INVISIBLE
                txt_lan.visibility = View.GONE
                img_three_dots.visibility = View.GONE
                navigation.selectedItemId = R.id.navigation_loylitybalance
            } else {
                val loginIntent = Intent(requireActivity(), LoginActivity::class.java)
                requireActivity().startActivityForResult(loginIntent, 1)
                // loginAlertBox(getString(R.string.alert_easy_journey))
            }
        }

        mView.btn_full_screen.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var intent = Intent(requireContext(), FullvideoActivity::class.java)
                intent.putExtra("PATH", "" + sessionManager.video)
                intent.putExtra("videoVolume", videoVolume)
                startActivity(intent)
            }
        })

        mView.btn_mute.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (isMute()) {
                    audio.setStreamVolume(AudioManager.STREAM_MUSIC, videoVolume, 0)
                    mView.btn_mute.setImageResource(R.drawable.ic_volume)
                } else {
                    audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
                    mView.btn_mute.setImageResource(R.drawable.ic_volume_off)
                }
            }
        })

        if (sessionManager.userId.toString().isNotEmpty()) {

            // if user is logged in first check either the full details are filled or not...

            mPresenter.getAccountDetails(sessionManager.userId.toString().toInt())

        }
    }

    private fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Log.d("24Jan>>>>", "!= PackageManager.PERMISSION_GRANTED")
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(requireActivity())
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()

                Log.d("24Jan>>>>", "No explanation needed,D")

            }
        } else {
            Log.d("24Jan>>>>", "getLatLang")

            getLatLang()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLatLang() {
        /*get lat and long using last known location*/
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val lastKnownLocationGPS =
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (currentLatitude != 0.0 && currentLongitude != 0.0) {
//            currentLatitude = lastKnownLocationGPS.latitude
//            currentLongitude = lastKnownLocationGPS.longitude
            Log.d("24Jan>>>>", "currentLatitude != 0.0")
            val bundle = Bundle()
            bundle.putDouble("latitude", currentLatitude)
            bundle.putDouble("longitude", currentLongitude)
//          Toast.makeText(this@HomeActivity, "home \n "+currentLatitude.toString()+" ========= "+currentLongitude.toString(), Toast.LENGTH_SHORT).show()
//          bundle.putDouble("latitude", 25.3239007)
//          bundle.putDouble("longitude", 51.5338199)

            val fragment = ScanProductsFragment().newInstance()
            fragment.arguments = bundle
            addFragment(fragment)


        } else {
            val loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            if (loc != null) {
                Log.d("24Jan>>>>", "loc != null")

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

                Log.d("10Oct", "Initialised else if")

                Utils.showProgressDialog(requireActivity(), true)
                //Initialize the Handler
                mDelayHandler = Handler()
                //Navigate with delay
                mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)

            } else {

                Log.d("24Jan>>>>", "else ifffff")

                GpsUtils(requireActivity()).turnGPSOn { isGPSEnable ->
                    // turn on GPS
                    isGPS = isGPSEnable

                    Log.d("24Jan>>>>", "isGPS")

                }
            }
        }
    }

    val mRunnable: Runnable = Runnable {
        if (!requireActivity().isFinishing) {
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


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {

                Log.d("24Jan>>>", "LOCATION_PERMISSION_REQUEST_CODE")
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocationPermission()
                }
                return
            }
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("24Jan>>>>", "MY_PERMISSIONS_REQUEST_LOCATION")

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.d("24Jan>>>>", "GRANTEDDDDDD")


                        // Now check background location
//                        checkBackgroundLocation()

                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )

                    }

                } else {
                    Log.d("24Jan>>>", "ELSEEEE")

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(requireActivity(), "permission denied", Toast.LENGTH_LONG).show()

                    // Check if we are in a state where the user has denied the permission and
                    // selected Don't ask again
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", requireActivity().packageName, null)
                            )
                        )
                    }
                }
                return
            }

        }

        if (requestCode == 22) {
            if (grantResults.size > 0) if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission granted, perform required code


            } else {
                // not granted
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
            ?.add(R.id.content, fragment, fragment.javaClass.simpleName)
            ?.commit()
    }

    @SuppressLint("NewApi")
    private fun setVideoPlayer(str: String) {
        mView.pb.visibility = View.VISIBLE
        mView.lay_zoom.visibility = View.GONE
        val path = str
        mView.videoView?.setVideoPath(path)
        mView.videoView?.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
            override fun onPrepared(p0: MediaPlayer?) {
                mView.pb.visibility = View.GONE
                mView.lay_zoom.visibility = View.VISIBLE
                mView.btn_mute.setImageResource(R.drawable.ic_volume)
                player = p0
                controller = MyMediaController(requireContext(), false)
                controller?.setMediaPlayer(this@HomeFragment)
                controller?.setAnchorView(mView.view_controller)
                player?.isLooping = true
                mView.videoView?.start()
                isFistTime = false

            }

        })

/*
        mView.view_controller.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                controller?.show()
                return false
            }

        })
*/

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun populateList() {
        //crating an arraylist to store users using the data class user
        val homeModel = ArrayList<HomeModel>()
        //adding some dummy data to the list
        if (sessionManager.userId.toString().isNotEmpty()) {
            homeModel.add(
                HomeModel(
                    getString(R.string.myrewards),
                    context.getDrawable(R.drawable.appicon),
                    ContextCompat.getColor(requireActivity(), R.color.hm_color),
                    false
                )
            )
            homeModel.add(
                HomeModel(
                    getString(R.string.measy),
                    context.getDrawable(R.drawable.shoponline),

                    ContextCompat.getColor(requireActivity(), R.color.hm_color), false
                )
            )
            homeModel.add(
                HomeModel(
                    getString(R.string.storelocation),
                    context.getDrawable(R.drawable.visitstore),

                    ContextCompat.getColor(requireActivity(), R.color.hm_color), false
                )
            )

            homeModel.add(
                HomeModel(
                    getString(R.string.promotions),
                    context.getDrawable(R.drawable.homepromo),

                    ContextCompat.getColor(requireActivity(), R.color.hm_color), false
                )
            )

            homeModel.add(
                HomeModel(
                    getString(R.string.scanpay),
                    context.getDrawable(R.drawable.scanpay),

                    ContextCompat.getColor(requireActivity(), R.color.hm_color), false
                )
            )
            if (sessionManager.languageselect == "en") {
                homeModel.add(
                    HomeModel(
                        getString(R.string.mrestaurants),
                        context.getDrawable(R.drawable.mrestaurant),

                        ContextCompat.getColor(requireActivity(), R.color.hm_color), false
                    )
                )

            } else {
                homeModel.add(
                    HomeModel(
                        getString(R.string.mrestaurants) + "M",
                        context.getDrawable(R.drawable.mrestaurant),

                        ContextCompat.getColor(requireActivity(), R.color.hm_color), false
                    )
                )

            }

            /*   homeModel.add(
                   HomeModel(
                       getString(R.string.myshopping),
                       ContextCompat.getColor(requireActivity(), R.color.hm_color), false
                   )
               )
               homeModel.add(
                   HomeModel(
                       getString(R.string.itworks),
                       ContextCompat.getColor(requireActivity(), R.color.hm_color), false
                   )
               )
               homeModel.add(
                   HomeModel(
                       getString(R.string.mymonoprix),
                       ContextCompat.getColor(requireActivity(), R.color.hm_color), false
                   )
               )*/
        } else {

            homeModel.add(
                HomeModel(
                    getString(R.string.myrewards),
                    context.getDrawable(R.drawable.appicon),
                    ContextCompat.getColor(requireActivity(), R.color.hm_color),
                    false
                )
            )
            homeModel.add(
                HomeModel(
                    getString(R.string.measy),
                    context.getDrawable(R.drawable.shoponline),

                    ContextCompat.getColor(requireActivity(), R.color.hm_color), false
                )
            )
            homeModel.add(
                HomeModel(
                    getString(R.string.storelocation),
                    context.getDrawable(R.drawable.visitstore),

                    ContextCompat.getColor(requireActivity(), R.color.hm_color), false
                )
            )

            homeModel.add(
                HomeModel(
                    getString(R.string.promotions),
                    context.getDrawable(R.drawable.homepromo),
                    ContextCompat.getColor(requireActivity(), R.color.hm_color), false
                )
            )

            homeModel.add(
                HomeModel(
                    getString(R.string.scanpay),
                    context.getDrawable(R.drawable.scanpay),

                    ContextCompat.getColor(requireActivity(), R.color.hm_color), false
                )
            )

            if (sessionManager.languageselect == "en") {
                homeModel.add(
                    HomeModel(
                        getString(R.string.mrestaurants),
                        context.getDrawable(R.drawable.mrestaurant),

                        ContextCompat.getColor(requireActivity(), R.color.hm_color), false
                    )
                )

            } else {
                homeModel.add(
                    HomeModel(
                        getString(R.string.mrestaurants) + "M",
                        context.getDrawable(R.drawable.mrestaurant),

                        ContextCompat.getColor(requireActivity(), R.color.hm_color), false
                    )
                )

            }

            /*  homeModel.add(
                  HomeModel(
                      getString(R.string.myshopping),
                      ContextCompat.getColor(requireActivity(), R.color.color6), true
                  )
              )
              homeModel.add(
                  HomeModel(
                      getString(R.string.measy),
                      ContextCompat.getColor(requireActivity(), R.color.hm_color), false
                  )
              )
              homeModel.add(
                  HomeModel(
                      getString(R.string.location),
                      ContextCompat.getColor(requireActivity(), R.color.hm_color), false
                  )
              )
              homeModel.add(
                  HomeModel(
                      getString(R.string.itworks),
                      ContextCompat.getColor(requireActivity(), R.color.hm_color), false
                  )
              )
              homeModel.add(
                  HomeModel(
                      getString(R.string.mymonoprix),
                      ContextCompat.getColor(requireActivity(), R.color.color6), true
                  )
              )*/
        }
        //creating our adapter
        mAdapter = HomeAdapter(homeModel, requireActivity())
        //now adding the adapter to recyclerview
        mView.home_recyclerview.adapter = mAdapter
        if (sessionManager.userId.toString().isEmpty()) {
            mView.txt_st_card.setTextColor(ContextCompat.getColor(context, R.color.color6))
            mView.txt_loyalty.setTextColor(ContextCompat.getColor(context, R.color.color6))
            mView.txt_st_cag.setTextColor(ContextCompat.getColor(context, R.color.color6))
            mView.txt_points.setTextColor(ContextCompat.getColor(context, R.color.color6))
            mView.img_lock.visibility = View.VISIBLE
        } else {
            mView.txt_st_card.setTextColor(ContextCompat.getColor(context, R.color.red))
            mView.txt_loyalty.setTextColor(ContextCompat.getColor(context, R.color.red))
            mView.txt_st_cag.setTextColor(ContextCompat.getColor(context, R.color.red))
            mView.txt_points.setTextColor(ContextCompat.getColor(context, R.color.red))
            mView.img_lock.visibility = View.GONE
        }
    }

    private fun getSliderBanners() {
        if (mView.banner != null) {
            mView.banner.removeAllSliders()
        }
        try {

            for (i in mBannerList.indices) {
                textSliderView = DefaultSliderView(requireActivity())
                textSliderView
                    .image(mBannerList[i].image)
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener {
                        if (mBannerList[i].link != null && !mBannerList[i].link.equals("") && mBannerList[i].link.contains(
                                ".pdf"
                            )
                        ) {
//                            Toast.makeText(requireContext(),"PDF : "+mBannerList[i].link,Toast.LENGTH_SHORT).show()
                            download("" + mBannerList[i].link, folder)
                        } else {
                            var intent = Intent(requireActivity(), ImageActivity::class.java)
                            intent.putParcelableArrayListExtra("LIST", ArrayList(mBannerList))
                            intent.putExtra("POSITION", i)
                            startActivity(intent)
//                        Toast.makeText(requireContext(),"Image : "+mBannerList[i].image,Toast.LENGTH_SHORT).show()
                        }
                    }
                mView.banner.addSlider(textSliderView)
            }

        } catch (e: Exception) {
            Log.e("", e.message.toString())
            e.printStackTrace()
        }
        // mView.banner.moveNextPosition(true)
        mView.banner.movePrevPosition(false)
        mView.banner.setPresetTransformer(SliderLayout.Transformer.Accordion)
        mView.banner.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        mView.banner.setCustomAnimation(DescriptionAnimation())
        mView.banner.pagerIndicator.setVisibility(View.VISIBLE)
        mView.banner.pagerIndicator
            .setDefaultIndicatorColor(
                ContextCompat.getColor(requireActivity(), R.color.color2),
                ContextCompat.getColor(requireActivity(), R.color.gray)
            )
        mView.banner.setDuration(3000)
        mView.banner.addOnPageChangeListener(this)

        if (mBannerList.size == 1) {
            mView.banner.stopAutoCycle()
        } else {
            mView.banner.startAutoCycle()
        }

    }

    /*For Slider*/
    override fun onSliderClick(slider: BaseSliderView?) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }

    override fun showBanner(response: StatusBannerModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {

            mBannerList = response.data as MutableList<BannerModel>
            for (i in 0 until mBannerList.size) {
                if (mBannerList.get(i).link.contains(".pdf")) {
                    promotionLink = mBannerList.get(i).link
                    sessionManager.PROMOTION_LINK = promotionLink
                    break
                }
            }

            getSliderBanners()
            /*promotion api call*/
            /*Utils.showProgressDialogBanner(requireContext(), true)
            */

        } else if (response.status == 402) {
            Utils.tokenExpire(requireActivity())
            Utils.showProgressDialog(requireActivity(), true)

//            mPresenter.loadBanner()
        } else {
            showMessage(response.statusMessage)
        }
    }

    private fun checkAndOpenNotificationSettings(channelId: String) {
        if (!areNotificationsEnabled()) {
            openNotificationSettingsForApp(channelId)
        }
    }

    private fun areNotificationsEnabled(): Boolean {
        val context: Context = requireContext()
        val notificationManager = NotificationManagerCompat.from(context)
        return notificationManager.areNotificationsEnabled()
    }

    private fun openNotificationSettingsForApp(channelId: String?) {
        // Links to this app's notification settings.
        val intent = Intent()
        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && channelId != null) {
            intent.action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
            intent.putExtra("android.provider.extra.APP_PACKAGE", requireActivity().packageName)
        }
        intent.putExtra("app_package", requireActivity().packageName)
        intent.putExtra("app_uid", requireActivity().applicationInfo.uid)
        startActivity(intent)
    }

    //    Added by Jaideep on 12-jan-2022.
    override fun showPromotionOnline(response: PromotionOnlineModel) {
        /*Utils.pd.dismiss()*/
        if (response.status == 200) {
            if (response.data != null && !response.data.isEmpty()) {
                for (data in response.data) {
                    if (data.promotionName.equals("ShopOnline")) {
                        sessionManager.shopOnline = "" + data.url
                    } else if (data.promotionName.equals("Video")) {
                        sessionManager.video = "" + data.url

                        setVideoPlayer("" + sessionManager.video)
                    }
                }
            }
        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }


    }

    override fun showLoyalty(response: LoylityModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {
            if (response.data != null) {
                mView.txt_points.text =
                    resources.getString(R.string.Qatarcurrency) + " " + response.data?.points
                sessionManager.loyaltyAmount =
                    Utils.numbersWithoutLocalization(response.data?.points).toString()

                Log.d("20Jan>>>", response.data?.points.toString())
                sessionManager.loyaltyId = response.data?.csmroleID.toString()
                sessionManager.cardno = response.data?.barcode
                sessionManager.userName = response.data?.userName
                sessionManager.barcodePath = response.data?.barcodePath
            }
        } else if (response.status == 402) {
            Utils.tokenExpire(requireActivity())
            Utils.showProgressDialog(requireActivity(), true)
            Log.d("10Oct", "402")

            mPresenter.getLoyalty(sessionManager.userId.toString().toInt())
        } else {
            showMessage(response.statusMessage)
        }
    }

    override fun setBenefit(response: BenefitModel) {


    }

    override fun setTerms(response: TermsAndConditionsModel) {

    }

    override fun showStoreLocation(response: StatusModel?) {
        if (response!!.status == 200) {
            Log.e("stor_response", "data= " + response.data);
            sessionManager.storeLocation = response.data.address
            sessionManager.storelocationarabic = response.data.address_Arabic
            if (sessionManager.languageselect == "en") {
                (context as HomeActivity).txtlocation?.text = sessionManager.storeLocation
            } else {
                (context as HomeActivity).txtlocation?.text = sessionManager.storelocationarabic
            }
        }
    }

    override fun showDeviceIDUpdated(response: StatusDiviceIDUpdateModel) {
        if (response.status == 404) {
            if (response.data.isActive && !response.data.consumerid.equals("") && !response.data.consumerid.equals(
                    "0"
                ) && !response.data.consumerid.equals("null")
            ) {
                sessionManager.userId = ""
                sessionManager.name = ""
                sessionManager.familyName = ""
                sessionManager.mobileNumber = ""
                sessionManager.loyaltyId = ""
                sessionManager.loyaltyAmount = ""
                sessionManager.cardno = ""
                sessionManager.storeLocation = ""
                sessionManager.instaToken = ""
                db = AppDatabase(context)
                db.todoDao().deleteAll()
                if (Utils.gLogin) {
                    Utils.gLogin = false
                }
                if (Utils.fLogin) {

                    LoginManager.getInstance().logOut()
                    Utils.fLogin = false
                }
                val i = Intent(requireActivity(), HomeActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)

            }
        }
    }

    override fun showReloadButton(
        throwable: Throwable,
        errorBody: ErrorBody?,
        network: Boolean
    ) {
        Utils.pd.dismiss()
        if (Utils.pd.isShowing) {
            Utils.pd.dismiss()
        }

        when {
            network -> showDilaogBox(
                getString(R.string.no_internet),
                getString(R.string.generic_no_internet_desc)
            )
            errorBody != null -> showMessage(errorBody.toString())
            throwable is SocketException -> showMessage(throwable.message!!)
//            else -> showMessage(throwable.message!!)
        }
    }

    override fun showAccountDetails(response: StatusModel) {
        when {
            response.status == 200 -> {
                //check all the properties... if any of the property is empty let it take to account screen to update the details with toast message
                //take a bool value...
                if (response.data.gender.isNullOrEmpty()) {
                    sendUserToAccountScreen()
                    Utils.pd.dismiss()

                } else if (response.data.userName.isNullOrEmpty()) {
                    sendUserToAccountScreen()
                    Utils.pd.dismiss()

                } else if (response.data.familyName.isNullOrEmpty()) {
                    sendUserToAccountScreen()
                    Utils.pd.dismiss()

                } else if (response.data.userEmail.isNullOrEmpty()) {
                    sendUserToAccountScreen()
                    Utils.pd.dismiss()

                } else if (response.data.mobileNumber.isNullOrEmpty()) {
                    sendUserToAccountScreen()
                    Utils.pd.dismiss()

                }/* else if (response.data.dob.isNullOrEmpty()) {
                    sendUserToAccountScreen()
                    Utils.pd.dismiss()

                }*/ else if (response.data.area.isNullOrEmpty()) {
                    sendUserToAccountScreen()
                    Utils.pd.dismiss()

                } else
                    Utils.pd.dismiss()

            }
            response.status == 402 -> {
                Utils.tokenExpire(requireContext())
                /*Account details*/

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

    private fun sendUserToAccountScreen() {

        val i = Intent(requireActivity(), MyAccountActivity::class.java)
        i.putExtra("comingfrom", "home")
        startActivity(i)
    }

    override fun onResume() {
        super.onResume()

        val myApp = requireActivity().getApplication() as MyApplication

        if (sessionManager.userId != null && !sessionManager.userId.equals("")) {
            mPresenter.DeviceIDUpdated(
                sessionManager.userId.toString(),
                Utils.getDeviceId(context).toString()
            )
        }
//        mPresenter.getStoreDetails((contshowReloadButtonext as HomeActivity).currentLatitude.toString() + "," + (context as HomeActivity).currentLongitude)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // check if the request code is same as what is passed  here it is 200 after delete
        if (requestCode == 1) {
            populateList()
        }

    }

    override fun start() {
        if (player != null) {
            return player?.start()!!;
        }
    }

    override fun pause() {
        if (player != null) {
            return player?.pause()!!;
        }
    }

    override fun getDuration(): Int {
        if (player != null) {
            return 0;
        } else {
            return 0
        }
    }

    override fun getCurrentPosition(): Int {
        if (player != null) {
            return 0;
        } else {
            return 0
        }
    }

    override fun seekTo(pos: Int) {
        if (player != null) {
            return player?.seekTo(pos)!!;
        }
    }

    override fun isPlaying(): Boolean {
        if (player != null) {
            return false;
        } else {
            return false
        }
    }

    override fun getBufferPercentage(): Int {
        return 0;
    }

    override fun canPause(): Boolean {
        return true
    }

    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun canSeekForward(): Boolean {
        return true
    }

    override fun isFullScreen(): Boolean {
        return true
    }

    override fun isMute(): Boolean {
        val musicVolume: Int = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
        return (musicVolume == 0)
    }

    override fun toggleFullScreen() {

        var intent = Intent(requireContext(), FullvideoActivity::class.java)
        intent.putExtra("PATH", "" + sessionManager.video)
        intent.putExtra("videoVolume", videoVolume)
        startActivity(intent)
    }

    override fun toggleMute() {

    }

    fun download(file: String, folder_: String) {
        val directory = File(folder_)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val dir = File(folder_)
        var children = dir.list()
        /*for(item in children) {
            File(dir, item).delete();
        }*/
        if (EasyPermissions.hasPermissions(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) && EasyPermissions.hasPermissions(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            DownloadFile().execute(file)
        } else {
            //If permission is not present request for the same.
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your file storage so that it can write files.",
                WRITE_REQUEST_CODE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    private inner class DownloadFile : AsyncTask<String, String, String>() {

        private var progressDialog: ProgressDialog? = null
        private var fileName: String? = null

        override fun onPreExecute() {
            super.onPreExecute()
            this.progressDialog = ProgressDialog(requireActivity())
            this.progressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            this.progressDialog!!.setCancelable(false)
            this.progressDialog!!.show()
        }

        override fun doInBackground(vararg f_url: String): String {
            var count: Int
            try {
                val url = URL(f_url[0])
                val connection = url.openConnection()
                connection.connect()
                val lengthOfFile = connection.contentLength
                val input = BufferedInputStream(url.openStream(), 8192)
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1)
                val directory = File(folder)
                if (!directory.exists()) {
                    directory.mkdirs()
                }
                val output = FileOutputStream(folder + fileName!!)
                val data = ByteArray(1024)
                var total: Long = 0
                while (input.read(data).also { count = it } != -1) {
                    total += count.toLong()
                    publishProgress("" + (total * 100 / lengthOfFile).toInt())
                    Log.d("Download Activity", "Progress: " + (total * 100 / lengthOfFile).toInt())
                    output.write(data, 0, count)
                }
                output.flush()
                output.close()
                input.close()
                return resources.getString(R.string.downloaded_at) + " " + "$folder$fileName"
            } catch (e: Exception) {
                Log.e("Error: ", e.message!!)
            }

            return resources.getString(R.string.something_went_wrong)
        }

        override fun onProgressUpdate(vararg progress: String) {
            // setting progress percentage
            progressDialog!!.progress = Integer.parseInt(progress[0])
        }


        override fun onPostExecute(message: String) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog!!.dismiss()

            // Display File path after downloading
            Toast.makeText(
                requireContext(),
                message, Toast.LENGTH_LONG
            ).show()

            if (message.contains(resources.getString(R.string.downloaded_at))) {
                MediaScannerConnection.scanFile(
                    requireContext(),
                    arrayOf(folder + fileName!!), null
                ) { path, uri ->
                    Utils.openFile(requireContext(), File(path))
                }
            }
        }
    }


}