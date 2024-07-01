package com.production.monoprix.ui.scanproducts

import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import com.production.monoprix.ui.scanproducts.ShopListFragmentContractor
import rx.functions.Action1

class ShopListFragmentPresenter : BaseMvpPresenterImpl<ShopListFragmentContractor.View>(),
    ShopListFragmentContractor.Presenter {

    override fun getStoreDetails(currentlatandlong: String) {
        ApiManager.getStoreLocation(currentlatandlong)

            .subscribe(
                Action1 { mView?.showStoreLocation(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )

    }

    override fun getStoreLocation() {
        ApiManager.getStores()
            .subscribe(
                Action1 { mView?.showStoreLocation(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )

    }


}