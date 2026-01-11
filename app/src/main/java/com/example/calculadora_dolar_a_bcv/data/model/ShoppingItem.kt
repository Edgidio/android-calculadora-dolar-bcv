package com.example.calculadora_dolar_a_bcv.data.model

import java.util.UUID

data class ShoppingItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val quantity: Int,
    val priceUsd: Double
) {
    val totalUsd: Double
        get() = quantity * priceUsd
}
