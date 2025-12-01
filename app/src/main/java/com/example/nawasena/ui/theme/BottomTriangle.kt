package com.example.nawasena.ui.theme

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class BottomTriangle(
    // Ketinggian puncak segitiga dari atas box
    private val peakHeightDp: Float = 100f,
    // Lebar ketumpulan di puncak (untuk membuat rounded corner)
    private val cornerRadiusDp: Float = 30f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ) = androidx.compose.ui.graphics.Outline.Generic(
        path = Path().apply {
            val width = size.width
            val height = size.height

            // Konversi DP ke PX
            val peakHeightPx = peakHeightDp * density.density
            val cornerRadiusPx = cornerRadiusDp * density.density

            val peakX = width / 2f // Titik puncak di tengah horizontal
            val peakY = height - peakHeightPx // Puncak segitiga dari bawah box

            // 1. Mulai dari sudut Kiri Bawah
            moveTo(0f, height)

            // 2. Garis miring dari Kiri Bawah ke titik sebelum Puncak (kiri)
            lineTo(peakX - cornerRadiusPx, peakY)

            // 3. Kurva tumpul di puncak
            quadraticBezierTo(
                x1 = peakX,
                y1 = peakY - (cornerRadiusPx * 0.5f), // Puncak sedikit lebih tinggi
                x2 = peakX + cornerRadiusPx,
                y2 = peakY
            )

            // 4. Garis miring dari Puncak (kanan) ke Kanan Bawah
            lineTo(width, height)

            // 5. Tutup path
            close()
        }
    )
}