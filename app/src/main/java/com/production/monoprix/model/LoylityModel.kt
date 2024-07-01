package com.production.monoprix.model

import com.squareup.moshi.Json

data class LoylityModel (
    val `data`: StatusResponseModel?,
    val status: Int,
    @Json(name = "status_message")
    val statusMessage: String
)