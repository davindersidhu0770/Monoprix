package com.production.monoprix.ui.payment

import android.content.Context
import android.content.Intent
import android.util.Log
import android.webkit.JavascriptInterface

class MyJavaScriptInterface internal constructor(private val ctx: Context, var orderId: String, var loyaltyPoints: Double) {

    @JavascriptInterface
    fun showHTML(html: String) {
        println(html)
        Log.d("7June: ",html)
        if (html.contains("\"status\":200")) {
            val i = Intent(ctx, ConfirmationActivity::class.java)
            i.putExtra("orderId", orderId)
            i.putExtra("loyaltyPoints", loyaltyPoints)
            i.putExtra("jsonresponse", html)
            ctx.startActivity(i)

        } else if (html.contains("\"status\":400")) {
            val i = Intent(ctx, PaymentFailureActivity::class.java)
            ctx.startActivity(i)
        }

    }

}
