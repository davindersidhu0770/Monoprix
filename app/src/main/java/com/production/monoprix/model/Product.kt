package com.production.monoprix.model




data class Product(
    val price: Double,
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val salesPrice: Double,
    val promotionFlag:Boolean,
    val pluBarcode:Boolean
)