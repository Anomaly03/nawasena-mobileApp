package com.example.nawasena

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import com.example.nawasena.data.repository.AuthRepository
import com.example.nawasena.data.repository.DestinationRepository
import com.example.nawasena.ui.theme.NawasenaTheme
import com.example.nawasena.ui.viewmodel.AuthViewModel
import com.example.nawasena.ui.viewmodel.DashboardViewModel
import com.example.nawasena.utils.DestinationSeeder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    // Klien GPS
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // --- 1. SETUP GPS CLIENT ---
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // --- 2. SETUP PERMISSION LAUNCHER ---
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            // Izin diterima/ditolak logic ada di setContent
        }

        // Minta izin saat aplikasi dibuka
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        // --- 3. SETUP FIREBASE & MVVM ---
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        // Rakit Auth
        val authRepository = AuthRepository(auth, firestore)
        val authViewModel = AuthViewModel(authRepository)

        // Rakit Dashboard
        val destinationRepository = DestinationRepository(firestore)
        val dashboardViewModel = DashboardViewModel(destinationRepository)

        setContent {
            NawasenaTheme {
                // State lokasi
                var userLat by remember { mutableDoubleStateOf(0.0) }
                var userLong by remember { mutableDoubleStateOf(0.0) }

                // Ambil lokasi sekali saat aplikasi mulai
                // PERBAIKAN: Hapus anotasi @RequiresPermission di sini karena bikin error syntax
                LaunchedEffect(Unit) {
                    getUserLocation { lat, long ->
                        userLat = lat
                        userLong = long
                    }
                }

                // --- 4. JALANKAN APLIKASI ---
                NawasenaApp(
                    authViewModel = authViewModel,
                    dashboardViewModel = dashboardViewModel,
                    userLat = userLat,
                    userLong = userLong
                )
            }
        }
    }

    // --- FUNGSI HELPER AMBIL LOKASI ---
    private fun getUserLocation(onLocationReceived: (Double, Double) -> Unit) {
        // Cek izin secara manual agar aman
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return // Stop jika tidak ada izin
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onLocationReceived(location.latitude, location.longitude)
            }
        }
    }
}