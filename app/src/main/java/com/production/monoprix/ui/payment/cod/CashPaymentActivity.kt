package com.production.monoprix.ui.payment.cod

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.production.monoprix.MyApplication
import com.production.monoprix.R
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.model.StatusCashPaymentModel
import com.production.monoprix.ui.payment.ConfirmationActivity
import com.production.monoprix.util.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_cash_payment.*
import kotlinx.android.synthetic.main.activity_cash_payment.img_barcode
import kotlinx.android.synthetic.main.include_toolbar.*


class CashPaymentActivity : AppCompatActivity() {
    lateinit var orderId: String
    lateinit var invoiceAmount: String
    lateinit var barcodeurl: String
    lateinit var message: String
    var loyaltyPoints: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_cash_payment)
        init()
    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }
    private fun init() {
        orderId = intent.getStringExtra("orderId")!!
        invoiceAmount = intent.getStringExtra("invoiceAmount")!!
        barcodeurl = intent.getStringExtra("barcodeurl")!!
        message = intent.getStringExtra("message")!!
        loyaltyPoints = intent.getDoubleExtra("loyaltyPoints",0.0)
        txt_title.text = getString(R.string.payment)
        txt_orderid.text = getString(R.string.orderid) + " #" + orderId
        txt_orderamount.text =
            getString(R.string.orderamount) + " " + resources.getString(R.string.Qatarcurrency) + " " + invoiceAmount
        img_left_arrow.setOnClickListener{
            onBackPressed()
        }
        txt_verify.setOnClickListener {
            apiCall()
        }
        Picasso.with(this).load(barcodeurl).into(img_barcode)
        txt_message.setText(message)
    }

    private fun apiCall() {
        Utils.showProgressDialog(this, true)
        ApiManager.doCashPayment(orderId)
            .subscribe(
                this::handleResponse, this::handleError
            )
    }

    private fun handleResponse(rsponse: StatusCashPaymentModel) {
        Utils.pd.dismiss()
        when {
            rsponse.status == 200 -> {
                val i = Intent(this, ConfirmationActivity::class.java)
                i.putExtra("orderId", orderId)
                i.putExtra("invoiceAmount", invoiceAmount)
                i.putExtra("barcodeurl", barcodeurl)
                i.putExtra("loyaltyPoints", loyaltyPoints)
                startActivity(i)
            }
            rsponse.status == 402 -> {
                Utils.tokenExpire(this)
                Utils.showProgressDialog(this, true)
                apiCall()
            }
            rsponse.status == 205 -> Toast.makeText(this, rsponse.statusMessage, Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(this, rsponse.statusMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleError(error: Throwable) {
        Utils.pd.dismiss()
        Toast.makeText(this, "Error ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
    }


}