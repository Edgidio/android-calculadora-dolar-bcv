package com.example.calculadora_dolar_a_bcv.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response from dolarapi.com API
 * Contains the parallel market exchange rate (promedio)
 */
data class ParaleloResponse(
    @SerializedName("fuente")
    val fuente: String,
    
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("compra")
    val compra: Double?,
    
    @SerializedName("venta")
    val venta: Double?,
    
    @SerializedName("promedio")
    val promedio: Double,
    
    @SerializedName("fechaActualizacion")
    val fechaActualizacion: String
)
