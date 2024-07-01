package com.production.monoprix.ui.signup

import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.*
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView

class SignUpContractor {
    interface View : BaseMvpView {
        fun showSignUp(response: StatusModel)
        fun showSocialLogin(response: StatusModel)
        fun showDeviceIDChangeRequestEmail(response: DeviceUpdateStatusModel)
        fun setCMSDetails(response: TermsAndConditionsModel)
//        fun setCMSDetails(response: StatusModel)
        fun setTermsAndConditions(response: TermsAndConditionsModel)
        fun showReloadButton(
            throwable: Throwable,
            errorBody: ErrorBody?,
            network: Boolean
        )
        fun showCountries(response :CountryModel)
    }

    interface Presenter : BaseMvpPresenter<View> {
        fun loadSignUp(title: String,name: String,familyname :String,email:String,countryId:Int,mobile:String,
                       password: String,flagyears:Boolean,flagTerms:Boolean,deviceId:String,
                       dob:String,country:String,area:String)
        fun DeviceIDChangeRequestEmail(userid: String,deviceId: String)
        fun loadSocialLogin(Title: String,
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
                            deviceId: String)
        fun getCMSDetails(strCMSid: String)
        fun getCountries()
        fun getTermAndConditions(strCMSid: String)

    }

}