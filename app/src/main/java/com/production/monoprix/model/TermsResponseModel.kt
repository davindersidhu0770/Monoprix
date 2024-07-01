package com.production.monoprix.model


import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class TermsResponseModel(
    val getLoyaltyProgram_T_C_Eng: String,
    val getLoyaltyProgram_T_C_Arabic: String

    )