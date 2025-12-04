package com.example.nawasena

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nawasena.data.local.NawasenaDatabase
import com.example.nawasena.data.repository.AuthRepository
import com.example.nawasena.data.repository.DestinationRepository
import com.example.nawasena.data.repository.ProfileRepository // IMPORT INI
import com.example.nawasena.ui.theme.NawasenaTheme
import com.example.nawasena.ui.viewmodel.AuthViewModel
import com.example.nawasena.ui.viewmodel.DashboardViewModel
import com.example.nawasena.ui.viewmodel.ProfileViewModel
import com.example.nawasena.ui.viewmodel.ProfileViewModelFactory
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
            // Logika izin bisa ditambahkan jika perlu
        }

        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        // --- 3. SETUP DATABASE & FIREBASE ---
        val database = NawasenaDatabase.getDatabase(this)
        val profileDao = database.profileDao()

        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        // --- 4. SETUP REPOSITORY ---
        val authRepository = AuthRepository(auth, firestore)
        val destinationRepository = DestinationRepository(firestore)

        // Setup ProfileRepository (Gabungan DAO + Firestore)
        val profileRepository = ProfileRepository(profileDao, firestore)

        // --- 5. SETUP GLOBAL VIEWMODEL ---
        val authViewModel = AuthViewModel(authRepository)
        val dashboardViewModel = DashboardViewModel(destinationRepository)

        setContent {
            NawasenaTheme {
                // State lokasi
                var userLat by remember { mutableDoubleStateOf(0.0) }
                var userLong by remember { mutableDoubleStateOf(0.0) }

                // Ambil lokasi sekali saat aplikasi mulai
                LaunchedEffect(Unit) {
                    getUserLocation { lat, long ->
                        userLat = lat
                        userLong = long
                    }
                }

                // Ambil state user saat ini dari AuthViewModel
                val authState by authViewModel.uiState.collectAsState()
                val currentUser = authState.currentUser

                // --- 6. SETUP PROFILE VIEWMODEL (DENGAN REPOSITORY) ---
                val profileViewModel: ProfileViewModel = viewModel(
                    key = currentUser?.uid, // Refresh VM jika user login/logout
                    factory = ProfileViewModelFactory(
                        repository = profileRepository, // Inject Repository di sini
                        user = currentUser
                    )
                )

                // Sinkronisasi data awal (Cloud -> Local) saat login
                LaunchedEffect(currentUser) {
                    currentUser?.let { user ->
                        profileViewModel.ensureUserInRoom(user)
                    }
                }

                // --- 7. JALANKAN APLIKASI ---
                NawasenaApp(
                    authViewModel = authViewModel,
                    dashboardViewModel = dashboardViewModel,
                    profileViewModel = profileViewModel,
                    userLat = userLat,
                    userLong = userLong
                )
            }
        }
    }

    // --- FUNGSI HELPER AMBIL LOKASI ---
    private fun getUserLocation(onLocationReceived: (Double, Double) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onLocationReceived(location.latitude, location.longitude)
            }
        }
    }
}