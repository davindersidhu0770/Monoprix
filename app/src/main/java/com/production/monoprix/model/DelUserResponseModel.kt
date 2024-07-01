package com.production.monoprix.model


import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class DelUserResponseModel(

    @Json(name = "name")
    val userName: String,
    @Json(name = "ispasswordresetdatetime")
    val ispasswordresetdatetime: String



)