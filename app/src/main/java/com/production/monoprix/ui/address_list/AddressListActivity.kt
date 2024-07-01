package com.production.monoprix.ui.address_list

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.AddressLists
import com.production.monoprix.model.AddressStatus
import com.production.monoprix.mvp.BaseMvpActivity
import com.production.monoprix.ui.add_new_address.AddNewAddressActivity
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.activity_address_list.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_store_location.*
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.include_toolbar.txt_title
import java.net.SocketException


class AddressListActivity :
    BaseMvpActivity<AddressListContractor.View, AddressListContractor.Presenter>(),
    AddressListContractor.View, View.OnClickListener, ItemClickListener {
    lateinit var sessionManager: SessionManager
    override var mPresenter: AddressListContractor.Presenter = AddressListPresenter()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var mAddressList: ArrayList<AddressLists>
    lateinit var mAdapter: AddressListAdapter
    var diff: String? = ""
    var addressId: String? = ""
    var aList: List<AddressLists> = ArrayList<AddressLists>()
    lateinit var itemClickListener: ItemClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)
        sessionManager = SessionManager(this)
        init()
    }

    fun init() {
        diff = intent.getStringExtra("diff") ?: ""
        if (intent.getStringExtra("AddressID") != null) {
            addressId = intent.getStringExtra("AddressID")
        }

        txt_title.text = getString(R.string.addresslist)
        /*recyclerView*/
        // Initialize listener
        // Initialize listener
        itemClickListener = this
/*
        itemClickListener = ItemClickListener { s -> // Notify adapter
            recycler_addressLists.post(Runnable { mAdapter.notifyDataSetChanged() })
            // Display toast

        }
*/
        linearLayoutManager = LinearLayoutManager(this)
        recycler_addressLists.layoutManager = linearLayoutManager
        /* if(diff != null) {
             if(diff == "checkout"){
                 mAddressList = intent.getParcelableArrayListExtra("aList")
                 mAdapter = AddressListAdapter(mAddressList, this,diff)
                 recycler_addressLists.adapter = mAdapter
             } else {
                 Utils.showProgressDialog(this, true)
                 mPresenter.getShippingAddressList(sessionManager.userId.toString())
             }
         } else{*/
        /*Api Call*/

        //  }
        Utils.showProgressDialog(this, true)
        mPresenter.getShippingAddressList(sessionManager.userId.toString())

        //Onclick
        img_left_arrow.setOnClickListener(this)
        txt_add_new_address.setOnClickListener(this)

    }


    private fun showAlertMessage() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.app_name))
        builder.setMessage(resources.getString(R.string.please_fill_allthedetails))
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, id ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }

    override fun onSuccessfullyDelAddress(response: AddressStatus?) {
        Utils.pd.dismiss()
        when {
            response!!.status == 200 -> {
//              txt_no_address.visibility = View.GONE
                recycler_addressLists.visibility = View.VISIBLE
                mAddressList = response.data as ArrayList<AddressLists>
                if (mAddressList.size > 0) {
                    mAdapter =
                        AddressListAdapter(mAddressList, this, diff, addressId!!, itemClickListener)
                    recycler_addressLists.adapter = mAdapter
                } else {
                    txt_no_address.visibility = View.VISIBLE
                    recycler_addressLists.visibility = View.GONE
                }
            }
            response.status == 402 -> {
                Utils.tokenExpire(this)
                Utils.showProgressDialog(this, true)
                mPresenter.getShippingAddressList(sessionManager.userId.toString())
            }
            else -> {
                txt_no_address.visibility = View.VISIBLE
                recycler_addressLists.visibility = View.GONE
            }
        }

    }

    override fun setShippingAddressList(response: AddressStatus?) {
        Utils.pd.dismiss()
        when {

            response!!.status == 200 -> {

                Log.d("22Nov:"," setShippingAddressList +++ ")

                txt_no_address.visibility = View.GONE
                recycler_addressLists.visibility = View.VISIBLE
                mAddressList = response.data as ArrayList<AddressLists>
                if (mAddressList.size > 0) {
                    mAdapter = AddressListAdapter(mAddressList, this, diff, addressId!!, itemClickListener)
                    recycler_addressLists.adapter = mAdapter
                } else {
                    txt_no_address.visibility = View.VISIBLE
                    recycler_addressLists.visibility = View.GONE
                }
            }
            response.status == 402 -> {
                Utils.tokenExpire(this)
                Utils.showProgressDialog(this, true)
                mPresenter.getShippingAddressList(sessionManager.userId.toString())
            }
            else -> {
                txt_no_address.visibility = View.VISIBLE
                recycler_addressLists.visibility = View.GONE
            }
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_left_arrow -> {
                // onBackPressed()
                setResult(200)
                finish()
            }

            R.id.txt_add_new_address -> {
                val i = Intent(this@AddressListActivity, AddNewAddressActivity::class.java)
                i.putExtra("pageName", "0")
                i.putExtra("email", sessionManager.email)
                i.putExtra("phone", sessionManager.mobileNumber)
                i.putExtra("diff", diff)
                startActivityForResult(i, 200)


            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 200 && resultCode == 200) {
            Utils.showProgressDialog(this, true)
            mPresenter.getShippingAddressList(sessionManager.userId.toString())

        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()

        setResult(200)
        finish()


    }

    override fun onClick(s: String?) {
        recycler_addressLists.post(Runnable { mAdapter.notifyDataSetChanged() })

    }

    override fun passAddressId(addressId: String?) {

        if (addressId != null) {
            sessionManager.userId?.let { mPresenter.delAddressById(addressId, it) }
        }

    }


}