package com.production.monoprix.ui.signup

import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import com.production.monoprix.ui.login.LoginContractor
import rx.functions.Action1

class SignUpPresenter : BaseMvpPresenterImpl<SignUpContractor.View>(), SignUpContractor.Presenter {
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
        Islogin: Boolean,social_type: String,
        fcmtoken:String,
        deviceId: String
    ) {
        ApiManager.doSocialLogin(Title,Name,FamilyName,Email,CountryId,MobileNumber,
            Flag18years,flgTermsOfService,socialmediaId,socialmediatoken,Islogin,social_type,
            fcmtoken,deviceId)
            .subscribe(
                Action1 { mView?.showSocialLogin(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }

    override fun getCountries() {
        ApiManager.getCountries()
            .subscribe(
                Action1 { mView?.showCountries(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }

    override fun getTermAndConditions(strCMSid: String) {


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


    override fun loadSignUp(
        title: String,
        name: String,
        familyname :String,
        email:String,
        countryId:Int,
        mobile:String,
        password: String,
        flagyears:Boolean,
        flagTerms:Boolean,
        deviceId:String,
        dob:String,
        country:String,
        area:String

    ) {
        ApiManager.loadSignUp(title,name,familyname, email, countryId, mobile,password,flagyears,flagTerms,deviceId,dob,country,area)
            .subscribe(
                Action1 { mView?.showSignUp(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }
   /* override fun loadSocialLogin(
        login_type: String,
        id: String,
        email: String,
        access_token: String,
        device_id: String,
        mobile_number: String,
        name: String,
        country_code: String,
        loginFlag: String
    ) {
        ApiManager.doSocialLogin(login_type, id, email, access_token, device_id, mobile_number, name, country_code,loginFlag)
            .subscribe(
                Action1 { mView?.showSocialLogin(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }*/

    override fun getCMSDetails(strCMSid: String) {
        ApiManager.getCMSDetails(strCMSid)
            .subscribe(
                Action1 { mView?.setCMSDetails(it) },
                GeneralErrorHandler(
                    mView, true
                ) { throwable, errorBody, isNetwork -> mView?.showReloadButton(throwable, errorBody, isNetwork) }
            )
    }





}