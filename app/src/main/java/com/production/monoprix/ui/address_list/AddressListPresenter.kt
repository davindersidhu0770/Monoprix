package com.production.monoprix.ui.address_list

import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import rx.functions.Action1

class AddressListPresenter : BaseMvpPresenterImpl<AddressListContractor.View>(), AddressListContractor.Presenter {

    //getShippingAddressList
    override fun getShippingAddressList(userId: String) {
        ApiManager.getShippingAddressList(userId)
            .subscribe(
                Action1 { mView?.setShippingAddressList(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )

    }

    override fun delAddressById(id: String, userId: String) {
        ApiManager.delAddress(id,userId)
            .subscribe(
                Action1 { mView?.onSuccessfullyDelAddress(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )

    }

}