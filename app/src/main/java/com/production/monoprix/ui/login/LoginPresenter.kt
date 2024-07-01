package com.production.monoprix.ui.login


import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import rx.functions.Action1

class LoginPresenter : BaseMvpPresenterImpl<LoginContractor.View>(), LoginContractor.Presenter {
    override fun loadLogin(
        email: String,
        password: String,
        device_id: String,
        device_token: String,
        fcmtoken: String,
        deviceId: String,
        isFingerLogin: Boolean
    ) {
        ApiManager.loadLogin(
            email,
            password,
            device_id,
            device_token,
            fcmtoken,
            deviceId,
            isFingerLogin
        )
            .subscribe(
                Action1 { mView?.showLogin(it) },
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

    override fun DeviceIDChangeRequestEmail(
        userid: String,
        deviceId: String
    ) {
        ApiManager.DeviceIDChangeRequestEmail(userid, deviceId)
            .subscribe(
                Action1 { mView?.showDeviceIDChangeRequestEmail(it) },
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


    override fun loadSocialLogin(
        Title: String,
        Name: String,
        FamilyName: String,
        Email: String,
        CountryId: Int,
        MobileNumber: String,
        Flag18years: Boolean,
        flgTermsOfService: Boolean,
        socialmediaId: String,
        socialmediatoken: String,
        Islogin: Boolean, social_type: String, fcmtoken: String,
        deviceId: String
    ) {
        ApiManager.doSocialLogin(
            Title,
            Name,
            FamilyName,
            Email,
            CountryId,
            MobileNumber,
            Flag18years,
            flgTermsOfService,
            socialmediaId,
            socialmediatoken,
            Islogin,
            social_type,
            fcmtoken,
            deviceId
        )
            .subscribe(
                Action1 { mView?.showSocialLogin(it) },
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

    override fun delUserById(userid: String) {
        ApiManager.delAccountById(userid)
            .subscribe(
                Action1 { mView?.onSuccessfullyDeletionOfAccount(it) },
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

    override fun getCMSDetails(strCMSid: String) {
        ApiManager.getCMSDetails(strCMSid)
            .subscribe(
                Action1 { mView?.setCMSDetails(it) },
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

    override fun getTermAndConditions(strCMSid: String) {
        ApiManager.getCMSTermsAndConditions(strCMSid)
            .subscribe(
                Action1 { mView?.setTermsAndConditions(it) },
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

    override fun getPrivacy() {
        ApiManager.getPandP()
            .subscribe(
                Action1 { mView?.setPrivacy(it) },
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

    override fun getFaqApi() {
        ApiManager.getFaqLink()
            .subscribe(
                Action1 { mView?.setFaq(it) },
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

    override fun getHelp() {
        ApiManager.getHelp()
            .subscribe(
                Action1 { mView?.setHelp(it) },
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



