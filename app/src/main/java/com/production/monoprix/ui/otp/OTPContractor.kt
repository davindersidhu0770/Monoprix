package com.production.monoprix.ui.otp

import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.StatusModel
import com.production.monoprix.model.StatusRegisterModel
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView

class OTPContractor {
    interface View : BaseMvpView {
        fun showVerifyOtp(response: StatusRegisterModel)
        fun showLogin(response: StatusModel)
        fun showResendOtp(response: StatusRegisterModel)
        fun showReloadButton(
            throwable: Throwable,
            errorBody: ErrorBody?,
            network: Boolean
        )
    }

    interface Presenter : BaseMvpPresenter<View> {
        fun loadVerifyOtp(userId: String, otp: String, email: String, mobile_number: String,isAccUpdate: Boolean)
        fun loadResendOtp(mobile_number: Int)
        fun loadLogin(email: String,password: String,device_id: String,device_token: String,fcmtoken:String,
                      deviceID: String)
    }
}