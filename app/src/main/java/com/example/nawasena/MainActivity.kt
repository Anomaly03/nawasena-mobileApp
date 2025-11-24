// File pertama yang dijalankan oleh Android.
// Hanya berfungsi untuk memuat tema (NawasenaTheme) dan memanggil NawasenaApp().
// Tidak ada menempatkan logika disini.

package com.example.nawasena

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.example.nawasena.ui.theme.NawasenaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NawasenaTheme {
                // Panggil apliakasi
                NawasenaApp()
            }
        }
    }
}