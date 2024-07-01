package com.production.monoprix.model


import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class HelpResponseModel(
    val help_Eng: String,
    val help_Arabic: String

    )