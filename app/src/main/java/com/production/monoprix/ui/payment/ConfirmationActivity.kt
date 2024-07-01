package com.production.monoprix.ui.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.production.monoprix.MyApplication
import com.production.monoprix.R
import com.production.monoprix.room.AppDatabase
import com.production.monoprix.ui.home.HomeActivity
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_confirmation.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.json.JSONException
import org.json.JSONObject


class ConfirmationActivity : AppCompatActivity(), View.OnClickListener {

    var orderId: String? = null
    var orderAmount: String? = null
    var jsonResponse: String? = null
    var invoiceAmount: String? = null
    var barcodeurl: String? = null
    var loyaltyPoints: Double = 0.0
    lateinit var sessionManager: SessionManager
    lateinit var db: AppDatabase

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        setContentView(R.layout.activity_confirmation)

        init()
    }

    fun init() {
        db = AppDatabase(this)
        sessionManager = SessionManager(this)

        db.todoDao().deleteAll()
        sessionManager.totalCart = "0"

        jsonResponse = intent.getStringExtra("jsonresponse")
        orderId = intent.getStringExtra("orderId")
        invoiceAmount = intent.getStringExtra("invoiceAmount")
        barcodeurl = intent.getStringExtra("barcodeurl")
        loyaltyPoints = intent.getDoubleExtra("loyaltyPoints", 0.0)
        if (invoiceAmount != null) {
            txt_or_amount.text =
                getString(R.string.orderamount) + " " + resources.getString(R.string.Qatarcurrency) + " " + invoiceAmount
        }
        if (loyaltyPoints > 0) {
            txt_loyalty_point.text =
                getString(R.string.loyal_point) + ": " + Utils.numbersWithoutLocalization(
                    loyaltyPoints
                )
            txt_loyalty_point.visibility = View.VISIBLE
        } else {
            txt_loyalty_point.visibility = View.GONE
        }
        if (jsonResponse == null) {
            txt_order_id.text = getString(R.string.orderid) + " #" + orderId
            if (barcodeurl != null)
                Picasso.with(this).load(barcodeurl).into(img_barcode)
        } else {
            orderSuccess(jsonResponse)
        }
        txt_title.text = getString(R.string.confirmation)
        txt_confirm_emial.text = getString(R.string.confemail) + "\n" + sessionManager.email

        // dateConversionDeliveryNow(deliveryDate)
        /*onclick listener*/
        img_left_arrow.setOnClickListener(this)
        txt_cont_shopping.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_left_arrow -> {
                db.todoDao().deleteAll()
                sessionManager.totalCart = "0"
                val i = Intent(this, HomeActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
            }
            R.id.txt_cont_shopping -> {
                db.todoDao().deleteAll()
                sessionManager.totalCart = "0"
                val i = Intent(this, HomeActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
            }
        }
    }


    private fun orderSuccess(jsonresponse: String?) {
        /*val newResult = jsonresponse!!.replace(
            "<html><head></head><body><pre style=\"word-wrap: break-word; white-space: pre-wrap;\">",
            ""
        )*/

        val newResult = jsonresponse!!.replace(
            "<html><head><meta name=\"color-scheme\" content=\"light dark\"></head><body><pre style=\"word-wrap: break-word; white-space: pre-wrap;\">",
            ""
        )
        Log.i("new", newResult + "")
        val secondResult = newResult.replace("</pre></body></html>", "")
        Log.i("second", secondResult + "")

        var jo: JSONObject? = null
        try {
            jo = JSONObject(secondResult).getJSONObject("data")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        if (jo != null) {
            orderId = jo!!.optString("orderid")
            orderAmount = jo.optString("invoiceAmount")
            txt_or_amount.text =
                getString(R.string.orderamount) + " " + resources.getString(R.string.Qatarcurrency) + " " + orderAmount
            txt_order_id.text = getString(R.string.orderid) + " #" + orderId
            Picasso.with(this).load(jo.optString("qrCodeImageUrl")).into(img_barcode)
        } else {
            txt_order_id.text = getString(R.string.orderid) + " #" + orderId
            if (barcodeurl != null)
                Picasso.with(this).load(barcodeurl).into(img_barcode)
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
          db.todoDao().deleteAll()
          sessionManager.totalCart = "0"
          val i = Intent(this, HomeActivity::class.java)
          i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
          startActivity(i)
    }

}