package com.production.monoprix.ui.account


import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.*
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView

class MyActivityContractor {
    interface View : BaseMvpView {
        fun showCountries(response : CountryModel)
        fun showUpdateAccount(response: MyAccountModel)
        fun showAccountDetails(response: StatusModel)
        fun showReloadButton(
            throwable: Throwable,
            errorBody: ErrorBody?,
            network: Boolean
        )
    }

    interface Presenter : BaseMvpPresenter<View> {
        fun getCountries()
        fun doUpdateAccount(userId: Int,title: String,name: String,familyName:String,
                            email: String,countryId: Int,country: String,
                            mobile_number: String,password: String,
                            loyaltyno:String,area:String,dob:String)
        fun getAccountDetails(userId: Int)
    }
}