package com.production.monoprix.ui.splash

import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.StatusModel
import com.production.monoprix.model.StatusTokenModel
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView

class SplashContractor {
    interface View : BaseMvpView {
        fun showToken(response: StatusTokenModel)
        fun showSalt(response: StatusModel)
        fun showReloadButton(
            throwable: Throwable,
            errorBody: ErrorBody?,
            network: Boolean

        )


    }

    interface Presenter : BaseMvpPresenter<View> {
        fun loadToken(key: String)
        fun  loadSalt()

    }
}