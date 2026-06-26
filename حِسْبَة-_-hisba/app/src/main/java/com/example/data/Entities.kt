package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val currency: String,
    val generalWaste: Float,
    val profitMode: String,
    val profitInput: Float,
    val baseCostTotal: Float,
    val totalWaste: Float,
    val totalCost: Float,
    val profit: Float,
    val sellPrice: Float,
    val date: String
)

@Entity(tableName = "components")
data class ComponentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val name: String,
    val qty: Float,
    val unit: String,
    val price: Float,
    val waste: Float,
    val baseCost: Float,
    val costWithWaste: Float
)
