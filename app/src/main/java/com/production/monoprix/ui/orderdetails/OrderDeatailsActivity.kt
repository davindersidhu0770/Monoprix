package com.production.monoprix.ui.orderdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.production.monoprix.MyApplication
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.model.Product
import com.production.monoprix.model.StatusCashPaymentModel
import com.production.monoprix.model.StatusOrderDetaileModel
import com.production.monoprix.mvp.BaseMvpActivity
import com.production.monoprix.ui.checkout.CheckoutActivity
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_orderdetails.*
import kotlinx.android.synthetic.main.activity_recyclerview.order_list
import kotlinx.android.synthetic.main.include_toolbar.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.SocketException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


class OrderDeatailsActivity :
    BaseMvpActivity<OrderDetailsContractor.View, OrderDetailsContractor.Presenter>(),
    OrderDetailsContractor.View,
    View.OnClickListener {

    override var mPresenter: OrderDetailsContractor.Presenter = OrderDetailsPresenter()
    lateinit var mAdapter: OrderDetailsAdapter
    lateinit var sessionManager: SessionManager
    var oList: ArrayList<Product> = ArrayList()
    lateinit var mLinearLayoutManager: LinearLayoutManager
    var id: Int = 0
    var storeId: Int = 0
    var imageUrl: kotlin.String? = null
    var loyaltyPoints: Double =0.0
    lateinit var jsonArray: JSONArray
    lateinit var jsonobject: JSONObject
    var cartId=0

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orderdetails)
        init()
    }

    fun init() {
        sessionManager = SessionManager(this)
        id = intent.getIntExtra("id", 0)
        storeId = intent.getIntExtra("storeId", 0)
        imageUrl = intent.getStringExtra("image")
        loyaltyPoints = intent.getDoubleExtra("loyaltyPoints",0.0)
        Picasso.with(this).load(imageUrl).into(ivQrCode)
        mLinearLayoutManager = LinearLayoutManager(this)
        order_list.layoutManager = mLinearLayoutManager
        /*order details*/
        nested.visibility=View.GONE
        Utils.showProgressDialog(this, true)
        mPresenter.getOrderDetails(id)
        /*onclick listener*/
        img_left_arrow.setOnClickListener(this)
        txt_title.text = getString(R.string.orderid) + " #" + id

        txt_verify.setOnClickListener {
            if (id != 0) {
                if (txt_verify.text == getString(R.string.verify)) {
                    verifyApiCall()
                } else {
                    // todo call reorder api
                    reorderApiCall()
                }
            }

        }



    }

    private fun reorderApiCall() {
//        Utils.showProgressDialog(this, true)
        cartDetailsJsonFormat()
        val intent = Intent(this@OrderDeatailsActivity, CheckoutActivity::class.java)
        intent.putExtra("cartDetails", jsonArray.toString())
        intent.putExtra("cartId", cartId)
        startActivity(intent)
    }
    private fun cartDetailsJsonFormat() {
        jsonArray = JSONArray()
        /*for (z in db.todoDao().getAll().indices) {
            try {
                jsonobject = JSONObject()
                jsonobject.put("ProductId", db.todoDao().getAll()[z].productId)
                jsonobject.put("Quantity", db.todoDao().getAll()[z].quantity)
                jsonobject.put("StoreId", db.todoDao().getAll()[z].storeId)
                jsonobject.put("UserId", sessionManager.userId)
                jsonArray.put(jsonobject)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }*/
        for (z in oList.indices){
            try {
                jsonobject = JSONObject()
                jsonobject.put("ProductId", oList[z].productId)
                jsonobject.put("Quantity", oList[z].quantity)
                jsonobject.put("StoreId", storeId)
                jsonobject.put("UserId", sessionManager.userId)
                jsonArray.put(jsonobject)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        println("Size of sendDetails Map: $jsonArray")
    }
    private fun verifyApiCall() {
        Utils.showProgressDialog(this, true)
        ApiManager.doCashPayment(id.toString())
            .subscribe(
                this::handleResponse, this::handleError
            )
    }

    private fun handleResponse(rsponse: StatusCashPaymentModel) {
        Utils.pd.dismiss()
        when {
            rsponse.status == 200 -> {
                Log.e("response ", "=====$rsponse")
                val intent = Intent()
                intent.putExtra("status", true)
                setResult(RESULT_OK, intent)
                finish()
            }
            rsponse.status == 402 -> {
                Utils.tokenExpire(this)
                Utils.showProgressDialog(this, true)
                verifyApiCall()
            }
            rsponse.status == 205 -> Toast.makeText(this, rsponse.statusMessage, Toast.LENGTH_SHORT)
                .show()
            else -> Toast.makeText(this, rsponse.statusMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleError(error: Throwable) {
        Utils.pd.dismiss()
        Toast.makeText(this, "Error ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
    }

    override fun showOrderDetails(response: StatusOrderDetaileModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {
            oList = response.data.products as ArrayList<Product>
            val precision = DecimalFormat("#.##", DecimalFormatSymbols.getInstance(
                Locale.ENGLISH))
            cartId=response.data.cartId
            nested.visibility=View.VISIBLE
            if (response.data.loyaltyEarned > 0) {
                tv_loyalty_p.text = ""+Utils.numbersWithoutLocalization(response.data.loyaltyEarned)
                tv_loyalty_p.visibility = View.VISIBLE
                txt_loyalty.visibility = View.VISIBLE
                loyalty_view.visibility = View.VISIBLE
            }else{
                tv_loyalty_p.visibility = View.GONE
                txt_loyalty.visibility = View.GONE
                loyalty_view.visibility = View.GONE
            }

//          txt_verify.visibility=VISIBLE//shyam update 18/06/2021
            /*if(response.data.deliveryStatus.contains("In Process", true)){
                txt_verify.background=resources.getDrawable(R.drawable.red_rectangle_corner_back)
                txt_verify.text=getString(R.string.verify)
                ivQrCode.visibility=VISIBLE
            }else{
                ivQrCode.visibility=GONE
                txt_verify.background=resources.getDrawable(R.drawable.green_rect)
                txt_verify.text=getString(R.string.re_order)
            }*/

            ivQrCode.visibility=GONE
            txt_verify.background=resources.getDrawable(R.drawable.green_rect)
            txt_verify.text=getString(R.string.re_order)
            if (response.data.loyaltyRedeemed > 0 && response.data.loyaltyRedeemed < response.data.invoiceAmount) {
                payment_mode.text = response.data.paymentMode+"\n + Loyalty"
            }else{
                payment_mode.text = response.data.paymentMode
            }
            if (response.data.address != null) {
                address_lbl.visibility = View.VISIBLE
                address_lbl.text = getString(R.string.address_lbl)
                address_lbl.compoundDrawablePadding = 5

                if (sessionManager.languageselect == "مع") {
                    address_lbl.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.shipping_img,
                        0
                    )
                    txt_order_status.text = response.data.deliveryStatusArabic

                } else {
                    address_lbl.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.shipping_img,
                        0,
                        0,
                        0
                    )
                    txt_order_status.text = response.data.deliveryStatus
                }

                address_txt.visibility = View.VISIBLE
                address_view.visibility = View.VISIBLE
                address_txt.text = response.data.address

            } else {
                address_lbl.visibility = View.VISIBLE
                address_lbl.text = getString(R.string.takeaway)
                if (sessionManager.languageselect == "مع") {
                    address_lbl.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_take_away_black,
                        0
                    )
                    txt_order_status.text = response.data.deliveryStatusArabic
                } else {
                    address_lbl.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_take_away_black,
                        0,
                        0,
                        0
                    )
                    txt_order_status.text = response.data.deliveryStatus
                }

                address_lbl.compoundDrawablePadding = 5
                address_txt.visibility = View.GONE
                address_view.visibility = View.VISIBLE

            }

            txt_sub_total.text =
                resources.getString(R.string.Qatarcurrency) + " " + Utils.numbersWithoutLocalization(
                    response.data.subTotal
                )
            txt_payableamount.text =
                resources.getString(R.string.Qatarcurrency) + " " + Utils.numbersWithoutLocalization(
                    response.data.invoiceAmount
                )

            mAdapter = OrderDetailsAdapter(oList, this)
            order_list.adapter = mAdapter
        } else if (response.status == 402) {
            Utils.tokenExpire(this)
            Utils.showProgressDialog(this, true)
            mPresenter.getOrderDetails(id)
        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_left_arrow -> {
                onBackPressed()
            }
        }
    }

}