package com.production.monoprix.ui.forgotpassword

import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import rx.functions.Action1

class PasswordChangePresenter : BaseMvpPresenterImpl<PasswordChangeContractor.View>(), PasswordChangeContractor.Presenter {
    override fun loadChangePassword(userId: Int,otp: String,password: String) {
        ApiManager.doRsetPassword(userId,otp,password)
            .subscribe(
                Action1 { mView?.showChangePassword(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }

    override fun loadResendOtp(mobile_number: Int) {
        ApiManager.doResendOtp(mobile_number)
            .subscribe(
                Action1 { mView?.showResendOtp(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }


}