package com.production.monoprix.ui.orderhistory

import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import rx.functions.Action1

class OrderHistoryPresenter : BaseMvpPresenterImpl<OrderHistoryContractor.View>(), OrderHistoryContractor.Presenter {
    override fun getOrderHistoryList(userId: Int) {
        ApiManager.getOrderHistoryList(userId)
            .subscribe(
                Action1 { mView?.showOrderHistoryList(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }
}