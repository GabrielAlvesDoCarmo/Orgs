package com.gdsdevtec.orgs.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gdsdevtec.orgs.data.local.database.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM PRODUCT")
    fun getAllProduct() : Flow<List<ProductEntity>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveProduct(product: ProductEntity)
    @Insert
    suspend fun saveProduct(vararg product: ProductEntity)
    @Delete
    fun deleteProduct(product: ProductEntity)

    @Update
    fun updateProduct(product: ProductEntity)

    @Query("SELECT * FROM PRODUCT WHERE id = :id")
    fun getProductForId(id : Long) : Flow<ProductEntity?>
}