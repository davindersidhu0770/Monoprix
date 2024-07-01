package com.production.monoprix.ui.forgotpassword

import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.StatusModel
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView

class ForgotPasswordContractor {
    interface View : BaseMvpView {
        fun showForgotPassword(response: StatusModel)
        fun showReloadButton(
            throwable: Throwable,
            errorBody: ErrorBody?,
            network: Boolean
        )


    }

    interface Presenter : BaseMvpPresenter<View> {
        fun loadForgotPassword(email: String,type: String)
    }
}