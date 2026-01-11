package com.example.calculadora_dolar_a_bcv.data.api

import com.example.calculadora_dolar_a_bcv.data.model.ExchangeRateResponse
import retrofit2.http.GET

/**
 * Retrofit service for dolarvzla.com API
 * Provides official USD and EUR exchange rates from BCV
 */
interface DolarVzlaService {
    
    @GET("public/exchange-rate")
    suspend fun getExchangeRate(): ExchangeRateResponse
}
