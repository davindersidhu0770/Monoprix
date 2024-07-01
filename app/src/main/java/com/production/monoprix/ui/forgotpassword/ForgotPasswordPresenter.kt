package com.production.monoprix.ui.forgotpassword

import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import rx.functions.Action1

class ForgotPasswordPresenter : BaseMvpPresenterImpl<ForgotPasswordContractor.View>(), ForgotPasswordContractor.Presenter {
    override fun loadForgotPassword(email: String,type: String) {
        ApiManager.doForgotPassword(email,type)
            .subscribe(
                Action1 { mView?.showForgotPassword(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }


}