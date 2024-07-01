package com.production.monoprix.ui.shoplist

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.model.CartList
import com.production.monoprix.model.StatusModel
import com.production.monoprix.mvp.BaseMvpFragment
import com.production.monoprix.ui.barcodescanner.BarcodeScannerActivity
import com.production.monoprix.ui.checkout.CheckoutActivity
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.activity_empty_layout.view.*
import kotlinx.android.synthetic.main.fragment_shoplist.*
import kotlinx.android.synthetic.main.fragment_shoplist.view.*
import kotlinx.android.synthetic.main.include_toolbar.view.*
import java.net.SocketException


class ShopListFragment : BaseMvpFragment<ShopListContractor.View, ShopListContractor.Presenter>(),
    ShopListContractor.View {

    override var mPresenter: ShopListContractor.Presenter = ShopListPresenter()
    lateinit var mView: View
    lateinit var sessionManager: SessionManager
    lateinit var mLinearLayoutManager: LinearLayoutManager
    lateinit var mAdapter: ShopListAdapter
    var cList: ArrayList<CartList> = java.util.ArrayList<CartList>()
    lateinit var navigation: BottomNavigationView
    lateinit var storeId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_shoplist, container, false)
        init()
        return mView
    }

    fun init() {
        sessionManager = SessionManager(requireActivity())
        mLinearLayoutManager = LinearLayoutManager(requireActivity())
        mView.product_list.layoutManager = mLinearLayoutManager
        //product list
        //  apiCall()
        mView.txt_add_products.setOnClickListener {
            val include = activity!!.findViewById(R.id.include) as View
            navigation = activity!!.findViewById(R.id.navigation) as BottomNavigationView
            include.visibility = View.GONE
//            navigation.selectedItemId = R.id.navigation_scan
        }
        mView.txt_checkout.setOnClickListener {
            val i = Intent(requireActivity(), CheckoutActivity::class.java)
            startActivity(i)
        }
    }

    fun apiCall() {
        Utils.showProgressDialog(requireActivity(), true)
        mPresenter.getProductList(
            "[]",
            sessionManager.deviceId.toString(),
            sessionManager.shopId.toString(),
            sessionManager.userId.toString()
        )

    }

    override fun showProductList(response: StatusModel) {
        Utils.pd.dismiss()
        if (response.status == 200) {
            cList = response.data.cartitems as ArrayList<CartList>
            if (cList.size > 0) {
                storeId = response.data.cartitems[0].clientStoreId
                sessionManager.cartId = response.data.cartitems[0].cartId
                if (sessionManager.shopId!!.isNotEmpty()) {
                    if (sessionManager.shopId != storeId) {
                        alert()
                    } else {
                        nested.visibility = View.VISIBLE
                        include_empty_cart.visibility = View.GONE
                        mAdapter = ShopListAdapter(cList, requireActivity(), this)
                        mView.product_list.adapter = mAdapter
                        sessionManager.totalCart = cList.size.toString()
                    }
                } else {
                    nested.visibility = View.VISIBLE
                    include_empty_cart.visibility = View.GONE
                    mAdapter = ShopListAdapter(cList, requireActivity(), this)
                    mView.product_list.adapter = mAdapter
                    sessionManager.totalCart = cList.size.toString()
                }
            } else {
                nested.visibility = View.GONE
                include_empty_cart.visibility = View.VISIBLE
                sessionManager.totalCart = "0"
            }
        } else if (response.status == 402) {
            Utils.tokenExpire(requireActivity())
            apiCall()
        } else {
            nested.visibility = View.GONE
            include_empty_cart.visibility = View.VISIBLE
        }
    }

    fun alert() {
        val dialogBuilder = AlertDialog.Builder(requireActivity())
        // set message of alert dialog
        dialogBuilder.setMessage(getString(R.string.er_barcode))
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(
                getString(R.string.ok),
                DialogInterface.OnClickListener { dialog, id ->
                    clearCart()

                })
            // negative button text and action
            .setNegativeButton(
                getString(R.string.cancel),
                DialogInterface.OnClickListener { dialog, id ->
                    nested.visibility = View.GONE
                    include_empty_cart.visibility = View.VISIBLE
                    sessionManager.totalCart = "0"
                    dialog.cancel()
                })
        // create dialog box
        val alert = dialogBuilder.create()
        // show alert dialog
        alert.show()
    }

    private fun clearCart() {
        ApiManager.doClearCart(sessionManager.cartId.toString())
            .subscribe(
                this::handleResponse, this::handleError
            )
    }

    private fun handleResponse(rsponse: StatusModel) {
        Utils.pd.dismiss()
        if (rsponse.status == 200) {
            Toast.makeText(requireActivity(), rsponse.statusMessage, Toast.LENGTH_SHORT).show()
            apiCall()
        } else if (rsponse.status == 402) {
            Utils.tokenExpire(requireActivity())
            Utils.showProgressDialog(requireActivity(), true)
            clearCart()
        } else {
            Toast.makeText(requireActivity(), rsponse.statusMessage, Toast.LENGTH_SHORT).show()
        }

    }

    private fun handleError(error: Throwable) {
        Utils.pd.dismiss()
        Toast.makeText(requireActivity(), "Error ${error.localizedMessage}", Toast.LENGTH_SHORT)
            .show()
    }

    override fun showReloadButton(
        throwable: Throwable,
        errorBody: ErrorBody?,
        network: Boolean
    ) {
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

    override fun onStart() {
        super.onStart()

        apiCall()

    }
}
