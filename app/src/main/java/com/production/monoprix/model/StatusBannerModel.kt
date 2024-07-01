package com.production.monoprix.model


import com.squareup.moshi.Json

data class StatusBannerModel(
//    val `data`: List<String>,
    val `data`: List<BannerModel>,
    val status: Int,
    @Json(name = "status_message")
    val statusMessage: String
)