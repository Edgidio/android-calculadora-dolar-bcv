package com.example.calculadora_dolar_a_bcv.data.api

import com.example.calculadora_dolar_a_bcv.data.model.ParaleloResponse
import retrofit2.http.GET

/**
 * Retrofit service for dolarapi.com API
 * Provides parallel market exchange rate
 */
interface DolarApiService {
    
    @GET("v1/dolares/paralelo")
    suspend fun getParaleloRate(): ParaleloResponse
}
