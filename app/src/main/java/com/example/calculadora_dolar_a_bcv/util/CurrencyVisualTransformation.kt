package com.example.calculadora_dolar_a_bcv.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * VisualTransformation for real-time currency formatting
 * Formats input as the user types: 10000 -> 10.000,00
 */
class CurrencyVisualTransformation : VisualTransformation {
    
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        
        // Only accept digits and comma/dot
        val digitsOnly = originalText.filter { it.isDigit() }
        
        if (digitsOnly.isEmpty()) {
            return TransformedText(AnnotatedString(""), OffsetMapping.Identity)
        }
        
        // Format the number
        val formatted = formatNumber(digitsOnly)
        
        return TransformedText(
            text = AnnotatedString(formatted),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    // Map cursor position from original to formatted
                    val digitsBeforeCursor = text.text.take(offset).filter { it.isDigit() }.length
                    return calculateTransformedOffset(digitsBeforeCursor, formatted)
                }
                
                override fun transformedToOriginal(offset: Int): Int {
                    // Map cursor position from formatted back to original
                    val formatted = formatNumber(digitsOnly)
                    val digitsBeforeCursor = formatted.take(offset).filter { it.isDigit() }.length
                    return minOf(digitsBeforeCursor, text.length)
                }
            }
        )
    }
    
    private fun formatNumber(digits: String): String {
        if (digits.isEmpty()) return ""
        
        // Convert to decimal (last 2 digits are decimals)
        val integerPart: String
        val decimalPart: String
        
        when {
            digits.length == 1 -> {
                integerPart = "0"
                decimalPart = "0$digits"
            }
            digits.length == 2 -> {
                integerPart = "0"
                decimalPart = digits
            }
            else -> {
                integerPart = digits.dropLast(2)
                decimalPart = digits.takeLast(2)
            }
        }
        
        // Add thousand separators to integer part
        val formattedInteger = integerPart.reversed()
            .chunked(3)
            .joinToString(".")
            .reversed()
        
        return "$formattedInteger,$decimalPart"
    }
    
    private fun calculateTransformedOffset(digitsCount: Int, formatted: String): Int {
        var count = 0
        for (i in formatted.indices) {
            if (formatted[i].isDigit()) {
                count++
                if (count == digitsCount) {
                    return i + 1
                }
            }
        }
        return formatted.length
    }
}
