package com.production.monoprix.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shoppingLocalTable")
data class ShoppingListDataModel(
    @PrimaryKey(autoGenerate = true)
    val shoppingId: Int,
    @ColumnInfo(name = "shoppingData") val shoppingData: String
)
