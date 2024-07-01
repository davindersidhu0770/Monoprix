package com.production.monoprix.ui.account

import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import rx.functions.Action1

class DelAccountPresenter : BaseMvpPresenterImpl<MyActivityContractor.View>(),
    MyActivityContractor.Presenter {

    override fun getAccountDetails(userId: Int) {
        ApiManager.getAccountDetails(userId)
            .subscribe(
                Action1 { mView?.showAccountDetails(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork ->
                    mView?.showReloadButton(
                        throwable,
                        errorBody,
                        isNetwork
                    )
                }
            )
    }

    override fun doUpdateAccount(
        userId: Int,
        title: String,
        name: String,
        familyName: String,
        email: String,
        countryId: Int,
        country: String,
        mobile_number: String,
        password: String,
        loyaltyno: String,
        area: String,
        dob: String
    ) {
        ApiManager.doUpdateAccount(
            userId,
            title,
            name,
            familyName,
            email,
            countryId,
            country,
            mobile_number,
            password,
            loyaltyno,
            area,
            dob
        )
            .subscribe(
                Action1 { mView?.showUpdateAccount(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork ->
                    mView?.showReloadButton(
                        throwable,
                        errorBody,
                        isNetwork
                    )
                }
            )
    }

    override fun getCountries() {
        ApiManager.getCountries()
            .subscribe(
                Action1 { mView?.showCountries(it) },
                GeneralErrorHandler(
                    mView, true
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