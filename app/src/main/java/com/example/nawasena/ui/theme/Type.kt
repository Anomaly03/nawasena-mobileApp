package com.example.nawasena.ui.theme

import com.example.nawasena.R
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),

    // Medium
    Font(R.font.poppins_medium, FontWeight.Medium), // Misalnya, menggunakan file poppins_medium.ttf

    // Bold
    Font(R.font.poppins_bold, FontWeight.Bold),     // Misalnya, menggunakan file poppins_bold.ttf

    // Extra Bold
    Font(R.font.poppins_extrabold, FontWeight.ExtraBold),

    // Font light
    Font(R.font.poppins_light, FontWeight.Light),

    // Font Tebal custom
    Font(R.font.poppins_black, FontWeight.Black)
)


val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Black, // <-- Menggunakan bobot Black atau ExtraBold
        fontSize = 32.sp              // <-- Ukuran besar
    )
)