package com.production.monoprix.ui.store_location

import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import rx.functions.Action1

class StorePresenter : BaseMvpPresenterImpl<StoreContractor.View>(), StoreContractor.Presenter {

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