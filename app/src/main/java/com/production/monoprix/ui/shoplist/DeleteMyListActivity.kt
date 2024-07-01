package com.production.monoprix.ui.shoplist


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.production.monoprix.R
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.model.StatusModel
import com.production.monoprix.room.AppDatabase
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.activity_delete_product.view.*
import kotlinx.android.synthetic.main.include_toolbar.view.*


class DeleteMyListActivity :  Fragment() , View.OnClickListener {

    lateinit var sessionManager: SessionManager
    lateinit var db: AppDatabase
    lateinit var mView: View
    lateinit var navigation: BottomNavigationView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.activity_delete_product, container, false)
        init()
        return mView
    }

    fun init(){
        db = AppDatabase(requireActivity())
        sessionManager = SessionManager(requireActivity())
        mView.img_left_arrow.setOnClickListener(this)
        mView.txt_delete.setOnClickListener(this)
        mView.txt_cancel.setOnClickListener(this)
        mView.txt_title.text = getString(R.string.deletemy)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.img_left_arrow -> {
              /*  this.setResult(300)
                this.finish()*/
                navigation = activity!!.findViewById(R.id.navigation) as BottomNavigationView
//                navigation.selectedItemId = R.id.navigation_shlist
                //activity?.onBackPressed()
            }
            R.id.txt_delete ->{
                Utils.showProgressDialog(requireActivity(),true)
                clearCart()
            }
            R.id.txt_cancel -> {
               /* this.setResult(300)
                this.finish()*/
                navigation = activity!!.findViewById(R.id.navigation) as BottomNavigationView
//                navigation.selectedItemId = R.id.navigation_shlist
               // activity?.onBackPressed()
            }
        }
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
            db.todoDao().deleteAll()
           /* this.setResult(300)
            this.finish()*/
          //  activity?.onBackPressed()
            navigation = activity!!.findViewById(R.id.navigation) as BottomNavigationView
//            navigation.selectedItemId = R.id.navigation_shlist
        } else if(rsponse.status == 402) {
            Utils.tokenExpire(requireActivity())
            Utils.showProgressDialog(requireActivity(),true)
            clearCart()
        }else{
            Toast.makeText(requireActivity(), rsponse.statusMessage, Toast.LENGTH_SHORT).show()
        }

    }

    private fun handleError(error: Throwable) {
        Utils.pd.dismiss()
        Toast.makeText(requireActivity(), "Error ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
    }

}