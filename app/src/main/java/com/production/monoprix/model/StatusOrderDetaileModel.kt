package com.production.monoprix.model


import com.squareup.moshi.Json

data class StatusOrderDetaileModel(
    val `data`: OrderDetailModel,
    val status: Int,
    @Json(name = "status_message")
    val statusMessage: String,
    @Json(name="status_message_arabic")
    val statusMsgArabic:String
)