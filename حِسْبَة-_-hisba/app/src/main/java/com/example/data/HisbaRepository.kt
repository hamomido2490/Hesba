package com.example.data

import kotlinx.coroutines.flow.Flow

class HisbaRepository(private val dao: HisbaDao) {
    val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()

    fun getComponentsForProduct(productId: Long): Flow<List<ComponentEntity>> = dao.getComponentsForProduct(productId)
    fun getProductById(id: Long): Flow<ProductEntity?> = dao.getProductById(id)

    suspend fun saveProductWithComponents(product: ProductEntity, components: List<ComponentEntity>) {
        dao.insertProductWithComponents(product, components)
    }

    suspend fun deleteProductWithComponents(id: Long) {
        dao.deleteProductWithComponents(id)
    }
}
