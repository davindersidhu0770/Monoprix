package com.production.monoprix.ui.store_location

import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.CountryModel
import com.production.monoprix.model.StatusModel
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView

class StoreContractor {

    interface View : BaseMvpView {

        fun showStoreLocation(response: CountryModel)

        fun showReloadButton(throwable: Throwable, errorBody: ErrorBody?, network: Boolean)

    }

    interface Presenter : BaseMvpPresenter<View> {
        fun getStoreLocation()
    }

}