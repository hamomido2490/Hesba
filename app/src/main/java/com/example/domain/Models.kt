package com.example.domain

import com.example.data.ComponentEntity

data class CalculatorState(
    val productName: String = "",
    val currency: String = "ج.م",
    val components: List<ComponentItem> = emptyList(),
    val generalWastePercent: String = "",
    val profitMode: ProfitMode = ProfitMode.PERCENT,
    val profitInput: String = "",
)

enum class ProfitMode(val label: String) {
    PERCENT("نسبة مئوية %"),
    FIXED("مبلغ ثابت")
}

data class ComponentItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "",
    val qty: String = "",
    val unit: String = "جرام",
    val price: String = "",
    val wastePercent: String = "",
) {
    fun toEntity(productId: Long): ComponentEntity {
        val q = qty.toFloatOrNull() ?: 0f
        val p = price.toFloatOrNull() ?: 0f
        val w = wastePercent.toFloatOrNull() ?: 0f
        val (base, withWaste) = UnitSystem.calculateComponentCost(q, unit, p, w)
        return ComponentEntity(
            productId = productId,
            name = name,
            qty = q,
            unit = unit,
            price = p,
            waste = w,
            baseCost = base,
            costWithWaste = withWaste
        )
    }
}

data class CalculatorResults(
    val baseCostTotal: Float = 0f,
    val totalWaste: Float = 0f,
    val totalCost: Float = 0f,
    val profit: Float = 0f,
    val sellPrice: Float = 0f
)
