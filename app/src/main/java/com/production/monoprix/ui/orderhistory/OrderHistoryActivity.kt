package com.production.monoprix.ui.orderhistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.production.monoprix.MyApplication
import com.production.monoprix.ORDERDETAIL_ACTIVITY_REQUEST_CODE
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.OrderHistoryList
import com.production.monoprix.model.StatusOrderHistoryModel
import com.production.monoprix.mvp.BaseMvpActivity
import com.production.monoprix.ui.orderdetails.OrderDeatailsActivity
import com.production.monoprix.util.RecyclerItemClickListener
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.activity_recyclerview.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.net.SocketException


class OrderHistoryActivity :
    BaseMvpActivity<OrderHistoryContractor.View, OrderHistoryContractor.Presenter>(),
    OrderHistoryContractor.View,
    View.OnClickListener {

    override var mPresenter: OrderHistoryContractor.Presenter = OrderHistoryPresenter()
    lateinit var mLinearLayoutManager: LinearLayoutManager
    lateinit var sessionManager: SessionManager
    lateinit var mAdapter: OrderHistoryAdapter
    var oList: ArrayList<OrderHistoryList> = java.util.ArrayList<OrderHistoryList>()

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        init()
    }

    fun init() {
        sessionManager = SessionManager(this)
        mLinearLayoutManager = LinearLayoutManager(this)
        order_list.layoutManager = mLinearLayoutManager
        //order history list
        Utils.showProgressDialog(this, true)
        mPresenter.getOrderHistoryList(sessionManager.userId.toString().toInt())
        /*onclick listener*/
        img_left_arrow.setOnClickListener(this)
        /*title*/
        txt_title.text = getString(R.string.shoppinghistory)
        order_list.addOnItemTouchListener(
            RecyclerItemClickListener(this, order_list,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val i = Intent(this@OrderHistoryActivity, OrderDeatailsActivity::class.java)
                        i.putExtra("id", oList[position].orderId)
                        i.putExtra("image", oList[position].qrCodeImageUrl)
                        i.putExtra("storeId", oList[position].storeId)
                        i.putExtra("loyaltyPoints", oList[position].loyaltyPoints)
//                        startActivity(i)
                        startActivityForResult(i, ORDERDETAIL_ACTIVITY_REQUEST_CODE)
                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                }
            ))

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ORDERDETAIL_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Utils.showProgressDialog(this, true)
                mPresenter.getOrderHistoryList(sessionManager.userId.toString().toInt())
            }
        }
    }

    override fun showOrderHistoryList(response: StatusOrderHistoryModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {
            oList = response.data as ArrayList<OrderHistoryList>
            if (oList.size > 0) {
                txt_no_order.visibility = View.GONE
                order_list.visibility = View.VISIBLE
                mAdapter = OrderHistoryAdapter(oList, this,sessionManager)
                order_list.adapter = mAdapter
            } else {
                txt_no_order.visibility = View.VISIBLE
                order_list.visibility = View.GONE
            }
        } else if (response.status == 402) {
            Utils.tokenExpire(this)
            Utils.showProgressDialog(this, true)
            mPresenter.getOrderHistoryList(sessionManager.userId.toString().toInt())
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