package com.example.calculadora_dolar_a_bcv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculadora_dolar_a_bcv.ui.MainScreen
import com.example.calculadora_dolar_a_bcv.ui.MainViewModel
import com.example.calculadora_dolar_a_bcv.ui.ShoppingListScreen
import com.example.calculadora_dolar_a_bcv.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculadoradolarabcvTheme {
                AppContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf(Screen.Calculator) }
    
    // Shared MainViewModel for rates
    val mainViewModel: MainViewModel = viewModel()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                windowInsets = WindowInsets(0, 0, 0, 0)
            ) {
                // Custom Header
                DrawerHeader()
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Navigation Items
                NavigationDrawerItem(
                    label = { Text(text = "Calculadora Dólar") },
                    selected = currentScreen == Screen.Calculator,
                    onClick = {
                        scope.launch { 
                            drawerState.close() 
                            currentScreen = Screen.Calculator
                        }
                    },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = BCVBluePrimary,
                        selectedTextColor = Color.White,
                        selectedIconColor = Color.White,
                        unselectedTextColor = TextSecondary,
                        unselectedIconColor = TextSecondary
                    )
                )
                
                NavigationDrawerItem(
                    label = { Text(text = "Calculadora de Compra") },
                    selected = currentScreen == Screen.Shopping,
                    onClick = {
                        scope.launch { 
                            drawerState.close() 
                            currentScreen = Screen.Shopping
                        }
                    },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = BCVBluePrimary,
                        selectedTextColor = Color.White,
                        selectedIconColor = Color.White,
                        unselectedTextColor = TextSecondary,
                        unselectedIconColor = TextSecondary
                    )
                )
            }
        }
    ) {
        val contentModifier = Modifier.fillMaxSize()
        
        Surface(modifier = contentModifier, color = BackgroundLight) {
             when (currentScreen) {
                Screen.Calculator -> MainScreen(
                    mainViewModel = mainViewModel,
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
                Screen.Shopping -> {
                     Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = { 
                                    Text(
                                        "Lista de Compras",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    ) 
                                },
                                navigationIcon = {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = BCVBluePrimary,
                                    titleContentColor = Color.White,
                                    navigationIconContentColor = Color.White,
                                    actionIconContentColor = Color.White
                                )
                            )
                        }
                    ) { padding ->
                        Box(modifier = Modifier.padding(padding)) {
                            ShoppingListScreen(mainViewModel = mainViewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(BCVBluePrimary, BCVBlueDark)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo_bcv),
                contentDescription = "Logo BCV",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color.White)
                    .padding(8.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Calculadora BCV",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}

enum class Screen {
    Calculator,
    Shopping
}