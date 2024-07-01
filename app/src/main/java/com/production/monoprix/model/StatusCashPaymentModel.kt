package com.production.monoprix.model

import com.squareup.moshi.Json

class StatusCashPaymentModel (
    val `data`: Boolean,
    val status: Int,
    @Json(name = "status_message")
    val statusMessage: String
    )