package com.production.monoprix.ui.navigation


import android.content.DialogInterface
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.model.*
import com.production.monoprix.mvp.BaseMvpFragment
import com.production.monoprix.room.AppDatabase
import com.production.monoprix.ui.account.MyAccountActivity
import com.production.monoprix.ui.address_list.AddressListActivity
import com.production.monoprix.ui.contactus.ContactUsActivity
import com.production.monoprix.ui.home.HomeActivity
import com.production.monoprix.ui.howitworks.HowItWorksActivity
import com.production.monoprix.ui.login.LoginActivity
import com.production.monoprix.ui.login.LoginContractor
import com.production.monoprix.ui.login.LoginPresenter
import com.production.monoprix.ui.myShopingList.MyShopListActivity
import com.production.monoprix.ui.myShopingList.MyShopListFragment
import com.production.monoprix.ui.orderhistory.OrderHistoryActivity
import com.production.monoprix.ui.promotions.PromotionsActivity
import com.production.monoprix.ui.signup.SignUpActivity
import com.production.monoprix.ui.webview.BenefitWebviewActivity
import com.production.monoprix.ui.webview.CommonWebviewActivity
import com.production.monoprix.ui.webview.CommonWebviewActivityFb
import com.production.monoprix.util.RecyclerItemClickListener
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.activity_navigation.view.*
import kotlinx.android.synthetic.main.login_alert_dialogue.view.*


class DrawerFragment : BaseMvpFragment<LoginContractor.View, LoginContractor.Presenter>(),
    View.OnClickListener,
    LoginContractor.View {

    lateinit var views: View
    lateinit var sessionManager: SessionManager
    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var drawerAdapter: DrawerAdapter? = null
    lateinit var containerView: View
    private var recyclerView: RecyclerView? = null
    lateinit var names: Array<String>
    private val images = intArrayOf(
        R.drawable.ic_account,
        R.drawable.ic_list_icon,
        R.drawable.ic_round_location,
        R.drawable.ic_round_offer,
        R.drawable.ic_round_credit_card_black,
        R.drawable.ic_round_store,
        R.drawable.ic_order_history
    )

    var ivfacebook: ImageView? = null
    var ivinstagram: ImageView? = null
    var ivtwitter: ImageView? = null
    var ivyoutube: ImageView? = null

    lateinit var loginAlertDialog: AlertDialog
    lateinit var mGoogleApiClient: GoogleApiClient
    lateinit var versionName: String
    lateinit var db: AppDatabase
    override var mPresenter: LoginContractor.Presenter = LoginPresenter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        views = inflater.inflate(R.layout.activity_navigation, container, false)
        versionName = context?.packageManager
            ?.getPackageInfo(context?.packageName!!, 0)!!.versionName
        db = AppDatabase(requireActivity())

        ivfacebook = views.findViewById(R.id.ivfacebook)
        ivinstagram = views.findViewById(R.id.ivinstagram)
        ivtwitter = views.findViewById(R.id.ivtwitter)
        ivyoutube = views.findViewById(R.id.ivyoutube)

        ivfacebook?.setOnClickListener(View.OnClickListener {

            mDrawerLayout?.closeDrawers()

            val intent =
                Intent(requireActivity(), CommonWebviewActivityFb::class.java)
            intent.putExtra("weblink", ApiManager.FACEBOOKWEBLINK)
            startActivity(intent)

        })
        ivinstagram?.setOnClickListener(View.OnClickListener {

            mDrawerLayout?.closeDrawers()

            val intent =
                Intent(requireActivity(), CommonWebviewActivity::class.java)
            intent.putExtra("weblink", ApiManager.INSTAGRAMWEBLINK)
            startActivity(intent)

        })


        ivtwitter?.setOnClickListener(View.OnClickListener {

            mDrawerLayout?.closeDrawers()

            val intent =
                Intent(requireActivity(), CommonWebviewActivity::class.java)
            intent.putExtra("weblink", ApiManager.TWITTERWEBLINK)
            startActivity(intent)

        })

        ivyoutube?.setOnClickListener(View.OnClickListener {

            mDrawerLayout?.closeDrawers()

            val intent =
                Intent(requireActivity(), CommonWebviewActivity::class.java)
            intent.putExtra("weblink", ApiManager.YOUTUBEWEBLINK)
            startActivity(intent)

        })

        /*user is loggedin or not*/
        sessionManager = SessionManager(requireActivity())
        if (sessionManager.userId.toString().isNotEmpty()) {
            if (sessionManager.languageselect == "en") {
                names = arrayOf(
                    getString(R.string.myaccount),
                    getString(R.string.mloyaltyprogram), //new
                    getString(R.string.myshopping),
                    getString(R.string.shoppinghistory), // 14nov...
                    getString(R.string.mydeli),
                    getString(R.string.storelocation),//new
                    getString(R.string.promotions), //new
                    getString(R.string.mrestaurants),
                    getString(R.string.privacypolicy),  //new
                    getString(R.string.termsandconditions),
                    getString(R.string.faq),  //new
                    getString(R.string.contactus),
                    getString(R.string.deleteaccount),
                    getString(R.string.logout)
                )

            } else {
                names = arrayOf(
                    getString(R.string.myaccount),
                    getString(R.string.mloyaltyprogram), //new
                    getString(R.string.myshopping),
                    getString(R.string.shoppinghistory), // 14nov...
                    getString(R.string.mydeli),
                    getString(R.string.storelocation),//new
                    getString(R.string.promotions), //new
                    getString(R.string.mrestaurants) + "M",
                    getString(R.string.privacypolicy),  //new
                    getString(R.string.termsandconditions),
                    getString(R.string.faq),  //new
                    getString(R.string.contactus),
                    getString(R.string.deleteaccount),
                    getString(R.string.logout)
                )

            }

        } else {
            //hide logout...
            if (sessionManager.languageselect == "en") {
                names = arrayOf(
                    getString(R.string.myaccount),
                    getString(R.string.mloyaltyprogram), //new
                    getString(R.string.myshopping),
                    getString(R.string.shoppinghistory),
                    getString(R.string.mydeli),
                    getString(R.string.storelocation),//new
                    getString(R.string.promotions), //new
                    getString(R.string.mrestaurants),
                    getString(R.string.privacypolicy),
                    getString(R.string.termsandconditions),
                    getString(R.string.faq),  //new
                    getString(R.string.contactus)

                )


            } else {
                names = arrayOf(
                    getString(R.string.myaccount),
                    getString(R.string.mloyaltyprogram), //new
                    getString(R.string.myshopping),
                    getString(R.string.shoppinghistory),
                    getString(R.string.mydeli),
                    getString(R.string.storelocation),//new
                    getString(R.string.promotions), //new
                    getString(R.string.mrestaurants) + "M",
                    getString(R.string.privacypolicy),
                    getString(R.string.termsandconditions),
                    getString(R.string.faq),  //new
                    getString(R.string.contactus)

                )

            }
        }

        /*views.txt_signup.paintFlags = views.txt_signup.paintFlags or Paint.UNDERLINE_TEXT_FLAG*/
        views.txt_signin.paintFlags = views.txt_signin.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        val content = SpannableString(getString(R.string.registration))
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        views.txt_signup.setText(content)

        if (sessionManager.userId.toString().isNotEmpty()) {
//          views.txt_name.text = getString(R.string.hello) + "\n" + sessionManager.name + "!"

            var gender = ""
            if (sessionManager.gender.toString() == "Mr") {
                gender = context.resources.getString(R.string.mr)
            } else if (sessionManager.gender.toString() == "Ms") {
                gender = context.resources.getString(R.string.ms)

            } else if (sessionManager.gender.toString() == "Mrs") {
                gender = context.resources.getString(R.string.mrs)
            }
            views.txt_name.text =
                getString(R.string.hello) + "\n" + gender + " " + sessionManager.name + "!"

            views.txt_name.visibility = View.VISIBLE
            views.txt_signup.visibility = View.GONE
            views.txt_signin.visibility = View.GONE
//            views.txt_logout.visibility = View.VISIBLE
//            views.txt_logout.text = getString(R.string.logout)
            if (sessionManager.server == "QA") {
                views.txt_version.text =
                    getString(R.string.version) + " " + versionName + " " + "(QA)"
            } else {
                views.txt_version.text = getString(R.string.version) + " " + versionName
            }
            views.txt_version.visibility = View.VISIBLE
//            views.txt_logout.setOnClickListener(this)
        } else {
            views.txt_name.text = getString(R.string.hello1)
            views.txt_name.visibility = View.VISIBLE
            views.txt_signup.visibility = View.VISIBLE
            views.txt_signin.visibility = View.VISIBLE
            views.txt_logout.visibility = View.GONE
            // views.txt_logout.text = getString(R.string.version) + " " + versionName
            if (sessionManager.server == "QA") {
                views.txt_version.text =
                    getString(R.string.version) + " " + versionName + " " + "(QA)"
            } else {
                views.txt_version.text = getString(R.string.version) + " " + versionName
            }
            views.txt_version.visibility = View.VISIBLE
//            views.txt_logout.setOnClickListener(null)
        }

        views.txt_signin.setOnClickListener(this)
        views.txt_signup.setOnClickListener(this)
//      views.txt_logout.setOnClickListener(this)
        views.txt_contact.setOnClickListener(this)
        views.txt_how_it_works.setOnClickListener(this)
        views.img_left_arrow.setOnClickListener(this)

        /*menu list*/
        recyclerView = views.findViewById<View>(R.id.listview) as RecyclerView
        menuList()

        //Navigation Item OnClick
        recyclerView?.addOnItemTouchListener(
            RecyclerItemClickListener(requireActivity(), recyclerView!!,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        if (position == 0) {
                            mDrawerLayout?.closeDrawers()
                            if (sessionManager.userId.toString().isNotEmpty()) {
                                val i = Intent(requireActivity(), MyAccountActivity::class.java)
                                i.putExtra("comingfrom", "drawer")
                                startActivity(i)
                            } else {
                                //loginAlertBox(getString(R.string.alert_address))
                                val loginIntent =
                                    Intent(requireActivity(), LoginActivity::class.java)
                                startActivityForResult(loginIntent, 1)
                            }
                        } else if (position == 1) {

                            mDrawerLayout?.closeDrawers()

                            // my rewards...

                            if (sessionManager.userId.toString().isNotEmpty()) {

                                // add LoyaltyFragment here...
                                /* startActivity(
                                     Intent(
                                         requireActivity(),
                                         LoyaltyActivity::class.java
                                     )
                                 )*/
                                val navigation =
                                    activity!!.findViewById(R.id.navigation) as BottomNavigationView

                                navigation.selectedItemId = R.id.navigation_loylitybalance
                            } else {
                                val loginIntent =
                                    Intent(requireActivity(), LoginActivity::class.java)
                                requireActivity().startActivityForResult(loginIntent, 1)
                                //loginAlertBox(getString(R.string.alert_shoplist))
                            }


                        } else if (position == 2) {
                            mDrawerLayout?.closeDrawers()
                            /* val fragment = MyShopListFragment()
                            addFragment(fragment)*/
                            val i = Intent(requireActivity(), MyShopListActivity::class.java)
                            startActivity(i)
                        } else if (position == 3) {
                            mDrawerLayout?.closeDrawers()
                            if (sessionManager.userId.toString().isNotEmpty()) {//shoping history
                                val i = Intent(requireActivity(), OrderHistoryActivity::class.java)
                                startActivity(i)
                            } else {
                                val loginIntent =
                                    Intent(requireActivity(), LoginActivity::class.java)
                                startActivityForResult(loginIntent, 1)
                            }
                        } else if (position == 4) {
                            mDrawerLayout?.closeDrawers()
                            if (sessionManager.userId.toString().isNotEmpty()) {
                                val i = Intent(requireActivity(), AddressListActivity::class.java)
                                startActivity(i)
                            } else {
                                //loginAlertBox(getString(R.string.alert_address))
                                val loginIntent =
                                    Intent(requireActivity(), LoginActivity::class.java)
                                startActivityForResult(loginIntent, 1)
                            }
                        } else if (position == 5) {
                            mDrawerLayout?.closeDrawers()
                            val navigation =
                                activity!!.findViewById(R.id.navigation) as BottomNavigationView

                            navigation.selectedItemId = R.id.navigation_visit

//                          startActivity(Intent(requireActivity(), StoreLocation::class.java))
                        } else if (position == 6) {

                            mDrawerLayout?.closeDrawers()

                            startActivity(Intent(requireActivity(), PromotionsActivity::class.java))

                            // promotions...
                            /*  val navigation =
                                  activity!!.findViewById(R.id.navigation) as BottomNavigationView
                              (activity as HomeActivity).comingFrom = "promotions"
                              navigation.selectedItemId = R.id.navigation_shlist*/

                        } else if (position == 7) {

                            //                          changed by Jaideep on 12-jan-2022.
//                          startActivity(Intent(requireActivity(), PromotionsActivity::class.java))
//                          openURL("" + ApiManager.MRESTAURANTWEBLINK)

                            mDrawerLayout?.closeDrawers()

                            val intent =
                                Intent(requireActivity(), CommonWebviewActivity::class.java)
                            intent.putExtra("weblink", ApiManager.MRESTAURANTWEBLINK)
                            startActivity(intent)

                            /*val navigation =
                                activity!!.findViewById(R.id.navigation) as BottomNavigationView
                            (activity as HomeActivity).comingFrom = "mrestaurant"
                            navigation.selectedItemId = R.id.navigation_shlist*/

                        } else if (position == 8) {

                            mDrawerLayout?.closeDrawers()

                            // privacy policy...
                            Utils.showProgressDialog(requireActivity(), true)
                            mPresenter.getPrivacy()

                        } else if (position == 9) {

                            // terms and conditions...
                            mDrawerLayout?.closeDrawers()
                            Utils.showProgressDialog(requireActivity(), true)
                            mPresenter.getTermAndConditions("1")

                        } else if (position == 10) {

                            mDrawerLayout?.closeDrawers()

                            // privacy policy...
                            Utils.showProgressDialog(requireActivity(), true)
                            mPresenter.getFaqApi()

                        } else if (position == 11) {

                            /*  // help...
                              mDrawerLayout?.closeDrawers()
                              Utils.showProgressDialog(requireActivity(), true)
                              mPresenter.getHelp()*/
                            mDrawerLayout?.closeDrawers()
                            //contact us....
                            startActivity(Intent(requireActivity(), ContactUsActivity::class.java))


                        } else if (position == 12) {

                            mDrawerLayout?.closeDrawers()
                            // del account

                            delaccount()

                        } else if (position == 13) {

                            mDrawerLayout?.closeDrawers()
                            //logout...
                            alert()
                        }

                        /*else if (position == 11) {
                            mDrawerLayout?.closeDrawers()
                            //how does it work...
                            startActivity(Intent(requireActivity(), HowItWorksActivity::class.java))

                        } else if (position == 12) {
                            mDrawerLayout?.closeDrawers()
                            //logout...
                            alert()

                        }*/

                        /*  else if (position == 1) {
                              //my shopping list
                              mDrawerLayout?.closeDrawers()
                              (context as HomeActivity).navigation.menu.getItem(1).isChecked=true
                          }*/
                        /* else if (position == 2) {
                             mDrawerLayout?.closeDrawers()
                             if (sessionManager.userId.toString().isNotEmpty()) {
                                 val i = Intent(requireActivity(), AddressListActivity::class.java)
                                 startActivity(i)
                             } else {
                                 //loginAlertBox(getString(R.string.alert_address))
                                 val loginIntent =
                                     Intent(requireActivity(), LoginActivity::class.java)
                                 startActivityForResult(loginIntent, 1)
                             }
                         }*/

                        /* else if (position == 4) {
                             val include = activity!!.findViewById(R.id.include) as View
                             val txt_title = activity!!.findViewById(R.id.txt_title) as TextView
                             val txt_location =
                                 activity!!.findViewById(R.id.txt_location) as TextView
                             val txt_lan = activity!!.findViewById(R.id.txt_lan) as TextView
                             val img_three_dots =
                                 activity!!.findViewById(R.id.img_three_dots) as ImageView
                             val navigation =
                                 activity!!.findViewById(R.id.navigation) as BottomNavigationView
                             if (sessionManager.userId.toString().isNotEmpty()) {
                                 mDrawerLayout?.closeDrawers()
                                 include.visibility = View.VISIBLE
                                 txt_title.text = getString(R.string.loyalbalance)
                                 txt_title.setTextColor(
                                     ContextCompat.getColor(
                                         requireActivity(),
                                         R.color.black
                                     )
                                 )
                                 txt_location.visibility = View.INVISIBLE
                                 txt_lan.visibility = View.GONE
                                 img_three_dots.visibility = View.GONE
                                 navigation.selectedItemId = R.id.navigation_loylitybalance
                             } else {
                                 mDrawerLayout?.closeDrawers()
                                 val loginIntent =
                                     Intent(requireActivity(), LoginActivity::class.java)
                                 startActivityForResult(loginIntent, 1)
                             }
                         }
 */


                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                }
            ))

        return views
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txt_signin -> {
                mDrawerLayout?.closeDrawers()
                val i = Intent(requireActivity(), LoginActivity::class.java)
                i.putExtra("from_splash", true)
                startActivity(i)
            }
            R.id.txt_signup -> {
                mDrawerLayout?.closeDrawers()
                val i = Intent(requireActivity(), SignUpActivity::class.java)
                i.putExtra("from_splash", true)
                startActivity(i)
            }
            R.id.txt_logout -> {
                alert()
            }
            R.id.txt_contact -> {
                mDrawerLayout?.closeDrawers()
                startActivity(Intent(requireActivity(), ContactUsActivity::class.java))
            }
            R.id.txt_how_it_works -> {
                mDrawerLayout?.closeDrawers()
                startActivity(Intent(requireActivity(), HowItWorksActivity::class.java))
            }
            R.id.img_left_arrow -> {
                mDrawerLayout?.closeDrawers()
            }
        }
    }

    fun delaccount() {
        val dialogBuilder = android.app.AlertDialog.Builder(requireActivity())
        // set message of alert dialog
        dialogBuilder.setMessage(getString(R.string.doyou))
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(
                getString(R.string.ok),
                DialogInterface.OnClickListener { dialog, id ->

                    //hit del account api...

                    mPresenter.delUserById(sessionManager.userId.toString())

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

    fun alert() {
        val dialogBuilder = android.app.AlertDialog.Builder(requireActivity())
        // set message of alert dialog
        dialogBuilder.setMessage(getString(R.string.er_logout))
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(
                getString(R.string.ok),
                DialogInterface.OnClickListener { dialog, id ->
                    logoutAction()

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

    private fun logoutAction() {

        sessionManager.userId = ""
        sessionManager.name = ""
        sessionManager.familyName = ""
        sessionManager.mobileNumber = ""
        sessionManager.loyaltyId = ""
        sessionManager.loyaltyAmount = ""
        sessionManager.cardno = ""
        sessionManager.storeLocation = ""
        sessionManager.instaToken = ""
        views.txt_name.text = getString(R.string.hello1)
        views.txt_name.visibility = View.VISIBLE
        views.txt_signup.visibility = View.VISIBLE
        views.txt_signin.visibility = View.VISIBLE
        views.txt_logout.visibility = View.GONE
        db.todoDao().deleteAll()
        if (Utils.gLogin) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback {
                // Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
            }

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


    fun setUpDrawer(fragmentId: Int, drawerLayout: DrawerLayout) {
        containerView = requireActivity().findViewById(fragmentId)
        mDrawerLayout = drawerLayout
        mDrawerToggle = object :
            ActionBarDrawerToggle(
                activity,
                drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
            ) {

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                Utils.iaDrawerOpen = true
                activity?.invalidateOptionsMenu()
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                activity?.invalidateOptionsMenu()
            }

        }

        mDrawerLayout!!.addDrawerListener(mDrawerToggle!!)
        mDrawerLayout!!.post { mDrawerToggle!!.syncState() }

    }

    private fun menuList() {
        drawerAdapter = DrawerAdapter(populateList(), requireActivity())
        recyclerView!!.adapter = drawerAdapter
        recyclerView!!.layoutManager = LinearLayoutManager(activity) as RecyclerView.LayoutManager?
    }

    private fun populateList(): ArrayList<DrawerModel> {
        val list = ArrayList<DrawerModel>()
        for (i in names.indices) {
            val drawerModel = DrawerModel()
            drawerModel.name = names[i]
//          drawerModel.image = images[i]
            list.add(drawerModel)
        }
        return list
    }

    private fun loginAlertBox(msg: String) {
        val li = LayoutInflater.from(requireActivity())
        val popupDialog = li.inflate(R.layout.login_alert_dialogue, null)
        val alertDialogBuilder = AlertDialog.Builder(requireActivity())
        alertDialogBuilder.setView(popupDialog)
        loginAlertDialog = alertDialogBuilder.create()
        loginAlertDialog.setCancelable(false)
        loginAlertDialog.setCanceledOnTouchOutside(false)
        loginAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        popupDialog.txt_alert.text = msg
        popupDialog.txt_sign_in.setOnClickListener {
            val loginIntent = Intent(requireActivity(), LoginActivity::class.java)
            startActivityForResult(loginIntent, 1)
            loginAlertDialog.dismiss()
        }

        popupDialog.txt_sign_in_required.setOnClickListener { loginAlertDialog.dismiss() }
        alertDialogBuilder.setCancelable(true)
        loginAlertDialog.setCancelable(true)
        loginAlertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager.userId.toString().isNotEmpty()) {

            var gender = ""
            if (sessionManager.gender.toString() == "Mr") {
                gender = context.resources.getString(R.string.mr)
            } else if (sessionManager.gender.toString() == "Ms") {
                gender = context.resources.getString(R.string.ms)

            } else if (sessionManager.gender.toString() == "Mrs") {
                gender = context.resources.getString(R.string.mrs)

            }

            views.txt_name.text =
                getString(R.string.hello) + "\n" + gender + " " + sessionManager.name + "!"


//            views.txt_name.text = getString(R.string.hello) + "\n" + sessionManager.name + "!"
            views.txt_name.visibility = View.VISIBLE
            views.txt_signup.visibility = View.GONE
            views.txt_signin.visibility = View.GONE
//            views.txt_logout.visibility = View.VISIBLE
//            views.txt_logout.text = getString(R.string.logout)
            if (sessionManager.server == "QA") {
                //views.txt_version.text = getString(R.string.version) + " " + versionName + " " + "(QA)"
                views.txt_version.text = getString(R.string.version) + " " + versionName
            } else {
                views.txt_version.text = getString(R.string.version) + " " + versionName
            }
            views.txt_version.visibility = View.VISIBLE
//            views.txt_logout.setOnClickListener(this)
        } else {
            views.txt_name.text = getString(R.string.hello1)
            views.txt_name.visibility = View.VISIBLE
            views.txt_signup.visibility = View.VISIBLE
            views.txt_signin.visibility = View.VISIBLE
//            views.txt_logout.visibility = View.GONE
            if (sessionManager.server == "QA") {
                // views.txt_version.text = getString(R.string.version) + " " + versionName + " " + "(QA)"
                views.txt_version.text = getString(R.string.version) + " " + versionName
            } else {
                views.txt_version.text = getString(R.string.version) + " " + versionName
            }
            views.txt_version.visibility = View.VISIBLE
//            views.txt_logout.setOnClickListener(null)
        }
        menuList()
    }

    override fun onStart() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleApiClient = GoogleApiClient.Builder(requireActivity())
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
        mGoogleApiClient.connect()
        super.onStart()
    }

    //    Added by Jaideep on 12-jan-2022.
    fun openURL(strUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(strUrl)
        startActivity(Intent.createChooser(intent, "Open with"))
    }

    override fun showLogin(response: StatusModel) {

    }

    override fun showDeviceIDChangeRequestEmail(response: DeviceUpdateStatusModel) {
    }

    override fun showSocialLogin(response: StatusModel) {
    }

    override fun setCMSDetails(response: TermsAndConditionsModel) {

    }

    override fun setTermsAndConditions(response: TermsAndConditionsModel) {

        Utils.pd.dismiss()
        if (response.status == 200) {
//            val i = Intent(requireActivity(), WebviewActivity::class.java)
            val i = Intent(requireActivity(), BenefitWebviewActivity::class.java)
            if (sessionManager.languageselect == "en") {
                i.putExtra("description", response.data.getLoyaltyProgram_T_C_Eng)

            } else {
                i.putExtra("description", response.data.getLoyaltyProgram_T_C_Arabic)
            }
            i.putExtra("page_name", getString(R.string.termsandconditions))
            startActivity(i)
        } else if (response.status == 402) {
            Utils.tokenExpire(requireActivity())
            Utils.showProgressDialog(requireActivity(), true)
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

        Utils.pd.dismiss()
        if (response.status == 200) {
//            val i = Intent(requireActivity(), WebviewActivity::class.java)
            val i = Intent(requireActivity(), BenefitWebviewActivity::class.java)
            if (sessionManager.languageselect == "en") {
                i.putExtra("description", response.data.privacy_Policy_Eng)

            } else {
                i.putExtra("description", response.data.terms_Conditions_Arabic)
            }
            i.putExtra("page_name", getString(R.string.privacypolicy))
            startActivity(i)
        } else if (response.status == 402) {
            Utils.tokenExpire(requireActivity())
            Utils.showProgressDialog(requireActivity(), true)
            mPresenter.getPrivacy()
        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    override fun setFaq(response: FaqModel) {

        Utils.pd.dismiss()
        if (response.status == 200) {
//            val i = Intent(requireActivity(), WebviewActivity::class.java)
            val i = Intent(requireActivity(), BenefitWebviewActivity::class.java)
            if (sessionManager.languageselect == "en") {
                i.putExtra("description", response.data.getLoyaltyFAQ_Eng)

            } else {
                i.putExtra("description", response.data.getLoyaltyFAQ_Arabic)
            }
            i.putExtra("page_name", getString(R.string.faq))
            startActivity(i)
        } else if (response.status == 402) {
            Utils.tokenExpire(requireActivity())
            Utils.showProgressDialog(requireActivity(), true)
            mPresenter.getFaqApi()
        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    override fun setHelp(response: HelpModel) {

        Utils.pd.dismiss()
        if (response.status == 200) {
//            val i = Intent(requireActivity(), WebviewActivity::class.java)
            val i = Intent(requireActivity(), BenefitWebviewActivity::class.java)
            if (sessionManager.languageselect == "en") {
                i.putExtra("description", response.data.help_Eng)

            } else {
                i.putExtra("description", response.data.help_Arabic)
            }
            i.putExtra("page_name", getString(R.string.help))
            startActivity(i)
        } else if (response.status == 402) {
            Utils.tokenExpire(requireActivity())
            Utils.showProgressDialog(requireActivity(), true)
            mPresenter.getTermAndConditions("1")
        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    override fun showReloadButton(throwable: Throwable, errorBody: ErrorBody?, network: Boolean) {
    }

    override fun showCountries(response: CountryModel) {

    }

    override fun onSuccessfullyDeletionOfAccount(response: DelUserModel) {

        Utils.pd.dismiss()
        if (response.status == 200) {
            logoutAction()

        } else if (response.status == 402) {
            Utils.tokenExpire(requireActivity())
            Utils.showProgressDialog(requireActivity(), true)
            mPresenter.delUserById(sessionManager.userId.toString())
        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }

    }


    override fun showError(error: String?) {

    }

    override fun showError(stringResId: Int) {
    }

    override fun showMessage(srtResId: Int) {
    }

    override fun showMessage(message: String) {

    }

    override fun showDilaogBox(title: String, desc: String) {
    }

    //    Added by Jaideep on 12-jan-2022.

}