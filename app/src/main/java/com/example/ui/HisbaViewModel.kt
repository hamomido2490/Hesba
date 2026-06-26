package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ComponentEntity
import com.example.data.HisbaRepository
import com.example.data.ProductEntity
import com.example.domain.CalculatorResults
import com.example.domain.CalculatorState
import com.example.domain.ComponentItem
import com.example.domain.ProfitMode
import com.example.domain.UnitSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HisbaViewModel(private val repository: HisbaRepository) : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state

    val results: StateFlow<CalculatorResults> = _state.map { calculateResults(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CalculatorResults())

    val savedProducts = repository.allProducts.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun updateProductName(name: String) {
        _state.update { it.copy(productName = name) }
    }

    fun updateCurrency(currency: String) {
        _state.update { it.copy(currency = currency) }
    }

    fun updateGeneralWaste(waste: String) {
        _state.update { it.copy(generalWastePercent = waste) }
    }

    fun updateProfitMode(mode: ProfitMode) {
        _state.update { it.copy(profitMode = mode) }
    }

    fun updateProfitInput(input: String) {
        _state.update { it.copy(profitInput = input) }
    }

    fun addComponent() {
        _state.update {
            it.copy(components = it.components + ComponentItem())
        }
    }

    fun removeComponent(id: String) {
        _state.update {
            it.copy(components = it.components.filter { c -> c.id != id })
        }
    }

    fun updateComponent(id: String, updater: (ComponentItem) -> ComponentItem) {
        _state.update { state ->
            state.copy(components = state.components.map { c ->
                if (c.id == id) updater(c) else c
            })
        }
    }

    fun clearAll() {
        _state.value = CalculatorState()
    }

    private fun calculateResults(state: CalculatorState): CalculatorResults {
        var baseCostTotal = 0f
        var wasteAmountTotal = 0f

        for (comp in state.components) {
            val q = comp.qty.toFloatOrNull() ?: 0f
            val p = comp.price.toFloatOrNull() ?: 0f
            val w = comp.wastePercent.toFloatOrNull() ?: 0f
            val (base, withWaste) = UnitSystem.calculateComponentCost(q, comp.unit, p, w)
            baseCostTotal += base
            wasteAmountTotal += (withWaste - base)
        }

        val subtotal = baseCostTotal + wasteAmountTotal
        val generalWasteStr = state.generalWastePercent.toFloatOrNull() ?: 0f
        val generalWasteAmount = subtotal * (generalWasteStr / 100f)

        val totalCost = subtotal + generalWasteAmount
        val totalWaste = wasteAmountTotal + generalWasteAmount

        val profitInputVal = state.profitInput.toFloatOrNull() ?: 0f
        val profit = if (state.profitMode == ProfitMode.PERCENT) {
            totalCost * (profitInputVal / 100f)
        } else {
            profitInputVal
        }

        val sellPrice = totalCost + profit

        return CalculatorResults(
            baseCostTotal = baseCostTotal,
            totalWaste = totalWaste,
            totalCost = totalCost,
            profit = profit,
            sellPrice = sellPrice
        )
    }

    fun saveProduct() {
        viewModelScope.launch {
            val currentState = _state.value
            val currentResults = results.value
            
            if (currentState.productName.isBlank() || currentState.components.isEmpty()) return@launch

            val df = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
            val dateStr = df.format(Date())

            val product = ProductEntity(
                name = currentState.productName,
                currency = currentState.currency,
                generalWaste = currentState.generalWastePercent.toFloatOrNull() ?: 0f,
                profitMode = currentState.profitMode.name,
                profitInput = currentState.profitInput.toFloatOrNull() ?: 0f,
                baseCostTotal = currentResults.baseCostTotal,
                totalWaste = currentResults.totalWaste,
                totalCost = currentResults.totalCost,
                profit = currentResults.profit,
                sellPrice = currentResults.sellPrice,
                date = dateStr
            )

            val components = currentState.components.map { it.toEntity(0) }
            repository.saveProductWithComponents(product, components)
            
            // clear form
            clearAll()
        }
    }

    fun deleteSavedProduct(id: Long) {
        viewModelScope.launch {
            repository.deleteProductWithComponents(id)
        }
    }

    class Factory(private val repository: HisbaRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HisbaViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HisbaViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
