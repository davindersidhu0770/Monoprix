package com.production.monoprix.ui.orderdetails

import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.StatusOrderDetaileModel
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView

class OrderDetailsContractor {
    interface View : BaseMvpView {
        fun showOrderDetails(response: StatusOrderDetaileModel)
        fun showReloadButton(
            throwable: Throwable,
            errorBody: ErrorBody?,
            network: Boolean
        )
    }

    interface Presenter : BaseMvpPresenter<View> {
        fun getOrderDetails(id: Int)

    }
}