package com.production.monoprix.model


import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class FaqResponseModel(
    val getLoyaltyFAQ_Eng: String,
    val getLoyaltyFAQ_Arabic: String

    )