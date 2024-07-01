package com.production.monoprix.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class BenefitResponseModel(
    val benefit_Eng: String,
    val benefit_Arabic: String

    )