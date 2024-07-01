package com.production.monoprix.model


import com.squareup.moshi.Json

data class ChangePasswordModel(
//    val `data`: ChangePassData,
    val `data`: Int,
    val status: Int,
    @Json(name = "status_message")
    val statusMessage: String,
    @Json(name="status_message_arabic")
    val statusMsgArabic:String="الإسم"

)

data class ChangePassData(
    @Json(name = "email")
    val email: String,

    @Json(name = "userId")
    val userId: Int,
)