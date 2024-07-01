package com.production.monoprix.ui.contactus

import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.StatusModel
import com.production.monoprix.model.StatusTokenModel
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView

class ContactUsContractor {
    interface View : BaseMvpView {
        fun showgetContactUs(response: StatusModel)
        fun showSubmitContactUs(response: StatusTokenModel)
        fun showReloadButton(
            throwable: Throwable,
            errorBody: ErrorBody?,
            network: Boolean
        )


    }

    interface Presenter : BaseMvpPresenter<View> {
        fun getContactUs()
        fun doContactUs(name: String,email: String,mobile: String,message: String)
    }
}