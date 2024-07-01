package com.production.monoprix.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class AddressStatus(
    val status: Int,
    val status_message: String
    ,val data: List<AddressLists>
) : Parcelable
