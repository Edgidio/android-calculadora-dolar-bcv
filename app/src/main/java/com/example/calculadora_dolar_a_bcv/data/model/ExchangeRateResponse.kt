package com.example.calculadora_dolar_a_bcv.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response from dolarvzla.com API
 * Contains current, previous exchange rates and change percentages for USD and EUR
 */
data class ExchangeRateResponse(
    @SerializedName("current")
    val current: CurrentRate,
    
    @SerializedName("previous")
    val previous: PreviousRate,
    
    @SerializedName("changePercentage")
    val changePercentage: ChangePercentage
)

data class CurrentRate(
    @SerializedName("usd")
    val usd: Double,
    
    @SerializedName("eur")
    val eur: Double,
    
    @SerializedName("date")
    val date: String
)

data class PreviousRate(
    @SerializedName("usd")
    val usd: Double,
    
    @SerializedName("eur")
    val eur: Double,
    
    @SerializedName("date")
    val date: String
)

data class ChangePercentage(
    @SerializedName("usd")
    val usd: Double,
    
    @SerializedName("eur")
    val eur: Double
)
