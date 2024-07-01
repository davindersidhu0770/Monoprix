package com.production.monoprix.ui.orderdetails

import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import rx.functions.Action1

class OrderDetailsPresenter : BaseMvpPresenterImpl<OrderDetailsContractor.View>(), OrderDetailsContractor.Presenter {
    override fun getOrderDetails(id: Int) {
        ApiManager.getOrderDetails(id)
            .subscribe(
                Action1 { mView?.showOrderDetails(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }
}