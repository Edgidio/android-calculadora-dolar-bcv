package com.example.calculadora_dolar_a_bcv.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val BCVColorScheme = lightColorScheme(
    primary = BCVBluePrimary,
    onPrimary = BCVWhite,
    primaryContainer = BCVBlueLight,
    onPrimaryContainer = BCVBlueDark,
    
    secondary = AccentGold,
    onSecondary = BCVWhite,
    
    tertiary = SuccessGreen,
    
    error = ErrorRed,
    onError = BCVWhite,
    
    background = BackgroundLight,
    onBackground = TextPrimary,
    
    surface = CardBackground,
    onSurface = TextPrimary,
    
    surfaceVariant = BCVBlueLight,
    onSurfaceVariant = TextSecondary,
    
    outline = TextHint
)

@Composable
fun CalculadoradolarabcvTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BCVColorScheme,
        typography = Typography,
        content = content
    )
}