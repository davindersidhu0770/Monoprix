package com.production.monoprix.model


import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class PandPResponseModel(
    val privacy_Policy_Eng: String,
//  val privacy_Policy_Arabic: String
    val terms_Conditions_Arabic: String

    )