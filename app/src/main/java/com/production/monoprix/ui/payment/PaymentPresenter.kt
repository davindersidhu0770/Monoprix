package com.production.monoprix.ui.payment

import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import rx.functions.Action1


class PaymentPresenter : BaseMvpPresenterImpl<PaymentContractor.View>(), PaymentContractor.Presenter {
    override fun doPayment(
        cartId: String,
        userId: String,
        paymentmode: String,
        isdeliveryopted: Boolean,
        addressId: Int,
        deliveryslotId: Int,
        language: String,
        loyaltyRedeemed: String
    ) {
        ApiManager.doPayment(cartId,userId,paymentmode,isdeliveryopted,addressId,deliveryslotId,language,loyaltyRedeemed)
            .subscribe(
                Action1 { mView?.showPayment(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }


 override fun RedeemLoyalty( userId: String, total: String,loyaltybalance: String, amountToRedeem: String) {
        ApiManager.RedeemLoyalty( userId, total,loyaltybalance, amountToRedeem)
            .subscribe(
                Action1 { mView?.showRedeemPoint(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }

}