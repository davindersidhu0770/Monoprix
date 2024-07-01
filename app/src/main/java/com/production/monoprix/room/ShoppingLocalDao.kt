package com.production.monoprix.room

import androidx.room.*
import com.production.monoprix.model.ProductByCodeModel
import com.production.monoprix.model.ShoppingListDataModel

@Dao
interface ShoppingLocalDao {
    @Query("SELECT * FROM shoppingLocalTable")
    fun getAll(): List<ShoppingListDataModel>

    @Insert
    fun insertAll(vararg todo: ShoppingListDataModel)
/*

    @Query("DELETE FROM products")
    fun deleteAll()
*/

    @Query("UPDATE shoppingLocalTable SET shoppingData=:shoppingData WHERE shoppingId = :id")
    fun update(shoppingData: String, id: Int)
/*
    @Query("DELETE FROM products WHERE productId = :id ")
    fun deleteProduct(id: Int)*/



}