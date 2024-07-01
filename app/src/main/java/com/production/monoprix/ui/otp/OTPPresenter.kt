package com.production.monoprix.ui.otp

import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import rx.functions.Action1

class OTPPresenter : BaseMvpPresenterImpl<OTPContractor.View>(), OTPContractor.Presenter {
    override fun loadVerifyOtp(
        userId: String,
        otp: String,
        email: String,
        mobile_number: String,
        isAccUpdate: Boolean
    ) {
        ApiManager.loadVerifyOtp(userId.toInt(), otp,email,mobile_number,isAccUpdate)
            .subscribe(
                Action1 { mView?.showVerifyOtp(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }

    override fun loadLogin(email: String, password: String, device_id: String, device_token: String,fcmtoken:String, deviceId: String) {
        ApiManager.loadLogin(email, password, device_id, device_token,fcmtoken,deviceId,false)
            .subscribe(
                Action1 { mView?.showLogin(it) },
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

