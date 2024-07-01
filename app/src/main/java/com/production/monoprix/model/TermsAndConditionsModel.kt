package com.production.monoprix.model


import com.squareup.moshi.Json

data class TermsAndConditionsModel(
    val `data`: TermsResponseModel,
    val status: Int,
    @Json(name = "status_message")
    val statusMessage: String,
    @Json(name = "status_message_arabic")
    val statusMsgArabic: String
)