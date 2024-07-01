package com.production.monoprix.model


import com.squareup.moshi.Json

data class StatusTimeSlotModel(
    val `data`: List<TimeSlot>,
    val status: Int,
    @Json(name = "status_message")
    val statusMessage: String
)