package com.production.monoprix.ui.barcodescanner

import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import rx.functions.Action1


class BarcodePresenter : BaseMvpPresenterImpl<BarcodeContractor.View>(),
    BarcodeContractor.Presenter {


    override fun getProductDetails(shopId: String, code: String,location: String,userid: String) {
        ApiManager.getProductDetail(shopId,code,location,userid)
            .subscribe(
                Action1 { mView?.showProductDetails(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }



}