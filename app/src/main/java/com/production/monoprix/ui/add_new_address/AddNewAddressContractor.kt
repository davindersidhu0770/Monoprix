package com.production.monoprix.ui.add_new_address

import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.CountryModel
import com.production.monoprix.model.StatusModel
import com.production.monoprix.model.ZoneModel
import com.production.monoprix.mvp.BaseMvpPresenter
import com.production.monoprix.mvp.BaseMvpView

class AddNewAddressContractor {

    interface View : BaseMvpView {
        fun setEditShippingAddress(response: StatusModel?)
        fun setAddNewAddressDetails(response: StatusModel)
        fun showReloadButton(throwable: Throwable, errorBody: ErrorBody?, network: Boolean)
        fun setCityList(response: CountryModel?)
        fun setAreaList(response: CountryModel?)
        fun showCountries(response: CountryModel)
        fun showZones(response: ZoneModel)

    }

    interface Presenter : BaseMvpPresenter<View> {
        fun getEditShippingAddress(
            strAddressID: Int,
            title: String,
            userId: Int,
            areaId: Int,
            addressDesc: String,
            location: String,
            postCode: String?,
            mobile: String,
            landmark: String,
            building: String,
            houseNo: String,
            street: String,
            cityId: Int,
            countryId: Int,
            isDefalut: Boolean,
            zoneId: String?,
            municipality: String?,
            place: String?,
            area: String
        )

        fun addNewAddressDetails(
            title: String,
            userId: Int,
            areaId: Int,
            addressDesc: String,
            location: String?,
            postCode: String?,
            mobile: String,
            landmark: String,
            building: String,
            houseNo: String,
            street: String,
            cityId: Int,
            countryId: Int,
            isDefalut: Boolean,
            zoneId: String?,
            municipality: String?,
            place: String?,
            area: String
        )

        fun getCityList(countryId: Int)
        fun getAreaList(strCityID: Int)
        fun getCountries()
        fun getzones()

    }

}