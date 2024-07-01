package com.production.monoprix.ui.home

import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import com.production.monoprix.util.Utils
import rx.functions.Action1


class HomePresenter : BaseMvpPresenterImpl<HomeContractor.View>(), HomeContractor.Presenter {

    override fun getLoyalty(userId: Int) {
        ApiManager.getLoyality(userId)
            .subscribe(
                Action1 { mView?.showLoyalty(it) },
                GeneralErrorHandler(
                    mView, false
                ) { throwable, errorBody, isNetwork ->
                    mView?.showReloadButton(
                        throwable,
                        errorBody,
                        isNetwork
                    )
                }
            )
    }

    override fun DeviceIDUpdated(
        userid: String,
        deviceId: String
    ) {
        ApiManager.DeviceIDUpdated(userid, deviceId)
            .subscribe(
                Action1 { mView?.showDeviceIDUpdated(it) },
                GeneralErrorHandler(
                    mView, false
                ) { throwable, errorBody, isNetwork ->
                    mView?.showReloadButton(
                        throwable,
                        errorBody,
                        isNetwork
                    )
                }
            )
    }

    override fun getAccountDetails(userId: Int) {
        ApiManager.getAccountDetails(userId)
            .subscribe(
                Action1 { mView?.showAccountDetails(it) },
                GeneralErrorHandler(
                    mView, false
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }

    override fun loadBanner() {
        ApiManager.getBanner()
            .subscribe(
                Action1 {
                    mView?.showBanner(it)
                },

                GeneralErrorHandler(

                    mView, false
                ) { throwable, errorBody, isNetwork ->
                    mView?.showReloadButton(
                        throwable,
                        errorBody,
                        isNetwork
                    )
                }
            )
    }

    //    Added by Jaideep on 12-jan-2022.
    override fun promotionOnline() {
        ApiManager.getPromotionOnline()
            .subscribe(
                Action1 {
                    mView?.showPromotionOnline(it)
                },

                GeneralErrorHandler(

                    mView, false
                ) { throwable, errorBody, isNetwork ->
                    mView?.showReloadButton(
                        throwable,
                        errorBody,
                        isNetwork
                    )
                }
            )
    }

    override fun getBenefit() {
        ApiManager.getBenefits()
            .subscribe(
                Action1 {
                    mView?.setBenefit(it)
                },

                GeneralErrorHandler(

                    mView, false
                ) { throwable, errorBody, isNetwork ->
                    mView?.showReloadButton(
                        throwable,
                        errorBody,
                        isNetwork
                    )
                }
            )

    }

    override fun getTermAndConditions(type: String) {
        ApiManager.getCMSTermsAndConditions(type)
            .subscribe(
                Action1 {
                    mView?.setTerms(it)
                },

                GeneralErrorHandler(

                    mView, false
                ) { throwable, errorBody, isNetwork ->
                    mView?.showReloadButton(
                        throwable,
                        errorBody,
                        isNetwork
                    )
                }
            )

    }

    override fun getStoreDetails(currentlatandlong: String) {
        ApiManager.getStoreLocation(currentlatandlong)

            .subscribe(
                Action1 { mView?.showStoreLocation(it) },
                GeneralErrorHandler(
                    mView, false
                ) { throwable, errorBody, isNetwork ->
                    mView?.showReloadButton(
                        throwable,
                        errorBody,
                        isNetwork
                    )
                }
            )

    }


}