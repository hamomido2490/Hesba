package com.example.domain

object UnitSystem {
    data class UnitFactor(val major: String, val factor: Float)

    val units = mapOf(
        "جرام" to UnitFactor("كيلو", 0.001f),
        "كيلو" to UnitFactor("كيلو", 1f),
        "ونصة" to UnitFactor("كيلو", 0.0283495f),
        "مل" to UnitFactor("لتر", 0.001f),
        "لتر" to UnitFactor("لتر", 1f),
        "حبة" to UnitFactor("قطعة", 1f),
        "قطعة" to UnitFactor("قطعة", 1f),
        "علبة" to UnitFactor("قطعة", 1f),
        "كوب" to UnitFactor("قطعة", 1f),
        "باكت" to UnitFactor("قطعة", 1f)
    )

    fun calculateComponentCost(qty: Float, unit: String, price: Float, wastePercent: Float): Pair<Float, Float> {
        val factor = units[unit]?.factor ?: 1f
        val convertedQty = qty * factor
        val baseCost = convertedQty * price
        val costWithWaste = baseCost * (1 + wastePercent / 100f)
        return Pair(baseCost, costWithWaste)
    }
}
