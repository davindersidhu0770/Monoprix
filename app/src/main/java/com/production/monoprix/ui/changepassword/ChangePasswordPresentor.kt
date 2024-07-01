package com.production.monoprix.ui.changepassword

import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import rx.functions.Action1


class ChangePasswordPresentor : BaseMvpPresenterImpl<ChangePasswordContractor.View>(), ChangePasswordContractor.Presenter {


    override fun loadChangePassword(userId: Int, oldpassword: String, newpassword: String,platform:String, email:String) {
        ApiManager.doChangePassword(userId,oldpassword,newpassword,platform,email)
            .subscribe(
                Action1 { mView?.showChangePassword(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }

}
