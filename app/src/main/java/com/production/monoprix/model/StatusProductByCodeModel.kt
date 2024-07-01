package com.production.monoprix.model

import com.production.monoprix.model.ProductByCodeModel
import com.squareup.moshi.Json

class StatusProductByCodeModel (
    val `data`: ProductByCodeModel?,
    val status: Int,
    @Json(name = "status_message")
        val statusMessage: String,
    @Json(name = "status_message_arabic")
    val statusMsgArabic: String
    )