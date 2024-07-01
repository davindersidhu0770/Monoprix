package com.production.monoprix.model


import com.squareup.moshi.Json

data class CartList(
    val cartId: String,
    @Json(name = "cartDetailId")
    val cartItemId: String,
    @Json(name = "quantity")
    var cartQuantity: String,
    val code: String,
    val image: String,
    @Json(name = "Is_sold_out")
    val isSoldOut: Int,
    @Json(name = "r_name")
    val itemName: String,
    @Json(name = "salesPrice")
    val itemOriginalPrice: String,
    @Json(name = "price")
    val itemSellingPrice: String,
    /* @Json(name = "pluBarcode")
     val pluBarcode: Boolean,*/
    val pluBarcode: Boolean,
    val productId: String,
    val barcode: String,
    val promotionFlag:Boolean,
    val clientStoreId: String,
    var timerclose:Boolean

)