package com.production.monoprix.model


import com.squareup.moshi.Json

data class StatusTokenModel(
    val `data`: String,
    val status: Int,
    @Json(name = "status_message")
    val statusMessage: String,
    @Json(name="status_message_arabic")
    val statusMsgArabic:String="الإسم"

)