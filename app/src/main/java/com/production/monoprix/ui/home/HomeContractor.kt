package com.production.monoprix.ui.home

import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.*
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView
import rx.functions.Action1

class HomeContractor {
    interface View : BaseMvpView {
        fun showBanner(response: StatusBannerModel)
        //    Added by Jaideep on 12-jan-2022.
        fun showPromotionOnline(response: PromotionOnlineModel)
        fun showLoyalty(response: LoylityModel)
        fun setBenefit(response: BenefitModel)
        fun setTerms(response: TermsAndConditionsModel)
        fun showStoreLocation(response: StatusModel?)
        fun showDeviceIDUpdated(response: StatusDiviceIDUpdateModel)
        fun showReloadButton(
            throwable: Throwable,
            errorBody: ErrorBody?,
            network: Boolean
        )
        fun showAccountDetails(response: StatusModel)


    }

    interface Presenter : BaseMvpPresenter<View> {
        fun loadBanner()
        //    Added by Jaideep on 12-jan-2022.
        fun promotionOnline()
        fun getBenefit()
        fun getTermAndConditions(type:String)
        fun getLoyalty(userId: Int)
        fun getStoreDetails(currentlatandlong: String)
        fun DeviceIDUpdated(userid: String,deviceId: String)
        fun getAccountDetails(userId: Int)

    }
}