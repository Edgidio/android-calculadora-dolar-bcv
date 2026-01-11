package com.example.calculadora_dolar_a_bcv.data

import com.example.calculadora_dolar_a_bcv.data.api.DolarApiService
import com.example.calculadora_dolar_a_bcv.data.api.DolarVzlaService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Repository to manage exchange rate data from multiple sources
 * Combines official rates (USD, EUR) and parallel market rate
 */
class ExchangeRateRepository(
    private val dolarVzlaService: DolarVzlaService,
    private val dolarApiService: DolarApiService
) {
    
    /**
     * Fetches all exchange rates concurrently
     * Returns a CombinedRates object or null if both APIs fail
     */
    suspend fun getAllRates(): Result<CombinedRates> = try {
        coroutineScope {
            // Fetch both APIs concurrently
            val officialDeferred = async { 
                try {
                    dolarVzlaService.getExchangeRate()
                } catch (e: Exception) {
                    null
                }
            }
            
            val paraleloDeferred = async {
                try {
                    dolarApiService.getParaleloRate()
                } catch (e: Exception) {
                    null
                }
            }
            
            val official = officialDeferred.await()
            val paralelo = paraleloDeferred.await()
            
            if (official == null && paralelo == null) {
                Result.failure(Exception("No se pudieron obtener las tasas. Verifica tu conexión."))
            } else {
                Result.success(
                    CombinedRates(
                        usdOfficial = official?.current?.usd,
                        eurOfficial = official?.current?.eur,
                        paralelo = paralelo?.promedio,
                        usdChangePercentage = official?.changePercentage?.usd,
                        eurChangePercentage = official?.changePercentage?.eur,
                        lastUpdate = official?.current?.date ?: paralelo?.fechaActualizacion ?: ""
                    )
                )
            }
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

/**
 * Combined exchange rates from all sources
 */
data class CombinedRates(
    val usdOfficial: Double?,
    val eurOfficial: Double?,
    val paralelo: Double?,
    val usdChangePercentage: Double?,
    val eurChangePercentage: Double?,
    val lastUpdate: String
)
