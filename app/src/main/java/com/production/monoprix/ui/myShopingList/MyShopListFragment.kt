package com.production.monoprix.ui.myShopingList


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.ShoppingListDataModel
import com.production.monoprix.model.StatusModel
import com.production.monoprix.mvp.BaseMvpFragment
import com.production.monoprix.room.AppDatabase
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.include_home_header.*
import kotlinx.android.synthetic.main.include_loylty.view.*
import kotlinx.android.synthetic.main.include_toolbar.view.*
import java.net.SocketException


class MyShopListFragment :
    BaseMvpFragment<MyShopListContractor.View, MyShopListContractor.Presenter>(),
    MyShopListContractor.View {

    override var mPresenter: MyShopListContractor.Presenter = MyShopListPresenter()
    lateinit var mView: View
    lateinit var sessionManager: SessionManager
    lateinit var navigation: BottomNavigationView
    lateinit var etShopData: AppCompatEditText
    lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_myshoplist, container, false)
        mView.img_left_arrow.setOnClickListener {
            activity!!.onBackPressed()
        }
        mView.txt_title.text = getString(R.string.myshopping)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
        sessionManager = SessionManager(requireActivity())
        db = AppDatabase(requireContext())
        etShopData = mView.findViewById(R.id.etShopping)
        if (db.shoppingDao().getAll().isNotEmpty()) {
            val list = db.shoppingDao().getAll()[0].shoppingData
            etShopData.setText(list)
        }

    }

    fun saveToast() {
        if (!etShopData.text!!.isNullOrEmpty())
            Toast.makeText(
                requireContext(),
                getString(R.string.save_shopping_list),
                Toast.LENGTH_SHORT
            ).show()
    }

    override fun onStop() {
        super.onStop()
        if (db.shoppingDao().getAll().isEmpty()) {
            db.shoppingDao().insertAll(ShoppingListDataModel(1, etShopData.text.toString()))
        } else {
            db.shoppingDao().update(etShopData.text.toString(), 1)
        }
    }

    override fun showProductList(response: StatusModel) {
        Utils.pd.dismiss()

    }


    private fun handleResponse(rsponse: StatusModel) {
        Utils.pd.dismiss()


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

}
