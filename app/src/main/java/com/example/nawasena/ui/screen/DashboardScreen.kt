package com.example.nawasena.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// import com.example.nawasena.data.model.User // Uncomment jika ada
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.zIndex
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.offset

// Data class untuk memegang data tempat
data class PlaceData(
    val title: String,
    val location: String,
    val distance: String,
    val reviews: String
)

// Data yang digunakan untuk Highlight Card dan PlaceCard
val samplePlacesData = listOf(
    PlaceData("Enjoy The View of the Ocean", "Tambak Cemandi, Sedati, Sidoarjo", "", "4.9 (5,400 reviews)"),
    PlaceData("Mpu Tantular Museum", "", "", "4.3 (400 reviews)"),
    PlaceData("Sidoarjo Mud Flow", "", "", "4.6 (3,412 reviews)"),
    PlaceData("Ha... Waterfall", "", "", "4.1 (120 reviews)"),
    PlaceData("Bromo Sunset", "", "", "4.8 (900 reviews)"),
)

// --- DASHBOARD SCREEN UTAMA ---
@Composable
fun DashboardScreen(
    user: Any? = null,
    onLogout: () -> Unit = {},
    onDestinationClick: (String) -> Unit = {}
) {
    // Variabel untuk mengontrol overlap (seberapa jauh Card ditarik ke atas)
    val overlapDistance = 150.dp

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            // Placeholder untuk Bottom NAVBAR
            BottomAppBar(
                containerColor = Color.White,
                modifier = Modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* Home */ }) { Icon(Icons.Filled.LocationOn, contentDescription = "Home") }
                    IconButton(onClick = { /* Favorite */ }) { Icon(Icons.Filled.Star, contentDescription = "Favorite") }

                    // Floating Action Button di tengah
                    FloatingActionButton(onClick = { /* Add */ }, modifier = Modifier.size(56.dp)) {
                        Text("+")
                    }

                    IconButton(onClick = { /* Chat */ }) { Icon(Icons.Filled.Search, contentDescription = "Chat") }
                    IconButton(onClick = { /* Profile */ }) { Icon(Icons.Filled.LocationOn, contentDescription = "Profile") }
                }
            }
        }

    )
    { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {

            // 1. HEADER BIRU & SEARCH BAR (Top Area)
            item {
                HomeHeader()
            }

            // 2. HIGHLIGHT CARD BESAR (DIBUAT SCROLLABLE HORIZONTAL)
            item {
                HighlightCardRow(
                    places = samplePlacesData.take(3), // Ambil 3 item pertama untuk Highlight Row
                    overlapDistance = overlapDistance
                )
            }

            // 3. JUDUL POPULAR PLACE
            item {
                Text(
                    text = "Nearby Your Location",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    // Terapkan offset yang sama
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .offset(y = -overlapDistance)
                )
            }

            // 4. DAFTAR PLACE CARD KECIL (LazyRow Horizontal)
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.offset(y = -overlapDistance)
                ) {
                    items(samplePlacesData.drop(1)) { place ->
                        SmallPlaceCard(data = place)
                    }
                }
            }

            // Item tambahan lain di dashboard
            item {
                Spacer(modifier = Modifier.height(50.dp).offset(y = -overlapDistance))
                Button(onClick = onLogout, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).offset(y = -overlapDistance)) {
                    Text("Keluar (Logout)")
                }
            }
        }
    }
}

// --- KOMPONEN HEADER ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeHeader() {
    var searchQuery by remember { mutableStateOf("") }
    val density = LocalDensity.current
    val minHeight = 320.dp

    // Root Box untuk Layering dan Background
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(Color(0xFF3C30A8)) // Warna biru tua
            .heightIn(min = minHeight)
    ) {
        // --- 1. KONTEN (Search Bar dan Teks Discover) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .zIndex(1f)
        ) {
            // Teks "Discover"
            Text(
                text = "Discover",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = Color.White, // Ganti Black menjadi White agar terlihat di background biru
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Search Bar UTUH
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        "Search anything, explore everything",
                        color = Color.Gray.copy(alpha = 0.8f)
                    )
                },
                trailingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search Icon",
                        tint = Color.Gray
                    )
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(30.dp)),

                singleLine = true,

                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
        }
    }
}

// --- KOMPONEN BARU: LAZY ROW UNTUK HIGHLIGHT CARD ---
@Composable
fun HighlightCardRow(
    places: List<PlaceData>,
    overlapDistance: Dp
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            // Terapkan offset di sini untuk menarik seluruh baris ke atas
            .offset(y = -overlapDistance),
        horizontalArrangement = Arrangement.spacedBy(16.dp), // Jarak antar kartu
        contentPadding = PaddingValues(horizontal = 16.dp) // Padding di awal dan akhir baris
    ) {
        items(places) { place ->
            HighlightCardItem(data = place) // Panggil item tunggal yang sudah kita buat
        }
    }
}

// --- KOMPONEN HIGHLIGHT CARD ITEM TUNGGAL ---
@Composable
fun HighlightCardItem(
    data: PlaceData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier // Menerima modifier dari LazyRow
            .width(320.dp) // Lebar tetap untuk scrolling horizontal
            .height(250.dp) // Ketinggian Card
            .clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Ganti dengan Image sebenarnya !
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF8B7FC7)) // Warna placeholder
            )

            // Overlay Teks
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                // Lokasi
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text(text = data.location, color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp))
                }

                // Judul
                Text(
                    text = data.title,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Rating dan Review
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color.Yellow)
                    Text(text = data.reviews, color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }
}

// --- KOMPONEN PLACE CARD KECIL ---
@Composable
fun SmallPlaceCard(data: PlaceData) {
    Column(horizontalAlignment = Alignment.Start) {
        // Kartu Utama
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFDCD6F9)) // Warna ungu muda
                .clickable { /* Handle click */ }
        )

        // Teks di bawah kartu
        Text(
            text = data.title,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = 4.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Star, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
            Text(
                text = data.reviews,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}