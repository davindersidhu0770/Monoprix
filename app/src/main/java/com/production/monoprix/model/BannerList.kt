package com.production.monoprix.model


import com.squareup.moshi.Json

data class BannerList(
    @Json(name = "banner_id")
    val bannerId: String,
    @Json(name = "banner_title")
    val bannerTitle: String,
    val image: String,
    @Json(name = "redirect_url")
    val redirectUrl: String
)