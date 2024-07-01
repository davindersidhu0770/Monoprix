package com.production.monoprix.ui.barcodescanner

import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.CountryModel
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView
import com.production.monoprix.model.StatusProductByCodeModel

class BarcodeContractor {
    interface View : BaseMvpView {
        fun showProductDetails(response: StatusProductByCodeModel)
        fun showReloadButton(throwable: Throwable, errorBody: ErrorBody?, network: Boolean)


    }

    interface Presenter : BaseMvpPresenter<View> {
        fun getProductDetails(shopId: String,code: String,location: String,userid: String)

    }
}