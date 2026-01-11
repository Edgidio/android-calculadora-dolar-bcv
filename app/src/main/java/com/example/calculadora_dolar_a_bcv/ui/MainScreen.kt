package com.example.calculadora_dolar_a_bcv.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculadora_dolar_a_bcv.R
import com.example.calculadora_dolar_a_bcv.ui.theme.*
import com.example.calculadora_dolar_a_bcv.util.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    onOpenDrawer: () -> Unit
) {
    val uiState by mainViewModel.uiState.collectAsState()
    val selectedCurrency by mainViewModel.selectedCurrency.collectAsState()
    val bolivaresInput by mainViewModel.bolivaresInput.collectAsState()
    val dollarsInput by mainViewModel.dollarsInput.collectAsState()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header with BCV logo
            HeaderSection(
                onRefresh = { mainViewModel.loadRates() },
                onOpenDrawer = onOpenDrawer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Currency selector
            CurrencySelector(
                selectedCurrency = selectedCurrency,
                onCurrencySelected = { mainViewModel.selectCurrency(it) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Main content based on state
            when (uiState) {
                is UiState.Loading -> {
                    LoadingView()
                }
                is UiState.Success -> {
                    val rates = (uiState as UiState.Success).rates
                    SuccessView(
                        viewModel = mainViewModel,
                        selectedCurrency = selectedCurrency,
                        bolivaresInput = bolivaresInput,
                        dollarsInput = dollarsInput,
                        rates = rates
                    )
                }
                is UiState.Error -> {
                    ErrorView(
                        message = (uiState as UiState.Error).message,
                        onRetry = { mainViewModel.loadRates() }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderSection(
    onRefresh: () -> Unit,
    onOpenDrawer: () -> Unit
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isRefreshing) 360f else 0f,
        animationSpec = tween(durationMillis = 1000),
        finishedListener = { isRefreshing = false }
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BCVBluePrimary, BCVBlueDark)
                )
            )
            .padding(top = 48.dp, bottom = 32.dp)
    ) {
        // Menu Button (Top Left)
        IconButton(
            onClick = onOpenDrawer,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 8.dp, start = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // BCV Logo
            Image(
                painter = painterResource(id = R.drawable.logo_bcv),
                contentDescription = "Logo BCV",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(60.dp))
                    .background(Color.White)
                    .padding(12.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Calculadora de Dólar",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Tasas Oficiales BCV",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
        
        // Refresh button
        IconButton(
            onClick = {
                isRefreshing = true
                onRefresh()
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Actualizar",
                tint = Color.White,
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

@Composable
fun CurrencySelector(
    selectedCurrency: CurrencyType,
    onCurrencySelected: (CurrencyType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CurrencyType.entries.forEach { currency ->
            val isSelected = selectedCurrency == currency
            
            Button(
                onClick = { onCurrencySelected(currency) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                contentPadding = PaddingValues(horizontal = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) BCVBluePrimary else Color.White,
                    contentColor = if (isSelected) Color.White else BCVBluePrimary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = if (isSelected) 8.dp else 2.dp
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = currency.displayName,
                    fontSize = 11.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun SuccessView(
    viewModel: MainViewModel,
    selectedCurrency: CurrencyType,
    bolivaresInput: String,
    dollarsInput: String,
    rates: com.example.calculadora_dolar_a_bcv.data.CombinedRates
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // Exchange Rate Card
        ExchangeRateCard(
            viewModel = viewModel,
            selectedCurrency = selectedCurrency,
            rates = rates
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Currency Conversion Inputs
        ConversionSection(
            viewModel = viewModel,
            bolivaresInput = bolivaresInput,
            dollarsInput = dollarsInput,
            rates = rates,
            selectedCurrency = selectedCurrency
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Last update info
        Text(
            text = "Última actualización: ${formatDate(rates.lastUpdate)}",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ExchangeRateCard(
    viewModel: MainViewModel,
    selectedCurrency: CurrencyType,
    rates: com.example.calculadora_dolar_a_bcv.data.CombinedRates
) {
    val currentRate = when (selectedCurrency) {
        CurrencyType.USD_OFICIAL -> rates.usdOfficial
        CurrencyType.EUR_OFICIAL -> rates.eurOfficial
        CurrencyType.PARALELO -> rates.paralelo
    }
    
    val changePercentage = viewModel.getCurrentPercentage()
    val isPositive = (changePercentage ?: 0.0) > 0
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Title in top right
            Text(
                text = "Tasa Actual",
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.TopStart)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Rate and Bs inline
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = currentRate?.let { CurrencyFormatter.formatRate(it) } ?: "---",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = BCVBluePrimary,
                        lineHeight = 48.sp
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "Bs",
                        fontSize = 20.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                // Percentage slightly higher up
                if (changePercentage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (isPositive) PositiveChange else NegativeChange,
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(2.dp))
                        
                        Text(
                            text = CurrencyFormatter.formatPercentage(changePercentage),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isPositive) PositiveChange else NegativeChange
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversionSection(
    viewModel: MainViewModel,
    bolivaresInput: String,
    dollarsInput: String,
    rates: com.example.calculadora_dolar_a_bcv.data.CombinedRates,
    selectedCurrency: CurrencyType
) {
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    val context = androidx.compose.ui.platform.LocalContext.current
    
    val currentRate = when (selectedCurrency) {
        CurrencyType.USD_OFICIAL -> rates.usdOfficial
        CurrencyType.EUR_OFICIAL -> rates.eurOfficial
        CurrencyType.PARALELO -> rates.paralelo
    }

    val bsPlaceholder = currentRate?.let { CurrencyFormatter.formatRate(it) } ?: "0,00"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Dollars Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "USD",
                    fontWeight = FontWeight.Bold,
                    color = BCVBluePrimary,
                    modifier = Modifier.width(40.dp)
                )
                
                TextField(
                    value = dollarsInput,
                    onValueChange = { viewModel.convertDollarsToBolivares(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { 
                        Text(
                            text = "1.00",
                            color = TextHint,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        ) 
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = com.example.calculadora_dolar_a_bcv.util.CurrencyVisualTransformation(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        textAlign = TextAlign.End,
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 1.dp,
                color = Color.LightGray.copy(alpha = 0.5f)
            )

            // Bolivares Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BS",
                    fontWeight = FontWeight.Bold,
                    color = BCVBluePrimary,
                    modifier = Modifier.width(40.dp)
                )

                TextField(
                    value = bolivaresInput,
                    onValueChange = { viewModel.convertBolivaresToDollars(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { 
                        Text(
                            text = bsPlaceholder,
                            color = TextHint,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        ) 
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = com.example.calculadora_dolar_a_bcv.util.CurrencyVisualTransformation(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        textAlign = TextAlign.End,
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                )

                IconButton(
                    onClick = {
                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(bolivaresInput))
                        // Optional: Show toast
                        android.widget.Toast.makeText(context, "Copiado", android.widget.Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copiar",
                        tint = BCVBluePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(64.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = BCVBluePrimary,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Cargando tasas...",
                fontSize = 16.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = ErrorRed.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Error",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ErrorRed
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BCVBluePrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reintentar")
                }
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        // Try to parse ISO format
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = isoFormat.parse(dateString)
        val displayFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        displayFormat.format(date ?: Date())
    } catch (e: Exception) {
        try {
            // Try simple date format
            val simpleFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = simpleFormat.parse(dateString)
            val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            displayFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }
}
