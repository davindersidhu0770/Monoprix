package com.production.monoprix.model


import com.squareup.moshi.Json

data class TimeSlot(
    @Json(name = "deliveryTimeId")
    val id: String,
    @Json(name = "timeSlot")
    val slotName: String,
    var isClicked: Boolean = false
)