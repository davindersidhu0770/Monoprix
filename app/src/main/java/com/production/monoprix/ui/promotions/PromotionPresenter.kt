package com.production.monoprix.ui.promotions

import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import rx.functions.Action1

class PromotionPresenter : BaseMvpPresenterImpl<PromotionContractor.View>(), PromotionContractor.Presenter {
    override fun loadPromotions() {
        ApiManager.getPromotions()
            .subscribe(
                Action1 { mView?.showPromotions(it) },
                GeneralErrorHandler(
                    mView, false
                ) {
                        throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork)
                }
            )
    }
    override fun loadBanner() {
        ApiManager.getPromotionBanner()
            .subscribe(
                Action1 {
                    mView?.showBanner(it)
                },

                GeneralErrorHandler(

                    mView, false
                ) {
                        throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork)}
            )
    }
}