package com.production.monoprix.ui.scanproducts

import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.CountryModel
import com.production.monoprix.model.StatusModel
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView

class ShopListFragmentContractor {
    interface View : BaseMvpView {
        fun showStoreLocation(response: StatusModel?)
        fun showReloadButton(throwable: Throwable, errorBody: ErrorBody?, network: Boolean)
        fun showStoreLocation(response: CountryModel)

    }

    interface Presenter : BaseMvpPresenter<View> {

        fun getStoreDetails(currentlatandlong: String)
        fun getStoreLocation()

    }


}