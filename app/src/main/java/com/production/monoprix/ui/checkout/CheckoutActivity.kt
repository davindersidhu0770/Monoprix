package com.production.monoprix.ui.checkout

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.production.monoprix.MyApplication
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.*
import com.production.monoprix.mvp.BaseMvpActivity
import com.production.monoprix.room.AppDatabase
import com.production.monoprix.ui.add_new_address.AddNewAddressActivity
import com.production.monoprix.ui.address_list.AddressListActivity
import com.production.monoprix.ui.home.HomeActivity
import com.production.monoprix.ui.payment.PaymentActivity
import com.production.monoprix.util.RecyclerItemClickListener
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import com.production.monoprix.util.Utils.Companion.convertStringDateToAnotherStringDate
import com.production.monoprix.util.Utils.Companion.getCurrentDateTime
import com.production.monoprix.util.Utils.Companion.timeIsGreater
import com.production.monoprix.util.Utils.Companion.toSimpleString
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.activity_empty_layout.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.json.JSONArray
import java.lang.Exception
import java.net.SocketException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CheckoutActivity : BaseMvpActivity<CheckoutContractor.View, CheckoutContractor.Presenter>(),
    CheckoutContractor.View, View.OnClickListener {
    private  var completeAddress: String=""
    override var mPresenter: CheckoutContractor.Presenter = CheckoutPresenter()
    lateinit var sessionManager: SessionManager
    lateinit var loyalty_message_description: String
    lateinit var loyalty_message_title: String
    var loyaltyBalance: Double = 0.00
    var cList: ArrayList<CartList> = ArrayList<CartList>()
    var cListnew: ArrayList<CartList> = ArrayList<CartList>()
    var list: ArrayList<CartList> = ArrayList<CartList>()
    var tList: ArrayList<TimeSlot> = ArrayList()
    lateinit var mLinearLayoutManager: LinearLayoutManager
    lateinit var mLayoutManager: LinearLayoutManager
    lateinit var mAdapter: CheckoutAdapter
    lateinit var mtAdapter: TimeSlotAdapter
    var cartDetails: String? = null
    var addressId: String = ""
    private lateinit var aList: ArrayList<AddressLists>
    var strPhone: String? = null
    var strAddress: String? = null
    var strAreaName: String? = null
    var strCityName: String? = null
    var cityId: String? = null
    var house_no: String? = null
    var muncipality: String? = null
    var place: String? = null
    var landMark: String? = null
    var building: String? = null
    var street: String? = null
    var zoneNo: String? = null
    var strAddressType: String? = null
    var strEmail: String? = null
    var strDefaultAddress: String? = null
    var strTimeSlotId: String = ""
    var strCountryName: String = ""
    var strPostalCode: String = ""
    var currentDateStr: String = ""
    var currentTimeStr: String = ""
    var cartId: Int = 0
    var timeslotValue: Boolean = false
    var isCallShippingAddress: Boolean = false
    var productId: String = ""
    var isGetItem: Boolean = false
    var quantity: String = ""
    var storeId: String = ""
    var userID: String = ""
    var manualPrice: Double = 0.0
    var salesPrice: Double = 0.0
    lateinit var finaljsonArray: JSONArray
    lateinit var db: AppDatabase


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        init()
    }

    fun init() {
        db = AppDatabase(this)
        cartDetails = intent.getStringExtra("cartDetails")
        cartId = intent.getIntExtra("cartId", 0)
        try {
            finaljsonArray = JSONArray(cartDetails)
        } catch (e: Exception) {
            Log.d("ArrayException", "init: " + e)
        }

        println("vikas == " + cartDetails)
        txt_title.text = getString(R.string.checkout_ti)
        img_left_arrow.setOnClickListener(this)
        txt_more_items.setOnClickListener(this)
        txt_less_items.setOnClickListener(this)
        txt_checkout.setOnClickListener(this)
        c1.setOnClickListener(this)
        c2.setOnClickListener(this)
        button_edit_address_page.setOnClickListener(this)
        txt_change_address.setOnClickListener(this)
        txt_checkout.setOnClickListener(this)
        txt_add_products.setOnClickListener(this)
        sessionManager = SessionManager(this)
        mLinearLayoutManager = LinearLayoutManager(this)
        product_list.layoutManager = mLinearLayoutManager
        mLayoutManager = LinearLayoutManager(this)
        txt_delivery_slot.setOnClickListener(this)
        arrow_btn.setOnClickListener(this)
        time_slot.layoutManager = mLayoutManager
        txt_more_items.paintFlags = txt_more_items.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        txt_less_items.paintFlags = txt_less_items.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        /*product list*/
//      Utils.showProgressDialog(this, true)
        Utils.showProgressDialog(this, true)
        nested.visibility = View.INVISIBLE

        if (cartDetails == null) {
            mPresenter.getProductList(
                sessionManager.userId.toString(), cartId
            )

            Log.d("15nov:", "cartDetails == null")

        } else {

            Log.d("finaljsonArray :", finaljsonArray.toString())
            mPresenter.getaddProduct(
                finaljsonArray
            )

            Log.d("15nov:", "else: " + finaljsonArray)

        }
        /*address list*/

        mPresenter.getShippingAddressList(sessionManager.userId.toString())
        /*time slot*/
        mPresenter.getTimeslot()
        /*recyclerView onItemClick listener*/
        time_slot.addOnItemTouchListener(
            RecyclerItemClickListener(this, time_slot,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        for (i in tList.indices) {
                            tList[i].isClicked = false
                        }
                        var strDate = currentDateStr.split("T")
                        var startTime = tList[position].slotName.split("-")
                        val calendar1: Calendar = Calendar.getInstance()
                        val formatter1 = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
                        val currentTime = formatter1.format(calendar1.time)

                        if (checkDates(
                                convertStringDateToAnotherStringDate
                                    (strDate[0], "yyyy-MM-dd", "dd-MMM-yyyy")
                            ) && timeIsGreater(startTime[0], currentTime)
                        ) {
                            if (tList[position].isClicked) {
                                tList[position].isClicked = false
                            } else {
                                tList[position].isClicked = true
                                strTimeSlotId = tList[position].id
                            }
                            mtAdapter.notifyDataSetChanged()
                        } else {
                            showTimeSlotAlert(position)
                        }

                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                }
            ))
    }

    fun showTimeSlotAlert(position: Int) {
        val dialogBuilder = android.app.AlertDialog.Builder(this)
        dialogBuilder.setTitle(getString(R.string.txt_alert))
        dialogBuilder.setMessage(getString(R.string.selected_slot))
            .setCancelable(true)
            .setPositiveButton(
                getString(R.string.confirm),
                DialogInterface.OnClickListener { dialog, id ->

                    if (tList[position].isClicked) {
                        tList[position].isClicked = false
                    } else {
                        tList[position].isClicked = true
                        strTimeSlotId = tList[position].id
                    }
                    mtAdapter.notifyDataSetChanged()

                })
            .setNegativeButton(
                getString(R.string.cancel),
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })
        val alert = dialogBuilder.create()
        alert.show()
    }

    fun checkDates(endDate: String?): Boolean {
        val date = getCurrentDateTime()
        val startDate = toSimpleString(date, "dd-MMM-yyyy")
        Log.e("date compare ", "=== ${startDate} ==== ${endDate}")
        val dfDate = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)

        var b = false
        try {
            b = when {
                dfDate.parse(startDate).before(dfDate.parse(endDate)) -> {
                    false // If start date is before end date.

                }
                dfDate.parse(startDate).equals(dfDate.parse(endDate)) -> {
                    true // If two dates are equal.
                }
                else -> {
                    true // If start date is after the end date.
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return b
    }

    fun apiCall(newCartId: Int) {
        isGetItem = true
        Utils.showProgressDialog(this, true)
        mPresenter.getProductList(
            sessionManager.userId.toString(),
            newCartId
        )
    }

    override fun showProductList(response: StatusModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {
            nested.visibility = View.VISIBLE
            txt_checkout.visibility = View.VISIBLE
            cList = response.data.cartitems as ArrayList<CartList>
            if (!isGetItem) {
                if (sessionManager.languageselect == "مع") {
                    if (response.data.loyalty_message_title_ar == null || response.data.loyalty_message_title_ar.equals(
                            "null"
                        )
                    ) {
                        loyalty_message_description = response.data.loyalty_message_description
                        loyalty_message_title = response.data.loyalty_message_title
                    } else {
                        loyalty_message_description = response.data.loyalty_message_description_ar
                        loyalty_message_title = response.data.loyalty_message_title_ar
                    }
                } else {
                    loyalty_message_description = response.data.loyalty_message_description
                    loyalty_message_title = response.data.loyalty_message_title
                }
                loyaltyBalance = response.data.loyaltyBalance
            }
            isGetItem = false
            for (i in cList.indices) {
                Log.e(
                    "shyam_test",
                    "+cList[" + i + "].cartId= " + cList[i].cartId/*cList[i].pluBarcode*/
                )
                db.todoDao()
                    .updateCartItemId(Integer.parseInt(cList[i].cartItemId), cList[i].barcode)
            }

            sessionManager.totalCart = cList.size.toString()
            currentDateStr = response.data.currentDate
            if (cList.isEmpty()) {
                include_empty_cart.visibility = View.VISIBLE
                txt_checkout.visibility = View.GONE

            } else {
                cListnew.clear()
                include_empty_cart.visibility = View.GONE
                txt_checkout.visibility = View.VISIBLE
                list.clear()
                list.addAll(cList)
                cListnew.addAll(cList)
                if (cList.size > 2) {
                    list.subList(2, list.size).clear()
                    mAdapter = CheckoutAdapter(
                        list,
                        this,
                        txt_sub_total,
                        txt_payableamount,
                        lable_home_delivery_err_msg,
                        c2,
                        response.data.subTotalAmount
                    )
                    txt_more_items.visibility = View.VISIBLE
                    txt_less_items.visibility = View.GONE
                    txt_more_items.text =
                        (cList.size - 2).toString() + " " + getString(R.string.moreitems)
                } else {
                    txt_more_items.visibility = View.GONE
                    txt_less_items.visibility = View.GONE
                    mAdapter = CheckoutAdapter(
                        cList,
                        this,
                        txt_sub_total,
                        txt_payableamount,
                        lable_home_delivery_err_msg,
                        c2,
                        response.data.subTotalAmount
                    )
                }
                product_list.adapter = mAdapter
                txt_sub_total.text =
                    resources.getString(R.string.Qatarcurrency) + " " + response.data.subTotalAmount
                txt_payableamount.text =
                    resources.getString(R.string.Qatarcurrency) + " " + response.data.total
            }

            if (response.data.subTotalAmount.toString().toDouble() < 99.00) {
                c1.isEnabled = true
                readio_take_away.isChecked = false
//                lable_take_away.setTextColor(ContextCompat.getColor(this, R.color.green))
//                img_take_away.setImageResource(R.drawable.ic_take_away_green)
            }
            /*if(response.data.is_homedelivery_available){
                c2.isEnabled = true
                c2.alpha = 1f
                lable_home_delivery_err_msg.visibility = View.GONE
            }else {
                c2.isEnabled = false
                c2.alpha = 0.5f
                lable_home_delivery_err_msg.visibility = View.VISIBLE
                lable_home_delivery_err_msg.text = response.data.delivery_message
            }*/
            if (response.data.subTotalAmount.toDouble() < 99.00) {
                c2.isEnabled = false
                c2.alpha = 0.5f
//                lable_home_delivery_err_msg.visibility = View.VISIBLE
//                ChangeTakeAway()
//                ChangeHomeDelivery()
            }
            lable_home_delivery_err_msg.visibility = View.VISIBLE
            lable_home_delivery_err_msg.text =
                if (sessionManager.languageselect == "en") response.data.delivery_message else response.data.delivery_message_arabic

        } /*else if (response.status == 402) {
            Utils.tokenExpire(this)
            apiCall(cartId)
        } */ else {
            nested.visibility = View.GONE
            txt_checkout.visibility = View.GONE
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

//    fun ChangeTakeAway() {
//        c1.isEnabled = true
//        readio_take_away.isChecked = true
//        lable_take_away.setTextColor(ContextCompat.getColor(this, R.color.green))
//        img_take_away.setImageResource(R.drawable.ic_take_away_green)
//    }

    fun ChangeHomeDeliveryNew() {
        Log.e("minus_disable", "c2.isEnabled= " + c2.isEnabled);
        if (c2.isEnabled) {
            readio_home_delivery.isChecked = false
            c3.visibility = View.GONE
            view9.visibility = View.VISIBLE
            lable_home_delivery.setTextColor(ContextCompat.getColor(this, R.color.black))
            img_home_delivery.setImageResource(R.drawable.ic_homedelivery)
        }
    }

    fun ChangeHomeDelivery() {
        if (c2.isEnabled) {
            readio_take_away.isChecked = true
            readio_home_delivery.isChecked = false
            c3.visibility = View.GONE
            view9.visibility = View.VISIBLE
            lable_take_away.setTextColor(ContextCompat.getColor(this, R.color.green))
            img_take_away.setImageResource(R.drawable.ic_takeawaygreen) //here

            lable_home_delivery.setTextColor(ContextCompat.getColor(this, R.color.black))
            img_home_delivery.setImageResource(R.drawable.ic_homedelivery)

        }
    }

    override fun setShippingAddressList(response: AddressStatus?) {
        if (isCallShippingAddress)
            Utils.pd.dismiss()
        if (response!!.status == 200) {
            button_edit_address_page.visibility = View.VISIBLE
            txt_address.visibility = View.VISIBLE
            txt_home.visibility = View.VISIBLE
            txt_change_address.text = getString(R.string.sel_address)
            aList = response.data as ArrayList<AddressLists>
            if (aList.size > 0) {
                for (i in aList.indices) {
                    if (aList[i].is_defalut_shipping_address) {
                        txt_address.visibility = View.VISIBLE
                        button_edit_address_page.visibility = View.VISIBLE
                        txt_home.visibility = View.VISIBLE
                        txt_change_address.text = getString(R.string.changeaddress)
                        if (aList[i].address_type == "1") {
                            txt_home.text = getString(R.string.label_home)
                        } else if (aList[i].address_type == "2") {
                            txt_home.text = getString(R.string.label_office)
                        } else if (aList[i].address_type.equals("3")) {
                            txt_home.text = getString(R.string.label_other)
                        } else if (aList[i].address_type.equals(getString(R.string.label_home))) {
                            txt_home.text = getString(R.string.label_home)
                        } else if (aList[i].address_type.equals(getString(R.string.label_office))) {
                            txt_home.text = getString(R.string.label_office)
                        }

                        completeAddress=""

                        if (aList[i].building !=null)
                            completeAddress = "\nBuilding : "+aList[i].building
                        if (aList[i].houseNo !=null)
                            completeAddress = completeAddress +" House No: "+ aList[i].houseNo
                        if (aList[i].muncipality !=null)
                            completeAddress = completeAddress+" " + aList[i].muncipality
                        if (aList[i].place !=null){

                            place= aList[i].place
                            completeAddress = completeAddress+" " + aList[i].place

                        }
                        if (aList[i].landMark !=null)
                            completeAddress = completeAddress +" \nLandmark : "+ aList[i].landMark

//                        Log.d("27Nov:","Place Name :"+ aList[i].place)

                        txt_address.text=  sessionManager.name + " " + sessionManager.familyName + "," +completeAddress+
                                "\n" + getString(R.string.mobile) + aList[i].mobile_number

                        /*txt_address.text = if (aList[i].addressDesc.isNullOrEmpty())
                            sessionManager.name + " " + sessionManager.familyName +
                                    "," + aList[i].area_name + "," + aList[i].city_name +
                                    "\n" + getString(R.string.mobile) + aList[i].mobile_number

                        else sessionManager.name + " " + sessionManager.familyName +
                                "," + aList[i].addressDesc + "," + aList[i].area_name +
                                "," + aList[i].city_name +
                                "\n" + getString(R.string.mobile) + aList[i].mobile_number*/

                        addressId = aList[i].shipping_address_id
                        strPhone = aList[i].mobile_number
                        strAddress = aList[i].addressDesc
                        strAreaName = aList[i].area_name
                        strCityName = aList[i].city_name
                        cityId = aList[i].city_id
                        strAddressType = aList[i].address_type
                        zoneNo = aList[i].zoneId.toString()

                        Log.d("27Nov:","set zoneId"+aList[i].zoneId)

                        street = aList[i].street
                        building = aList[i].building
                        house_no = aList[i].houseNo
                        landMark = aList[i].landMark
                        strEmail = sessionManager.email
                        strDefaultAddress = aList[i].is_defalut_shipping_address.toString()
                        strCountryName = aList[i].countryname
                        strPostalCode = aList[i].postcode ?: ""

                        break
                    } else {
                        button_edit_address_page.visibility = View.GONE
                        txt_address.visibility = View.GONE
                        txt_home.visibility = View.GONE
                        txt_change_address.text = getString(R.string.addaddress)
                    }
                }
            } else {
                txt_change_address.text = getString(R.string.addaddress)
                button_edit_address_page.visibility = View.GONE
                txt_address.visibility = View.GONE
                txt_home.visibility = View.GONE

            }
        } else if (response.status == 402) {
            Utils.tokenExpire(this)
            isCallShippingAddress = true
            Utils.showProgressDialog(this, true)
            mPresenter.getShippingAddressList(sessionManager.userId.toString())
        } else {
            txt_change_address.text = getString(R.string.addaddress)
            button_edit_address_page.visibility = View.GONE
            txt_address.visibility = View.GONE
            txt_home.visibility = View.GONE
        }
    }

    override fun showTimeSlot(response: StatusTimeSlotModel) {
        if (response.status == 200) {
            tList = response.data as ArrayList<TimeSlot>
            mtAdapter = TimeSlotAdapter(tList, this)
            time_slot.adapter = mtAdapter
        } else if (response.status == 402) {
            Utils.tokenExpire(this)
            mPresenter.getTimeslot()
        }
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

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.img_left_arrow -> {
                onBackPressed()
            }
            R.id.txt_more_items -> {
                if (cList.size != cListnew.size) {
                    cList.clear()
                    cList.addAll(cListnew)
                    cList.subList(0, 2).clear()
                } else {
                    cList.subList(0, 2).clear()
                }
                mAdapter.addData(cList)
                txt_more_items.visibility = View.GONE
                txt_less_items.visibility = View.VISIBLE
            }
            R.id.txt_less_items -> {
                mAdapter.lessData()
                txt_less_items.visibility = View.GONE
                txt_more_items.visibility = View.VISIBLE
            }
            R.id.txt_checkout -> {
                if (readio_take_away.isChecked || readio_home_delivery.isChecked) {
                    if (readio_take_away.isChecked) {
                        val i = Intent(this, PaymentActivity::class.java)
                        i.putExtra("subTotal", txt_sub_total.text.toString())
                        i.putExtra("paybleAmount", txt_payableamount.text.toString())
                        i.putExtra("loyaltyBalance", loyaltyBalance)
                        i.putExtra("loyalty_message_title", "" + loyalty_message_title)
                        i.putExtra("loyalty_message_description", "" + loyalty_message_description)
                        startActivity(i)
                    } else {
                        if (addressId.isNotEmpty()) {
                            if (strTimeSlotId.isNotEmpty()) {
                                val i = Intent(this, PaymentActivity::class.java)
                                i.putExtra("addresId", addressId)
                                i.putExtra("slotId", strTimeSlotId)
                                i.putExtra("subTotal", txt_sub_total.text.toString())
                                i.putExtra("paybleAmount", txt_payableamount.text.toString())
                                i.putExtra("loyaltyBalance", loyaltyBalance)
                                i.putExtra("loyalty_message_title", loyalty_message_title)
                                i.putExtra(
                                    "loyalty_message_description",
                                    loyalty_message_description
                                )
                                i.putExtra("addressType", txt_home.text.toString())
                                i.putExtra("address", txt_address.text.toString())
                                startActivity(i)
                            } else {
                                showMessage(getString(R.string.er_delivery_slot))
                            }
                        } else {
                            showMessage(getString(R.string.er_address))
                        }
                    }
                } else {
                    showMessage(getString(R.string.et_deliverytype))
                }
            }
            R.id.c1 -> {
                if (c1.isEnabled) {
                    TakeWayDialog();
                }
//                readio_take_away.isChecked = true
//                readio_home_delivery.isChecked = false
//                c3.visibility = View.GONE
//                view9.visibility = View.VISIBLE
//                lable_take_away.setTextColor(ContextCompat.getColor(this, R.color.green))
//                lable_home_delivery.setTextColor(ContextCompat.getColor(this, R.color.black))
//                img_home_delivery.setImageResource(R.drawable.ic_home_delivery)
//                img_take_away.setImageResource(R.drawable.ic_take_away_green)
            }
            R.id.c2 -> {
//                if (response.data.subTotalAmount.toString().toDouble() < 99.00) {
//
//                }
                if (c2.isEnabled) {
                    HomeDeliveryDialog()
                }
            }
            R.id.button_edit_address_page -> {
                val i = Intent(this, AddNewAddressActivity::class.java)
                i.putExtra("addressID", addressId)
                i.putExtra("email", strEmail)
                i.putExtra("phone", strPhone)

//                Log.d("16Nov: put"," strAreaName "+strAreaName)
//                Log.d("16Nov: put"," street "+street)

                i.putExtra("address", strAddress)
                i.putExtra("area", strAreaName)
                i.putExtra("city", strCityName)
                i.putExtra("cityId", cityId)
                i.putExtra("zone_no", zoneNo)
                i.putExtra("street", street)
                i.putExtra("building", building)
                i.putExtra("landMark", landMark)
                i.putExtra("house_no", house_no)
                i.putExtra("address_type", strAddressType)
                i.putExtra("isDefaultAddress", strDefaultAddress)
                i.putExtra("country", strCountryName)
                i.putExtra("postcode", strPostalCode)
                i.putExtra("pageName", "1")
                i.putExtra("diff", "checkout")
                i.putExtra("place", place)

                Log.d("27Nov: put"," zone_no "+zoneNo)

                startActivityForResult(i, 200)

            }
            R.id.txt_change_address -> {
                val i = Intent(this, AddressListActivity::class.java)
//                i.putExtra("aList", aList)
                i.putExtra("diff", "checkout")
                i.putExtra("AddressID", addressId)
                startActivityForResult(i, 200)
            }
            R.id.txt_add_products -> {
                val i = Intent(this, HomeActivity::class.java)
                i.putExtra("position", 2)
                startActivity(i)
                finish()
            }
            R.id.txt_delivery_slot -> {
                if (timeslotValue) {
                    timeslotValue = false
                    time_slot.visibility = View.GONE
                    arrow_btn.setImageResource(R.drawable.arrow_down_red)
                } else {
                    timeslotValue = true
                    time_slot.visibility = View.VISIBLE
                    arrow_btn.setImageResource(R.drawable.ic_arrow_up_red)
                }

            }
            R.id.arrow_btn -> {
                if (timeslotValue) {
                    timeslotValue = false
                    time_slot.visibility = View.GONE
                    arrow_btn.setImageResource(R.drawable.arrow_down_red)
                } else {
                    timeslotValue = true
                    time_slot.visibility = View.VISIBLE
                    arrow_btn.setImageResource(R.drawable.ic_arrow_up_red)
                }
            }
        }

    }

    fun TakeWayDialog() {
        val dialog = Dialog(this, R.style.MyDialogTheme1)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dailog_takeway)
        val dialogButton: Button = dialog.findViewById(R.id.yes) as Button
        dialogButton.setOnClickListener(View.OnClickListener {
            readio_take_away.isChecked = true
            readio_home_delivery.isChecked = false
            c3.visibility = View.GONE
            view9.visibility = View.VISIBLE
            lable_take_away.setTextColor(ContextCompat.getColor(this, R.color.green))
            lable_home_delivery.setTextColor(ContextCompat.getColor(this, R.color.black))
            img_home_delivery.setImageResource(R.drawable.ic_homedelivery)
            img_take_away.setImageResource(R.drawable.ic_takeawaygreen)//here

            dialog.dismiss()
        }
        )
        dialog.show()
    }

    fun HomeDeliveryDialog() {
        val dialog = Dialog(this, R.style.MyDialogTheme1)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dailog_home_delivery)
        val dialogButton: Button = dialog.findViewById(R.id.yes) as Button
        dialogButton.setOnClickListener(View.OnClickListener {
            readio_home_delivery.isChecked = true
            readio_take_away.isChecked = false
            c3.visibility = View.VISIBLE
            view9.visibility = View.GONE

            timeslotValue = true
            time_slot.visibility = View.VISIBLE
            arrow_btn.setImageResource(R.drawable.ic_arrow_up_red)

            lable_take_away.setTextColor(ContextCompat.getColor(this, R.color.black))
            lable_home_delivery.setTextColor(ContextCompat.getColor(this, R.color.green))
            img_home_delivery.setImageResource(R.drawable.homedeliverygreen)

            img_take_away.setImageResource(R.drawable.takeaway)
            dialog.dismiss()
        }
        )
        dialog.show()
    }
    /* Dialog logoutDlg=null;
    public void logout(String mes, final int dif) {
        try {
            if (logoutDlg != null) {
                logoutDlg.cancel();
            }
            logoutDlg = new Dialog(this, R.style.MyDialogTheme1);
            logoutDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            logoutDlg.setContentView(R.layout.logout_app);
            logoutDlg.setCanceledOnTouchOutside(false);
            TextView tvTitle1 = (TextView) logoutDlg.findViewById(R.id.tv_title);
            TextView tvsg = (TextView) logoutDlg.findViewById(R.id.exittext);
            tvsg.setText(mes);
            Button no = (Button) logoutDlg.findViewById(R.id.No);
            Button yes = (Button) logoutDlg.findViewById(R.id.yes);
            Button divi = (Button) logoutDlg.findViewById(R.id.divide_button);
            logoutDlg.show();
            if(dif == 1)
            {
                tvTitle1.setText(getResources().getString(R.string.logi_out));
            }
            else if(dif == 2)
            {
                tvTitle1.setText(getResources().getString(R.string.exit));
            }

            if (dif == 3) {
                no.setText(getResources().getString(R.string.ok_thankyou));
            } else {
                divi.setVisibility(View.VISIBLE);
                yes.setVisibility(View.VISIBLE);
                no.setText(getResources().getString(R.string.cancel));
            }
            yes.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        if (dif == 1) {
                            AuthModule auth = AuthModule.getAuthModule(getApplicationContext());
                            Log.e("splash_token","token="+auth.getAuthToken());
                            auth.setAuthToken("");
//                            auth.clearAuthToken();
                          *//*  Intent i = new Intent(Dashboard.this,
                                    LoginActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);*//*
                            finish();
                        } else if (dif == 2) {
                            finish();
                        }
                        if (logoutDlg != null) {
                            logoutDlg.dismiss();
                        }

                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
            });
            no.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (logoutDlg != null) {
                        logoutDlg.cancel();
                    }
                }
            });
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // check if the request code is same as what is passed  here it is 200 after delete
        if (requestCode == 200 && data != null) {

            button_edit_address_page.visibility = View.VISIBLE
            txt_address.visibility = View.VISIBLE
            txt_home.visibility = View.VISIBLE
            txt_change_address.text = getString(R.string.changeaddress)
            strPhone = data.getStringExtra("phone")
            strAddress = data.getStringExtra("address")
            strAreaName = data.getStringExtra("area")
            strCityName = data.getStringExtra("city")
            strAddressType = data.getStringExtra("address_type")
            addressId = data.getStringExtra("addressID") ?: ""
            strPostalCode = data.getStringExtra("postcode") ?: ""
            strCountryName = data.getStringExtra("country") ?: ""
            strDefaultAddress = data.getStringExtra("isDefaultAddress")
            street = data.getStringExtra("street")

            building = data.getStringExtra("building")
            landMark = data.getStringExtra("landMark")
            house_no = data.getStringExtra("house_no")
            muncipality = data.getStringExtra("muncipality")
            place = data.getStringExtra("place")
            zoneNo = data.getStringExtra("zone_no")
            zoneNo = data.getIntExtra("zone",0).toString()
            Log.d("27Nov:"," onActivityResult")

//          Log.d("22Nov:"," building "+building)
            Log.d("27Nov:","zone_no onn "+zoneNo)

            if (strAddressType.equals("1")) {
                txt_home.text = getString(R.string.label_home)
            } else if (strAddressType.equals("2")) {
                txt_home.text = getString(R.string.label_office)
            } else if (strAddressType.equals("3")) {
                txt_home.text = getString(R.string.label_other)
            } else if (strAddressType.equals(getString(R.string.label_home))) {
                txt_home.text = getString(R.string.label_home)
            } else if (strAddressType.equals(getString(R.string.label_office))) {
                txt_home.text = getString(R.string.label_office)
            }

//          Log.d("22Nov:"," muncipality "+muncipality+"place "+ place+"landMark "+landMark)

            completeAddress=""

            if (building !=null)
                completeAddress = "\nBuilding : "+building
            if (house_no !=null)
                completeAddress = completeAddress +" House No: "+ house_no
            if (muncipality !=null)
                completeAddress = completeAddress+" " + muncipality
            if (place !=null)
                completeAddress = completeAddress+" " + place
            if (landMark !=null)
                completeAddress = completeAddress +" \nLandmark : "+ landMark

            txt_address.text=  sessionManager.name + " " + sessionManager.familyName + "," +completeAddress+
                    "\n" + getString(R.string.mobile) + strPhone

            /*txt_address.text =
                if (strAddress.isNullOrEmpty()){
                sessionManager.name + " " + sessionManager.familyName + "," + strAreaName + "," +
                        street + "\n" + getString(R.string.mobile) + strPhone

            }

            else {
                sessionManager.name + " " + sessionManager.familyName + "," + strAddress +
                        "," + strAreaName + "," + street +
                        "\n" + getString(R.string.mobile) + strPhone

            }*/

        } else if (resultCode == 300) {
            Utils.showProgressDialog(this, true)
            isCallShippingAddress = true
            mPresenter.getShippingAddressList(sessionManager.userId.toString())
        }
    }

}