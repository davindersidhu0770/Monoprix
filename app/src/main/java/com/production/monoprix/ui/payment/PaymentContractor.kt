package com.production.monoprix.ui.payment

import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.RedeemPointModel
import com.production.monoprix.model.StatusModel
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView

class PaymentContractor {
    interface View : BaseMvpView {
        fun showPayment(response: StatusModel)
        fun showRedeemPoint(response: RedeemPointModel)
        fun showReloadButton(
            throwable: Throwable,
            errorBody: ErrorBody?,
            network: Boolean
        )


    }

    interface Presenter : BaseMvpPresenter<View> {
        fun doPayment(
            cartId: String,
            userId: String,
            paymentmode: String,
            isdeliveryopted: Boolean,
            addressId: Int,
            deliveryslotId: Int,
            language: String,
            loyaltyRedeemed: String
        )
        fun RedeemLoyalty(userId: String, total: String,loyaltybalance: String, amountToRedeem: String)

    }
}