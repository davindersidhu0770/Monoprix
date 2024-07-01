package com.production.monoprix.model


import com.squareup.moshi.Json

data class Data(
    /*country*/
    @Json(name = "code")
    val code: String,
    @Json(name = "countryId")
    val countryId: String,
    @Json(name = "name")
    val name: String,

    @Json(name = "clientStoreId")
    val clientStoreId: Int,

    /*city*/
    @Json(name = "cityId")
    val cityid: String,

    /*Area*/
    @Json(name = "areaId")
    val areaId: String,

    /*get stores*/
    @Json(name = "storeid")
    val storeid: String,
    @Json(name = "location")
    val location: String,
    val address: String,
    val address_Arabic: String,
    val name_Arabic: String,
    val phoneno: String,
    val storeTimings: String,
    val storeArabicTimings: String,
    val logo_Url: String,

    /*get promotions*/
    val promotionId: String,
    val productName: String,
    val productDesc: String,
    val price: String,
    val email: String


)