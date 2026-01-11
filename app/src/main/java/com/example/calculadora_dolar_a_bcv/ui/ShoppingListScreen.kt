package com.example.calculadora_dolar_a_bcv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculadora_dolar_a_bcv.data.model.ShoppingItem
import com.example.calculadora_dolar_a_bcv.ui.theme.*
import com.example.calculadora_dolar_a_bcv.util.CurrencyFormatter
import com.example.calculadora_dolar_a_bcv.util.CurrencyVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    shoppingViewModel: ShoppingViewModel = viewModel(),
    mainViewModel: MainViewModel // We need this for exchange rates
) {
    val budget by shoppingViewModel.budget.collectAsState()
    val isBudgetInMd by shoppingViewModel.isBudgetInMd.collectAsState()
    val items by shoppingViewModel.items.collectAsState()
    val totalSpentUsd by shoppingViewModel.totalSpentUsd.collectAsState()
    
    // Derived state for exchange rate
    val uiState by mainViewModel.uiState.collectAsState()
    val exchangeRate = when (uiState) {
        is UiState.Success -> (uiState as UiState.Success).rates.usdOfficial ?: 0.0
        else -> 0.0
    }

    // Input states
    var productName by remember { mutableStateOf("") }
    var productQuantity by remember { mutableIntStateOf(1) } // Int for stepper
    var productPriceRaw by remember { mutableStateOf("") }
    var budgetInputRaw by remember { mutableStateOf("") }

    // Logic to handle budget input changes with formatting
    fun updateBudget(text: String) {
        val digits = text.filter { it.isDigit() }
        budgetInputRaw = digits
        val amount = digits.toDoubleOrNull()?.div(100.0) ?: 0.0
        shoppingViewModel.setBudget(amount)
    }

    // Calculate totals
    val totalSpentBs = totalSpentUsd * exchangeRate
    val budgetUsd = if (isBudgetInMd) {
        if (exchangeRate > 0.0) budget / exchangeRate else 0.0
    } else {
        budget
    }
    val remainingUsd = budgetUsd - totalSpentUsd
    val remainingBs = remainingUsd * exchangeRate
    
    val isOverBudget = remainingUsd < 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- TOP SECTION: BUDGET & TOTALS ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isOverBudget) ErrorRed.copy(alpha = 0.05f) else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                
                // Title at the very top
                Text(
                    text = "Presupuesto de compra",
                    fontWeight = FontWeight.Bold,
                    color = BCVBluePrimary,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Budget Input Row
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = budgetInputRaw,
                        onValueChange = { updateBudget(it) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("0,00", textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth()) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = CurrencyVisualTransformation(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.LightGray,
                            focusedBorderColor = BCVBluePrimary
                        ),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = { shoppingViewModel.toggleBudgetCurrency() },
                         colors = ButtonDefaults.buttonColors(containerColor = BCVBluePrimary),
                         shape = RoundedCornerShape(8.dp),
                         contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text(if (isBudgetInMd) "Bs" else "USD")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Totals Summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Total Spent
                    Column {
                        Text("Total Gastado", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            CurrencyFormatter.formatDollars(totalSpentUsd), 
                            fontWeight = FontWeight.Bold, 
                            color = TextPrimary,
                            fontSize = 18.sp
                        )
                        Text(
                            "${CurrencyFormatter.formatBolivares(totalSpentBs)} Bs", 
                            fontSize = 11.sp, 
                            color = TextSecondary
                        )
                    }
                    
                    // Remaining
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Restante", fontSize = 12.sp, color = TextSecondary)
                         Text(
                            CurrencyFormatter.formatDollars(remainingUsd), 
                            fontWeight = FontWeight.Bold,
                            color = if (isOverBudget) ErrorRed else TextPrimary,
                            fontSize = 18.sp
                        )
                        Text(
                            "${CurrencyFormatter.formatBolivares(remainingBs)} Bs", 
                            fontSize = 11.sp, 
                            color = if (isOverBudget) ErrorRed else TextSecondary
                        )
                    }
                }
                
                if (isOverBudget) {
                     Text(
                        "¡Has excedido tu presupuesto!",
                        color = ErrorRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- PRODUCT ENTRY SECTION ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Producto") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BCVBluePrimary,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stepper for Quantity
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(0.4f)
                            .height(56.dp) // Match TextField height roughly
                            .background(Color.Transparent)
                    ) {
                        IconButton(
                            onClick = { if (productQuantity > 1) productQuantity-- },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = BCVBluePrimary)
                        }
                        
                        Text(
                            text = productQuantity.toString(),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp),
                            fontSize = 16.sp
                        )
                        
                        IconButton(
                            onClick = { productQuantity++ },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", tint = BCVBluePrimary)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Price Input
                    OutlinedTextField(
                        value = productPriceRaw,
                        onValueChange = { productPriceRaw = it.filter { char -> char.isDigit() } },
                        label = { Text("Precio ($)") },
                        modifier = Modifier.weight(0.6f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = CurrencyVisualTransformation(),
                        singleLine = true,
                         colors = OutlinedTextFieldDefaults.colors(
                             focusedBorderColor = BCVBluePrimary,
                             unfocusedBorderColor = Color.LightGray
                        ),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = {
                        val price = (productPriceRaw.toDoubleOrNull() ?: 0.0) / 100.0
                        if (productName.isNotEmpty() && price > 0) {
                            shoppingViewModel.addItem(productName, productQuantity, price)
                            productName = ""
                            productQuantity = 1
                            productPriceRaw = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BCVBluePrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AGREGAR")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // --- PRODUCT LIST HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Lista de Productos",
                style = MaterialTheme.typography.titleMedium,
                color = BCVBluePrimary
            )
            
            if (items.isNotEmpty()) {
                IconButton(
                    onClick = { shoppingViewModel.clearList() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete, // Or Icons.Outlined.Delete if available
                        contentDescription = "Limpiar lista",
                        tint = ErrorRed
                    )
                }
            }
        }

        // --- PRODUCT LIST CONTENT ---
        // Simplified list without Cards
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top
        ) {
            items(items) { item ->
                ShoppingItemRowSimple(
                    item = item,
                    onUpdateQuantity = { q -> shoppingViewModel.updateItemQuantity(item, q) }
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            }
        }
    }
}

@Composable
fun ShoppingItemRowSimple(
    item: ShoppingItem,
    onUpdateQuantity: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Name and Unit Price
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontWeight = FontWeight.Medium, fontSize = 16.sp, color = TextPrimary)
            Text(
                "${CurrencyFormatter.formatDollars(item.priceUsd)} c/u", 
                fontSize = 12.sp, 
                color = TextSecondary
            )
        }
        
        // Quantity Controls (Small)
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { onUpdateQuantity(item.quantity - 1) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = BCVBluePrimary, modifier = Modifier.size(16.dp))
            }
            Text(
                item.quantity.toString(),
                modifier = Modifier.padding(horizontal = 8.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            IconButton(
                onClick = { onUpdateQuantity(item.quantity + 1) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase", tint = BCVBluePrimary, modifier = Modifier.size(16.dp))
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Total Price for Item
        Text(
            CurrencyFormatter.formatDollars(item.totalUsd),
            fontWeight = FontWeight.Bold,
            color = BCVBluePrimary,
            modifier = Modifier.widthIn(min = 60.dp),
            textAlign = TextAlign.End
        )
    }
}
