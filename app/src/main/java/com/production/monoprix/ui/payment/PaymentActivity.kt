package com.production.monoprix.ui.payment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.production.monoprix.MyApplication
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.RedeemPointModel
import com.production.monoprix.model.StatusModel
import com.production.monoprix.mvp.BaseMvpActivity
import com.production.monoprix.ui.address_list.AddressListActivity
import com.production.monoprix.ui.payment.cod.CashPaymentActivity
import com.production.monoprix.ui.payment.webView.PaymentWebview
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.dialog_loyalty_point.*
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.layout_scan_cart_items.*
import okhttp3.internal.Util
import java.net.SocketException

class PaymentActivity : BaseMvpActivity<PaymentContractor.View, PaymentContractor.Presenter>(),
    PaymentContractor.View, View.OnClickListener {

    var loyaltyPointCustomDialog: Dialog? = null
    lateinit var subTotal: String
    lateinit var loyalty_message_description: String
    lateinit var loyalty_message_title: String
    var loyaltyBalance: Double = 0.00
    lateinit var payableAmount: String
    lateinit var mainPayableAmount: String
    var addressId: String? = null
    var loyalty_points: String = "0.00"
    var slotId: String? = null
    var addressType: String? = null
    var address: String? = null
    lateinit var sessionManager: SessionManager
    override var mPresenter: PaymentContractor.Presenter = PaymentPresenter()
    var strFirstName: String? = null
    var strLastName: String? = null
    var strPhone: String? = null
    var strAddress: String? = null
    var strAreaName: String? = null
    var strCityName: String? = null
    var strAddressType: String? = null
    lateinit var strPaymentType: String
    lateinit var cardType: String
    var isDeliveryOpted: Boolean = false
    var loyaltyRedeemed: String = "0.0"

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        init()
    }

    fun init() {
        sessionManager = SessionManager(this)
        /*onclick listner*/
        img_left_arrow.setOnClickListener(this)
        r_cash.setOnClickListener(this)
        r_cred.setOnClickListener(this)
        r_pay_pal.setOnClickListener(this)
        r_debit.setOnClickListener(this)
        img_loyalty_point.setOnClickListener(this)
        txt_pay_now.setOnClickListener(this)
        txt_change_address.setOnClickListener(this)
        txt_change.setOnClickListener(this)
        txt_title.text = getString(R.string.payment)
        txt_cart_total.text = getString(R.string.lable_cart_total)
        subTotal = intent.getStringExtra("subTotal")!!
        loyalty_message_description = intent.getStringExtra("loyalty_message_description")!!
        loyalty_message_title = intent.getStringExtra("loyalty_message_title")!!
        loyaltyBalance = intent.getDoubleExtra("loyaltyBalance", 0.00)!!
        payableAmount = intent.getStringExtra("paybleAmount")!!
        mainPayableAmount = intent.getStringExtra("paybleAmount")!!


        addressId = intent.getStringExtra("addresId")
        slotId = intent.getStringExtra("slotId")
        addressType = intent.getStringExtra("addressType")
        address = intent.getStringExtra("address")
        if (loyaltyBalance > 0) {
//            r_loyalty_point.visibility = View.VISIBLE //commented
//            view12.visibility = View.VISIBLE //commented
            view_loyalty_qar.visibility = View.VISIBLE
//          txt_st_loyalty_qar.visibility = View.VISIBLE
//            txt_loyalty_qar.visibility = View.VISIBLE  //commented
            txt_loyalty_qar.visibility = View.GONE
/*
            txt_loyalty_qar.setText(
                "" + Utils.numbersWithoutLocalization(
                    loyaltyBalance
                )
            )
*/

        } else {
            r_loyalty_point.visibility = View.GONE
            view12.visibility = View.GONE
            view_loyalty_qar.visibility = View.GONE
            txt_st_loyalty_qar.visibility = View.GONE
            txt_loyalty_qar.visibility = View.GONE
        }
        if (addressId == null) {
            addressId = "0"
            slotId = "0"
            rel_home.visibility = View.GONE
            r_take_away.visibility = View.VISIBLE
            isDeliveryOpted = false
        } else {
            rel_home.visibility = View.VISIBLE
            r_take_away.visibility = View.GONE
            txt_home.text = addressType
            txt_address.text = address
            isDeliveryOpted = true
        }
//        txt_sub_total.text = subTotal
        txt_sub_total.text = payableAmount
        txt_payableamount.text = payableAmount
        cart_top_total.text = payableAmount

        txt_cart_count.text = sessionManager.totalCart
        img_crd.isChecked = true
        img_crd.setOnClickListener {
            /*img_crd.isChecked=true
            img_deb.isChecked=false*/
            r_cred.performClick()
        }
        img_deb.setOnClickListener {
            /*img_crd.isChecked=false
            img_deb.isChecked=true*/
            r_debit.performClick()
        }


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_left_arrow -> {
                onBackPressed()
            }
            R.id.r_cash -> {
                if (payableAmount.replace(resources.getString(R.string.Qatarcurrency), "")
                        .toDouble() > 0
                ) {
                    img_cash.isChecked = true
                    img_crd.isChecked = false
                    img_pay.isChecked = false
                    img_deb.isChecked = false
                }
            }
            R.id.r_cred -> {
                if (payableAmount.replace(resources.getString(R.string.Qatarcurrency), "")
                        .toDouble() > 0
                ) {
                    img_cash.isChecked = false
                    img_crd.isChecked = true
                    img_pay.isChecked = false
                    img_deb.isChecked = false
                }
            }
            R.id.r_pay_pal -> {
                if (payableAmount.replace(resources.getString(R.string.Qatarcurrency), "")
                        .toDouble() > 0
                ) {
                    img_cash.isChecked = false
                    img_crd.isChecked = false
                    img_pay.isChecked = true
                    img_deb.isChecked = false
                }
            }
            R.id.r_debit -> {
                if (payableAmount.replace(resources.getString(R.string.Qatarcurrency), "")
                        .toDouble() > 0
                ) {
                    img_cash.isChecked = false
                    img_crd.isChecked = false
                    img_pay.isChecked = false
                    img_deb.isChecked = true
                }
            }
            R.id.img_loyalty_point -> {
                if (img_loyalty_point.text.equals(resources.getString(R.string.redeem))) {
                    LoyaltyPointAlertDialog(false)
                } else {
                    LoyaltyPointAlertDialog(true)
                }
            }
            R.id.tv_use_points -> {
                if (loyalty_points!!.equals("")) {
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.Please_valid_points),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                if (loyalty_points.toDouble() < 1) {
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.plz_enter_min_redeem_point),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                var total =
                    mainPayableAmount.replace(resources.getString(R.string.Qatarcurrency), "")
                if (loyalty_points!!.toDouble() > loyaltyBalance) {
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.enough_points),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                if (loyalty_points!!.toDouble() > total.toDouble()) {
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.actual_QAR),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                if (loyalty_points!!.toDouble() > 0) {
                    img_loyalty_point.setBackgroundDrawable(resources.getDrawable(R.drawable.corner_redeem_disable_bg))
                    img_loyalty_point.setTextColor(resources.getColor(R.color.black))
                    img_loyalty_point.text =
                        resources.getString(R.string.Qatarcurrency) + " " + Utils.numbersWithoutLocalization(
                            loyalty_points.toDouble()
                        )
                    loyaltyRedeemed = Utils.numbersWithoutLocalization(loyalty_points.toDouble())
                    var lBalance = loyaltyBalance - loyalty_points.toDouble()
                    txt_loyalty_qar.setText(
                        "" + Utils.numbersWithoutLocalization(
                            lBalance
                        )
                    )
                    var balanceAmount = total.toDouble() - loyalty_points.toDouble()
                    payableAmount =
                        resources.getString(R.string.Qatarcurrency) + " " + Utils.numbersWithoutLocalization(
                            balanceAmount
                        )

                    subTotal =
                        resources.getString(R.string.Qatarcurrency) + " " + Utils.numbersWithoutLocalization(
                            balanceAmount
                        )


//                    txt_sub_total.text = subTotal
                    txt_payableamount.text = payableAmount
                    if (balanceAmount > 0) {
                        strPaymentType = ""
                        img_cash.isEnabled = true
                        img_crd.isEnabled = true
                        img_pay.isEnabled = true
                        img_deb.isEnabled = true

                        r_pay_pal.isEnabled = true
                        r_cash.isEnabled = true
                        r_cred.isEnabled = true
                        r_debit.isEnabled = true
                        if (!img_cash.isChecked && !img_crd.isChecked && !img_pay.isChecked && !img_deb.isChecked) {
                            img_crd.isChecked = true
                        }

                    } else {
                        strPaymentType = "loyalty"
                        img_cash.isChecked = false
                        img_crd.isChecked = false
                        img_pay.isChecked = false
                        img_deb.isChecked = false

                        img_cash.isEnabled = false
                        img_crd.isEnabled = false
                        img_pay.isEnabled = false
                        img_deb.isEnabled = false

                        r_pay_pal.isEnabled = false
                        r_cash.isEnabled = false
                        r_cred.isEnabled = false
                        r_debit.isEnabled = false
                    }
                    loyaltyPointCustomDialog!!.dismiss();
                } else {
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.Please_valid_points),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            R.id.txt_cancel -> {
                loyaltyPointCustomDialog!!.dismiss();
            }
            R.id.txt_pay_now -> {

                if (img_cash.isChecked || img_crd.isChecked || img_pay.isChecked || img_deb.isChecked ||
                    strPaymentType.equals(
                        "loyalty"
                    )
                ) {
                    if (img_cash.isChecked) {
                        strPaymentType = "cash"
                    } else if (img_crd.isChecked) {
                        strPaymentType = "card_cc"
                        cardType = "Credit"
                    } else if (img_deb.isChecked) {
                        strPaymentType = "card_dc"
                        cardType = "Debit"
                    }
                    Utils.showProgressDialog(this, true)
                    mPresenter.doPayment(
                        sessionManager.cartId.toString(),
                        sessionManager.userId.toString(),
                        strPaymentType,
                        isDeliveryOpted,
                        addressId!!.toInt(),
                        slotId!!.toInt(),
                        if (sessionManager.languageselect == "en") "English" else "Arabic",
                        loyaltyRedeemed
                    )

                } else {
                    showMessage(getString(R.string.er_payment))
                }
            }
            R.id.txt_change_address -> {
                val i = Intent(this, AddressListActivity::class.java)
                i.putExtra("diff", "payment")
                startActivityForResult(i, 200)
            }
            R.id.txt_change -> {
                finish()
            }
        }
    }

    fun LoyaltyPointAlertDialog(isEdit: Boolean) {
        val inflater = layoutInflater as LayoutInflater
        val customView = inflater.inflate(R.layout.dialog_loyalty_point, null)
        loyaltyPointCustomDialog = Dialog(this, R.style.MyDialogTheme)
        loyaltyPointCustomDialog!!.setContentView(customView)

        loyaltyPointCustomDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        loyaltyPointCustomDialog!!.show()
        if (isEdit) {
            loyaltyPointCustomDialog!!.et_point.setText("" + loyalty_points)
        } else {
            var total = mainPayableAmount.replace(resources.getString(R.string.Qatarcurrency), "")
            if (total.toDouble() <= loyaltyBalance) {
                loyaltyPointCustomDialog!!.et_point.setText(
                    "" + Utils.numbersWithoutLocalization(
                        total.toDouble()
                    )
                )
            } else {
                loyaltyPointCustomDialog!!.et_point.setText(
                    "" + Utils.numbersWithoutLocalization(
                        loyaltyBalance
                    )
                )
            }
        }
        loyalty_points = loyaltyPointCustomDialog!!.et_point.text.toString()
        loyaltyPointCustomDialog!!.et_point.setSelection(loyaltyPointCustomDialog!!.et_point.text!!.length)
        loyaltyPointCustomDialog!!.et_point.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loyalty_points = ""
                val str: String = loyaltyPointCustomDialog!!.et_point.text.toString()
                if (str.isEmpty()) return
                val str2 = PerfectDecimal(str, 7, 2)
                if (str2 != str) {
                    loyaltyPointCustomDialog!!.et_point.setText(str2)
                    loyaltyPointCustomDialog!!.et_point.setSelection(str2!!.length)
                }
                loyalty_points = loyaltyPointCustomDialog!!.et_point.text.toString()
            }
        })

        loyaltyPointCustomDialog!!.title.setText("" + loyalty_message_title)
        loyaltyPointCustomDialog!!.txt_st.setText("" + loyalty_message_description)
        loyaltyPointCustomDialog!!.tv_use_points.setOnClickListener(this)
        loyaltyPointCustomDialog!!.txt_cancel.setOnClickListener(this)
        loyaltyPointCustomDialog!!.setCanceledOnTouchOutside(false)
        loyaltyPointCustomDialog!!.setCancelable(true)

    }


    fun PerfectDecimal(str: String, MAX_BEFORE_POINT: Int, MAX_DECIMAL: Int): String? {
        var str = str
        if (str[0] == '.') str = "0$str"
        val max = str.length
        var rFinal = ""
        var after = false
        var i = 0
        var up = 0
        var decimal = 0
        var t: Char
        while (i < max) {
            t = str[i]
            if (t != '.' && after == false) {
                up++
                if (up > MAX_BEFORE_POINT) return rFinal
            } else if (t == '.') {
                after = true
            } else {
                decimal++
                if (decimal > MAX_DECIMAL) return rFinal
            }
            rFinal = rFinal + t
            i++
        }
        return rFinal
    }


    override fun showPayment(response: StatusModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {
            if (response.data.pointsRedeemed.equals(response.data.invoiceAmount)) {
                val i = Intent(this, ConfirmationActivity::class.java)
                i.putExtra("orderId", response.data.orderID)
                i.putExtra("invoiceAmount", response.data.invoiceAmount)
                i.putExtra("barcodeurl", response.data.qrCodeImageUrl)
                i.putExtra("message", response.statusMessage)
                i.putExtra("loyaltyPoints", response.data.pointsEarned)
                startActivity(i)
                return
            }

            if (response.data.payment_url.isEmpty()) {
                val i = Intent(this, CashPaymentActivity::class.java)
                i.putExtra("orderId", response.data.orderID)
                i.putExtra("invoiceAmount", response.data.invoiceAmount)
                i.putExtra("barcodeurl", response.data.qrCodeImageUrl)
                i.putExtra("message", response.statusMessage)
                i.putExtra("loyaltyPoints", response.data.pointsEarned)
                startActivity(i)
            } else {
                val i = Intent(this, PaymentWebview::class.java)
                i.putExtra("orderId", response.data.orderID)
                i.putExtra("url", response.data.payment_url)
                i.putExtra("loyaltyPoints", response.data.pointsEarned)
                i.putExtra(
                    "title",
                    if (cardType == "Debit") getString(R.string.payment_dc) else getString(R.string.payment_cc)
                )
                startActivity(i)
            }
        } else if (response.status == 402) {
            Utils.tokenExpire(this)
            Utils.showProgressDialog(this, true)
            mPresenter.doPayment(
                sessionManager.cartId.toString(),
                sessionManager.userId.toString(),
                strPaymentType,
                isDeliveryOpted, addressId!!.toInt(), slotId!!.toInt(),
                if (sessionManager.languageselect == "en") "English" else "Arabic", loyaltyRedeemed
            )

        } else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
        }
    }

    //    Jaideep
    override fun showRedeemPoint(response: RedeemPointModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {
            img_loyalty_point.isEnabled = true
            img_loyalty_point.setBackgroundDrawable(resources.getDrawable(R.drawable.corner_redeem_disable_bg))
            img_loyalty_point.setTextColor(resources.getColor(R.color.black))
            img_loyalty_point.text =
                resources.getString(R.string.Qatarcurrency) + " " + Utils.numbersWithoutLocalization(
                    loyalty_points.toDouble()
                )
            loyaltyRedeemed = Utils.numbersWithoutLocalization(response.data.loyaltyRemeedmed)
            txt_loyalty_qar.setText(
                "" + Utils.numbersWithoutLocalization(
                    response.data.loyaltybalance
                )
            )
            payableAmount =
                resources.getString(R.string.Qatarcurrency) + " " + Utils.numbersWithoutLocalization(
                    response.data.balanceAmount
                )

            subTotal =
                resources.getString(R.string.Qatarcurrency) + " " + Utils.numbersWithoutLocalization(
                    response.data.balanceAmount
                )

//            txt_sub_total.text = subTotal
            txt_payableamount.text = payableAmount
            if (response.data.balanceAmount > 0) {
                img_cash.isEnabled = true
                img_crd.isEnabled = true
                img_pay.isEnabled = true
                img_deb.isEnabled = true

                r_pay_pal.isEnabled = true
                r_cash.isEnabled = true
                r_cred.isEnabled = true
                r_debit.isEnabled = true
            } else {
                strPaymentType = "loyalty"
                img_cash.isChecked = false
                img_crd.isChecked = false
                img_pay.isChecked = false
                img_deb.isChecked = false

                img_cash.isEnabled = false
                img_crd.isEnabled = false
                img_pay.isEnabled = false
                img_deb.isEnabled = false

                r_pay_pal.isEnabled = false
                r_cash.isEnabled = false
                r_cred.isEnabled = false
                r_debit.isEnabled = false
            }
        } else {
            loyalty_points = "0.00"
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // check if the request code is same as what is passed  here it is 200 after delete
        if (requestCode == 200 && data != null) {
            strFirstName = data.getStringExtra("firstname")
            strLastName = data.getStringExtra("lastname")
            strPhone = data.getStringExtra("phone")
            strAddress = data.getStringExtra("address")
            strAreaName = data.getStringExtra("area")
            strCityName = data.getStringExtra("city")
            strAddressType = data.getStringExtra("address_type")
            addressId = data.getStringExtra("addressID")
            if (strAddressType.equals("1")) {
                txt_home.text = getString(R.string.label_home)
            } else if (strAddressType.equals("2")) {
                txt_home.text = getString(R.string.label_office)
            } else if (strAddressType.equals("3")) {
                txt_home.text = getString(R.string.label_other)
            }
            txt_address.text =
                if (strAddress.isNullOrEmpty())
                    sessionManager.name + " " + sessionManager.familyName + "," + strAreaName + "," + strCityName +
                            "\n" + getString(R.string.mobile) + strPhone else sessionManager.name + " " + sessionManager.familyName + "," + strAddress + "," + strAreaName + "," + strCityName +
                        "\n" + getString(R.string.mobile) + strPhone

        }
    }

}