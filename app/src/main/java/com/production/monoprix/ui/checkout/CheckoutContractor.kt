package com.production.monoprix.ui.checkout

import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.AddressStatus
import com.production.monoprix.model.StatusModel
import com.production.monoprix.model.StatusTimeSlotModel
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView
import org.json.JSONArray

class CheckoutContractor {
    interface View : BaseMvpView {
        fun showProductList(response: StatusModel)
        fun showTimeSlot(response: StatusTimeSlotModel)
        fun setShippingAddressList(response: AddressStatus?)
        fun showReloadButton(
            throwable: Throwable,
            errorBody: ErrorBody?,
            network: Boolean

        )
    }

    interface Presenter : BaseMvpPresenter<View> {
      //  fun getProductList(userId: String,cartId:Int)
      fun getProductList(userId: String,cartId:Int)
        fun getaddProduct(jsonArray: JSONArray)
        fun getTimeslot()
        fun getShippingAddressList(userId: String)
    }
}