package com.production.monoprix.ui.login

import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.*
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView


class LoginContractor {
    interface View : BaseMvpView {
        fun showLogin(response: StatusModel)
        fun showDeviceIDChangeRequestEmail(response: DeviceUpdateStatusModel)
        fun showSocialLogin(response: StatusModel)
        fun setCMSDetails(response: TermsAndConditionsModel)
        fun setTermsAndConditions(response: TermsAndConditionsModel)
        fun setPrivacy(response: PrivacyPolicyModel)
        fun setFaq(response: FaqModel)
        fun setHelp(response: HelpModel)
        fun showReloadButton(
            throwable: Throwable,
            errorBody: ErrorBody?,
            network: Boolean
        )

        fun showCountries(response: CountryModel)
        fun onSuccessfullyDeletionOfAccount(response: DelUserModel)
    }

    interface Presenter : BaseMvpPresenter<View> {
        fun loadLogin(
            email: String, password: String, device_id: String,
            device_token: String, fcmToken: String, deviceId: String, isFingerLogin: Boolean
        )

        fun DeviceIDChangeRequestEmail(userid: String, deviceId: String)
        fun loadSocialLogin(
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
            Islogin: Boolean, social_type: String,
            fcmtoken: String,
            deviceId: String
        )

        fun getCMSDetails(strCMSid: String)
        fun getTermAndConditions(strCMSid: String)
        fun getPrivacy()
        fun getFaqApi()
        fun getHelp()
        fun getCountries()
        fun delUserById(userid: String)
    }

}