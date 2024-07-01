package com.production.monoprix.ui.address_list

import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.AddressStatus
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView

class AddressListContractor {

    interface View : BaseMvpView {
        fun onSuccessfullyDelAddress(response: AddressStatus?)
        fun setShippingAddressList(response: AddressStatus?)
        fun showReloadButton(throwable: Throwable, errorBody: ErrorBody?, network: Boolean)

    }

    interface Presenter : BaseMvpPresenter<View> {
        fun getShippingAddressList(userId: String)
        fun delAddressById(id: String, userId: String)

    }
        
}