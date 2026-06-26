package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface HisbaDao {
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: Long): Flow<ProductEntity?>

    @Query("SELECT * FROM components WHERE productId = :productId")
    fun getComponentsForProduct(productId: Long): Flow<List<ComponentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComponents(components: List<ComponentEntity>)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Long)

    @Query("DELETE FROM components WHERE productId = :productId")
    suspend fun deleteComponentsByProductId(productId: Long)

    @Transaction
    suspend fun insertProductWithComponents(product: ProductEntity, components: List<ComponentEntity>) {
        val productId = insertProduct(product)
        val componentsWithProductId = components.map { it.copy(productId = productId) }
        insertComponents(componentsWithProductId)
    }

    @Transaction
    suspend fun deleteProductWithComponents(id: Long) {
        deleteProductById(id)
        deleteComponentsByProductId(id)
    }
}
