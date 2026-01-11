package com.example.calculadora_dolar_a_bcv.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Provides Retrofit instances for API services
 */
object RetrofitClient {
    
    private fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    private val dolarVzlaRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.dolarvzla.com/")
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    private val dolarApiRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://ve.dolarapi.com/")
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val dolarVzlaService: DolarVzlaService by lazy {
        dolarVzlaRetrofit.create(DolarVzlaService::class.java)
    }
    
    val dolarApiService: DolarApiService by lazy {
        dolarApiRetrofit.create(DolarApiService::class.java)
    }
}
