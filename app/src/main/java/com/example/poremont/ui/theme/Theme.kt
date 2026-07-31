package com.example.poremont.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

<<<<<<< HEAD
/*
 * Define a custom colour palette that loosely follows the provided Figma
 * inspiration: a light background with a warm orange accent. The primary
 * colour is a saturated orange used for buttons and key actions. The
 * secondary and tertiary colours provide complementary tones for
 * illustrations and highlights. You can adjust these values if you
 * update your Figma design later; they are centralised here to avoid
 * scattering magic numbers throughout the code.
 */
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFA000),    // deep amber for dark mode accents
    onPrimary = Color(0xFF000000),
    secondary = Color(0xFFFFD54F),  // lighter amber for secondary actions
    onSecondary = Color(0xFF000000),
    tertiary = Color(0xFF80CBC4),   // teal accent for highlights
    onTertiary = Color(0xFF000000),
    background = Color(0xFF121212),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFFFFFFF)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF6F00),    // warm orange accent
    onPrimary = Color.White,
    secondary = Color(0xFFFFC107),  // amber for secondary actions
    onSecondary = Color.Black,
    tertiary = Color(0xFF4CAF50),   // green for success/progress
    onTertiary = Color.White,
    background = Color(0xFFFFFBF8), // off‑white light background
    onBackground = Color(0xFF1B1C20),
    surface = Color.White,
    onSurface = Color(0xFF1B1C20)
)
=======
private val DarkColorScheme = darkColorScheme(primary = Color(0xFF6750A4), secondary = Color(0xFF625B71), tertiary = Color(0xFF7D5260))
private val LightColorScheme = lightColorScheme(primary = Color(0xFF6750A4), secondary = Color(0xFF625B71), tertiary = Color(0xFF7D5260))
>>>>>>> 4d10c6215eb7563b03f068add459083a6174924d

@Composable
fun PORemontTheme(darkTheme: Boolean = isSystemInDarkTheme(), dynamicColor: Boolean = true, content: @Composable () -> Unit) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
<<<<<<< HEAD
    // Provide a default typography instance.  The generated Typography class
    // from material3 is used here instead of the one from kotlin.text to avoid
    // type mismatches during composition.
    MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
=======
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
>>>>>>> 4d10c6215eb7563b03f068add459083a6174924d
}