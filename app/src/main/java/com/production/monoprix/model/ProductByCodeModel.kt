package com.production.monoprix.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "products")
data class ProductByCodeModel (
    /*get product by code*/
    @ColumnInfo(name = "quantity") var quantity: Int = 1,
    @ColumnInfo(name = "cartItemId") var cartItemId: Int = 0,
    @ColumnInfo(name = "productTotal") var productTotal: Double = 0.0,
    @PrimaryKey (autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "productId") var productId: Int = 1,
    @ColumnInfo(name = "clientStoreId") val storeId: String,
    @ColumnInfo(name = "pluCode") val pluCode: String,
    @ColumnInfo(name = "r_name")val r_name: String,
    @ColumnInfo(name = "salesPrice")val salesPrice: String,
    @ColumnInfo(name = "manual_price")val manual_price: String,
    @ColumnInfo(name = "offer")val offer: String,
    @ColumnInfo(name = "pluBarcode")val pluBarcode: Boolean
)