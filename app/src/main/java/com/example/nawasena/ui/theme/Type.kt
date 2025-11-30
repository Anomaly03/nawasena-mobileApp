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
    Font(R.font.poppins_extrabold, FontWeight.ExtraBold)
    )


val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)