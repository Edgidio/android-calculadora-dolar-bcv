package com.example.calculadora_dolar_a_bcv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculadora_dolar_a_bcv.data.CombinedRates
import com.example.calculadora_dolar_a_bcv.data.ExchangeRateRepository
import com.example.calculadora_dolar_a_bcv.data.api.RetrofitClient
import com.example.calculadora_dolar_a_bcv.util.CurrencyFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the main screen
 * Manages exchange rates, currency conversion, and UI state
 */
class MainViewModel : ViewModel() {
    
    private val repository = ExchangeRateRepository(
        RetrofitClient.dolarVzlaService,
        RetrofitClient.dolarApiService
    )
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    private val _selectedCurrency = MutableStateFlow(CurrencyType.USD_OFICIAL)
    val selectedCurrency: StateFlow<CurrencyType> = _selectedCurrency.asStateFlow()
    
    private val _bolivaresInput = MutableStateFlow("")
    val bolivaresInput: StateFlow<String> = _bolivaresInput.asStateFlow()
    
    private val _dollarsInput = MutableStateFlow("")
    val dollarsInput: StateFlow<String> = _dollarsInput.asStateFlow()
    
    init {
        loadRates()
    }
    
    fun loadRates() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            repository.getAllRates().fold(
                onSuccess = { rates ->
                    _uiState.value = UiState.Success(rates)
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Error desconocido")
                }
            )
        }
    }
    
    fun selectCurrency(currencyType: CurrencyType) {
        _selectedCurrency.value = currencyType
        // Recalculate conversion with new rate
        if (_bolivaresInput.value.isNotEmpty()) {
            convertBolivaresToDollars(_bolivaresInput.value)
        } else if (_dollarsInput.value.isNotEmpty()) {
            convertDollarsToBolivares(_dollarsInput.value)
        }
    }
    
    fun convertBolivaresToDollars(bsText: String) {
        // Only keep digits
        val digits = bsText.filter { it.isDigit() }
        _bolivaresInput.value = digits
        _dollarsInput.value = ""
        
        if (digits.isEmpty()) {
            _dollarsInput.value = ""
            return
        }
        
        // Parse as cents (e.g. "100" -> 1.00)
        val bsAmount = digits.toDoubleOrNull()?.div(100.0) ?: return
        val rate = getCurrentRate() ?: return
        
        val dollarsAmount = bsAmount / rate
        
        // Format result back to digits string (e.g. 1.00 -> "100")
        // Use standard formatting first then strip non-digits to ensure correct rounding
        val formattedDollars = CurrencyFormatter.formatDollars(dollarsAmount)
        _dollarsInput.value = formattedDollars.filter { it.isDigit() }
    }
    
    fun convertDollarsToBolivares(usdText: String) {
        // Only keep digits
        val digits = usdText.filter { it.isDigit() }
        _dollarsInput.value = digits
        _bolivaresInput.value = ""
        
        if (digits.isEmpty()) {
            _bolivaresInput.value = ""
            return
        }
        
        // Parse as cents
        val usdAmount = digits.toDoubleOrNull()?.div(100.0) ?: return
        val rate = getCurrentRate() ?: return
        
        val bsAmount = usdAmount * rate
        
        // Format result back to digits
        val formattedBs = CurrencyFormatter.formatBolivares(bsAmount)
        _bolivaresInput.value = formattedBs.filter { it.isDigit() }
    }
    
    private fun getCurrentRate(): Double? {
        val state = _uiState.value
        if (state !is UiState.Success) return null
        
        return when (_selectedCurrency.value) {
            CurrencyType.USD_OFICIAL -> state.rates.usdOfficial
            CurrencyType.EUR_OFICIAL -> state.rates.eurOfficial
            CurrencyType.PARALELO -> state.rates.paralelo
        }
    }
    
    fun getCurrentPercentage(): Double? {
        val state = _uiState.value
        if (state !is UiState.Success) return null
        
        return when (_selectedCurrency.value) {
            CurrencyType.USD_OFICIAL -> state.rates.usdChangePercentage
            CurrencyType.EUR_OFICIAL -> state.rates.eurChangePercentage
            CurrencyType.PARALELO -> null // Parallel rate doesn't have percentage change
        }
    }
}

/**
 * UI State sealed class
 */
sealed class UiState {
    object Loading : UiState()
    data class Success(val rates: CombinedRates) : UiState()
    data class Error(val message: String) : UiState()
}

/**
 * Currency types available in the app
 */
enum class CurrencyType(val displayName: String) {
    USD_OFICIAL("USD Oficial"),
    EUR_OFICIAL("EUR Oficial"),
    PARALELO("Paralelo")
}
