package com.production.monoprix.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
@Parcelize
data class BannerModel (
    val id: Int,
    val image: String,
    val link: String
): Parcelable