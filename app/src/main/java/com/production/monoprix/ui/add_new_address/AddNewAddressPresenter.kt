package com.production.monoprix.ui.add_new_address

import com.production.monoprix.api.GeneralErrorHandler
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.mvp.BaseMvpPresenterImpl
import rx.functions.Action1

class AddNewAddressPresenter : BaseMvpPresenterImpl<AddNewAddressContractor.View>(),
    AddNewAddressContractor.Presenter {


    //GetCity List
    override fun getCityList(countryId: Int) {
        ApiManager.getCityList(countryId)
            .subscribe(
                Action1 { mView?.setCityList(it) },
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

    //Get Area List
    override fun getAreaList(strCityID: Int) {
        ApiManager.getAreaList(strCityID)
            .subscribe(
                Action1 { mView?.setAreaList(it) },
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


    //Edit Address
    override fun getEditShippingAddress(
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
    ) {
        ApiManager.getEditShippingAddress(
            strAddressID,
            title,
            userId,
            areaId,
            addressDesc,
            location,
            postCode!!,
            mobile,
            landmark,
            building,
            houseNo,
            street,
            cityId,
            countryId,
            isDefalut,
            zoneId!!,
            municipality!!,
            place!!,
            area
        )
            .subscribe(
                Action1 {
                    mView?.setEditShippingAddress(it)
                },
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


    //Add Address
    override fun addNewAddressDetails(
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
    ) {
        ApiManager.addNewAddressDetails(
            title,
            userId,
            areaId,
            addressDesc,
            location!!,
            postCode!!,
            mobile,
            landmark,
            building,
            houseNo,
            street,
            cityId,
            countryId,
            isDefalut,
            zoneId!!,
            municipality!!,
            place!!,
            area
        )
            .subscribe(
                Action1 { mView?.setAddNewAddressDetails(it) },
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

    /*country*/
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

    override fun getzones() {
        ApiManager.getZones()
            .subscribe(
                Action1 { mView?.showZones(it) },
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