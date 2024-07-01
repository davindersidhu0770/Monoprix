package com.production.monoprix.ui.shoplist

import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.StatusModel
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView

class ShopListContractor {
    interface View : BaseMvpView {
        fun showProductList(response: StatusModel)
        fun showReloadButton(
            throwable: Throwable,
            errorBody: ErrorBody?,
            network: Boolean

        )


    }

    interface Presenter : BaseMvpPresenter<View> {
        fun getProductList(products: String, device_id: String, shop_id: String, user_id: String)
    }
}