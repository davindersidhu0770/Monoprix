package com.production.monoprix.ui.orderhistory

import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.StatusOrderHistoryModel
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView

class OrderHistoryContractor {
    interface View : BaseMvpView {
        fun showOrderHistoryList(response: StatusOrderHistoryModel)
        fun showReloadButton(
            throwable: Throwable,
            errorBody: ErrorBody?,
            network: Boolean
        )
    }

    interface Presenter : BaseMvpPresenter<View> {
        fun getOrderHistoryList(userId: Int)

    }
}