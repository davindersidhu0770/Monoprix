package com.production.monoprix.ui.splash

import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import rx.functions.Action1


class SplashPresenter : BaseMvpPresenterImpl<SplashContractor.View>(), SplashContractor.Presenter {
    override fun loadToken(key : String) {
        ApiManager.getToken(key)
            .subscribe(
                Action1 { mView?.showToken(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork)}
            )
    }

    override fun loadSalt() {
        ApiManager.getSalt()
            .subscribe(
                Action1 { mView?.showSalt(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork)}
            )
    }


}