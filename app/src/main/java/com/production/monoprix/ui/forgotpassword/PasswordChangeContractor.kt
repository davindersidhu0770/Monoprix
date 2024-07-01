package com.production.monoprix.ui.forgotpassword

import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.ChangePasswordModel
import com.production.monoprix.model.StatusRegisterModel
import com.production.monoprix.model.StatusTokenModel
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView

class PasswordChangeContractor {
    interface View : BaseMvpView {
        fun showChangePassword(response: ChangePasswordModel)
        fun showResendOtp(response: StatusRegisterModel)
        fun showReloadButton(
            throwable: Throwable,
            errorBody: ErrorBody?,
            network: Boolean
        )


    }

    interface Presenter : BaseMvpPresenter<View> {
        fun loadChangePassword(userId: Int,otp: String,password: String)
        fun loadResendOtp(mobile_number: Int)
    }
}