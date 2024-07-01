package com.production.monoprix.ui.shoplist

import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import rx.functions.Action1


class ShopListPresenter : BaseMvpPresenterImpl<ShopListContractor.View>(), ShopListContractor.Presenter {

    override fun getProductList(products: String, device_id: String, shop_id: String, user_id: String) {
        ApiManager.getProductList(user_id,0)
            .subscribe(
                Action1 { mView?.showProductList(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork)}
            )
    }

}