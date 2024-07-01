package com.production.monoprix.room

import androidx.room.*
import com.production.monoprix.model.ProductByCodeModel

@Dao
interface ProductsDao {
    @Query("SELECT * FROM products")
    fun getAll(): List<ProductByCodeModel>

    @Insert
    fun insertAll(vararg todo: ProductByCodeModel)

    @Query("DELETE FROM products")
    fun deleteAll()

    @Query("UPDATE products SET quantity=:price WHERE pluCode = :id")
    fun update(price: Int, id: String)

    @Query("UPDATE products SET cartItemId=:cartItemId WHERE pluCode = :id")
    fun updateCartItemId(cartItemId: Int, id: String)

    @Query("DELETE FROM products WHERE cartItemId = :id ")
    fun deleteProductByCartItemId(id: String)

    @Query("DELETE FROM products WHERE pluCode = :id ")
    fun deleteProduct(id: String)




}