package com.production.monoprix.ui.checkout

import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import org.json.JSONArray
import rx.functions.Action1

class CheckoutPresenter : BaseMvpPresenterImpl<CheckoutContractor.View>(), CheckoutContractor.Presenter {
    override fun getTimeslot() {
        ApiManager.getTimeSlot()
            .subscribe(
                Action1 { mView?.showTimeSlot(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }

    override fun getaddProduct(jsonArray: JSONArray) {
        ApiManager.addProduct(jsonArray)
            .subscribe(
                Action1 { mView?.showProductList(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }

    override fun getProductList(userId: String,cartId:Int) {
        ApiManager.getProductList(userId,cartId)
            .subscribe(
                Action1 { mView?.showProductList(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }

    override fun getShippingAddressList(userId: String) {
        ApiManager.getShippingAddressList(userId)
            .subscribe(
                Action1 { mView?.setShippingAddressList(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }
}