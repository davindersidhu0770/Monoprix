package com.production.monoprix.ui.promotions

import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.CountryModel
import com.production.monoprix.model.StatusBannerModel
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView

class PromotionContractor {
    interface View : BaseMvpView {
        fun showBanner(response: StatusBannerModel)
        fun showPromotions(response: CountryModel)
        fun showReloadButton(
            throwable: Throwable,
            errorBody: ErrorBody?,
            network: Boolean
        )


    }

    interface Presenter : BaseMvpPresenter<View> {
        fun loadBanner()
        fun loadPromotions()
    }
}