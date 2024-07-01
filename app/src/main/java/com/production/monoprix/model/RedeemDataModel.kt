package com.production.monoprix.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

data class RedeemDataModel (
    var loyaltybalance: Double = 0.00,
    var loyaltyRemeedmed: Double = 0.00,
    var loyalty_message_description: String,
    var balanceAmount: Double = 0.00,
    var loyalty_message_title: String

)