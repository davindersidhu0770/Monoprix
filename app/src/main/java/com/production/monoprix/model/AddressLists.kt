package com.production.monoprix.model


import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
class AddressLists(
    @Json(name = "addressId")
    val shipping_address_id: String,
    @Json(name = "cityname")
    val city_name: String,
    @Json(name = "cityId")
    val city_id: String,
    @Json(name = "areaname")
    val area_name: String,
    @Json(name = "areaId")
    var area_id: String?,
    @Json(name = "location")
    val address: String?,
    @Json(name = "title")
    val address_type: String,
    @Json(name = "isdefault")
    val is_defalut_shipping_address: Boolean,

    @Json(name = "mobile")
    val mobile_number: String,
    @Json(name = "countryId")
    val country_code: String,

    @Json(name = "postcode")
    val postcode: String?,
    @Json(name = "countryname")
    val countryname: String,
    @Json(name = "addressDesp")
    val addressDesc: String,
    @Json(name = "building")
    val building: String,
    @Json(name = "houseNo")
    val houseNo: String?,
    @Json(name = "street")
    val street: String,
    @Json(name = "landMark")
    val landMark: String?,

    @Json(name = "muncipality")
    val muncipality: String?,
    @Json(name = "place")
    val place: String?,

    @Json(name = "zoneId")
    val zoneId: Int


) : Parcelable

