package com.example.calculadora_dolar_a_bcv.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Utility object for formatting currency values
 */
object CurrencyFormatter {
    
    private val symbols = DecimalFormatSymbols(Locale("es", "VE")).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }
    
    private val bsFormatter = DecimalFormat("#,##0.00", symbols)
    private val usdFormatter = DecimalFormat("#,##0.00", symbols)
    private val rateFormatter = DecimalFormat("#,##0.00", symbols)
    
    /**
     * Format bolivares amount
     * Example: 16000.00 -> "16.000,00"
     */
    fun formatBolivares(amount: Double): String {
        return bsFormatter.format(amount)
    }
    
    /**
     * Format dollars amount
     * Example: 48.45 -> "48,45"
     */
    fun formatDollars(amount: Double): String {
        return usdFormatter.format(amount)
    }
    
    /**
     * Format exchange rate
     * Example: 330.3751 -> "330,38"
     */
    fun formatRate(rate: Double): String {
        return rateFormatter.format(rate)
    }
    
    /**
     * Parse formatted string to double
     * Example: "16.000,00" -> 16000.00
     */
    fun parseAmount(formatted: String): Double? {
        return try {
            val normalized = formatted
                .replace(".", "")  // Remove thousand separators
                .replace(",", ".") // Convert decimal separator
            normalized.toDoubleOrNull()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Format percentage change
     * Example: 1.53222569635027 -> "+1.53%"
     */
    fun formatPercentage(percentage: Double): String {
        val sign = if (percentage >= 0) "+" else ""
        return "$sign%.2f%%".format(Locale.US, percentage)
    }
}
