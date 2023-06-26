package com.gdsdevtec.orgs.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gdsdevtec.orgs.model.Product

@Dao
interface ProductDao {
    @Query("SELECT * FROM Product")
    fun getAllProducts() : List<Product>

    @Insert
    fun saveProduct(product: Product)

    @Insert
    suspend fun saveProduct(vararg product: Product)

    @Delete
    fun deleteProduct(product: Product)

    @Update
    fun updateProduct(product: Product)

    @Query("SELECT * FROM product WHERE id = :id")
    fun getProductForId(id : Long) : Product
}