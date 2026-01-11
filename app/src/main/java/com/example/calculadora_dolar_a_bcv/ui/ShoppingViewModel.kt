package com.example.calculadora_dolar_a_bcv.ui

import androidx.lifecycle.ViewModel
import com.example.calculadora_dolar_a_bcv.data.model.ShoppingItem
import com.example.calculadora_dolar_a_bcv.util.CurrencyFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ShoppingViewModel : ViewModel() {

    private val _budget = MutableStateFlow(0.0)
    val budget: StateFlow<Double> = _budget.asStateFlow()

    private val _isBudgetInMd = MutableStateFlow(true) // true = Bs, false = USD
    val isBudgetInMd: StateFlow<Boolean> = _isBudgetInMd.asStateFlow()

    private val _items = MutableStateFlow<List<ShoppingItem>>(emptyList())
    val items: StateFlow<List<ShoppingItem>> = _items.asStateFlow()

    private val _totalSpentUsd = MutableStateFlow(0.0)
    val totalSpentUsd: StateFlow<Double> = _totalSpentUsd.asStateFlow()

    fun setBudget(amount: Double) {
        _budget.value = amount
    }

    fun toggleBudgetCurrency() {
        _isBudgetInMd.value = !_isBudgetInMd.value
    }

    fun addItem(name: String, quantity: Int, priceUsd: Double) {
        val newItem = ShoppingItem(name = name, quantity = quantity, priceUsd = priceUsd)
        _items.value = _items.value + newItem
        recalculateTotal()
    }

    fun removeItem(item: ShoppingItem) {
        _items.value = _items.value - item
        recalculateTotal()
    }

    fun updateItemQuantity(item: ShoppingItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItem(item)
            return
        }
        val updatedItems = _items.value.map {
            if (it.id == item.id) it.copy(quantity = newQuantity) else it
        }
        _items.value = updatedItems
        recalculateTotal()
    }

    fun clearList() {
        _items.value = emptyList()
        recalculateTotal()
    }

    private fun recalculateTotal() {
        _totalSpentUsd.value = _items.value.sumOf { it.totalUsd }
    }
}
