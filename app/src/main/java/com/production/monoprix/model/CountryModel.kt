package com.production.monoprix.model


import com.squareup.moshi.Json

data class CountryModel(
    @Json(name = "data")
    val `data`: List<Data>,
    @Json(name = "status")
    val status: Int,
    @Json(name = "status_message")
    val statusMessage: String,
    @Json(name = "status_message_arabic")
    val statusMsgArabic:String
)