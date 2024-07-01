package com.production.monoprix.ui.changepassword

import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.ChangePasswordModel
import com.production.monoprix.model.StatusTokenModel
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView

class ChangePasswordContractor {
    interface View : BaseMvpView {
        fun showChangePassword(response: ChangePasswordModel)
        fun showReloadButton(
            throwable: Throwable,
            errorBody: ErrorBody?,
            network: Boolean
        )


    }

    interface Presenter : BaseMvpPresenter<View> {
        fun loadChangePassword(userId: Int,newpass: String,newpassword: String,platform: String, email : String)
    }
}