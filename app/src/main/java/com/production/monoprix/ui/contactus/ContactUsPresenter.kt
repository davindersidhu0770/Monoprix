package com.production.monoprix.ui.contactus

import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import rx.functions.Action1

class ContactUsPresenter : BaseMvpPresenterImpl<ContactUsContractor.View>(), ContactUsContractor.Presenter {

    override fun doContactUs(name: String, email: String, mobile: String, message: String) {
        ApiManager.doContactUs(name,email,mobile,message)
            .subscribe(
                Action1 { mView?.showSubmitContactUs(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }

    override fun getContactUs() {
        ApiManager.getContactUs()
            .subscribe(
                Action1 { mView?.showgetContactUs(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }

}