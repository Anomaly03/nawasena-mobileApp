package com.example.nawasena.ui.screen

import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
// IMPORT PENTING: Model dan ViewModel
import com.example.nawasena.data.model.Destination
import com.example.nawasena.ui.viewmodel.DashboardViewModel

import coil.compose.AsyncImage


// --- DASHBOARD SCREEN UTAMA ---
@Composable
fun DashboardScreen(
    user: Any? = null,
    viewModel: DashboardViewModel,
    currentLat: Double = 0.0,
    currentLong: Double = 0.0,
    onLogout: () -> Unit = {},
    onDestinationClick: (String) -> Unit
) {
    // [INTEGRASI 3] Ambil data Real-time dari ViewModel
    val destinations by viewModel.uiDestinations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Variabel UI
    val overlapDistance = 150.dp

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon Search
                    IconButton(
                        onClick = { /* Handle search */ },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Icon Home (dengan background ungu)
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF6B5DD3)) // Warna ungu
                            .clickable { onDestinationClick("dashboard") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Home",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Icon Favorite (Heart)
                    IconButton(
                        onClick = { /* Handle favorite */ },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Icon Profile
                    IconButton(
                        onClick = { onDestinationClick("profile") },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {

            // 1. HEADER & SEARCH BAR
            item {
                // State lokal untuk teks search bar
                var query by remember { mutableStateOf("") }

                HomeHeader(
                    query = query,
                    onQueryChange = { newText ->
                        query = newText
                        viewModel.search(newText) // [INTEGRASI 4] Panggil fungsi search
                    }
                )
            }

            // Cek apakah data sudah siap
            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                // 2. HIGHLIGHT CARD (Ambil 3 data teratas)
                item {
                    if (destinations.isNotEmpty()) {
                        HighlightCardRow(
                            places = destinations.take(3),
                            overlapDistance = overlapDistance,
                            userLat = currentLat,
                            userLong = currentLong,
                            onClick = onDestinationClick
                        )
                    }
                }

                // 3. JUDUL POPULAR
                item {
                    Text(
                        text = "Nearby Your Location",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                            .offset(y = -overlapDistance)
                    )
                }

                // 4. DAFTAR CARD KECIL (Sisanya)
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.offset(y = -overlapDistance)
                    ) {
                        // Tampilkan semua data (atau drop(3) jika mau beda)
                        items(destinations) { place ->
                            // [INTEGRASI 5] Hitung Jarak
                            val distanceText = calculateDistance(currentLat, currentLong, place.latitude, place.longitude)

                            SmallPlaceCard(
                                data = place,
                                distance = distanceText,
                                onClick = { onDestinationClick(place.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- FUNGSI HEADER (Updated) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeHeader(
    query: String, // Terima value dari luar
    onQueryChange: (String) -> Unit // Callback saat ngetik
) {
    val minHeight = 320.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(Color(0xFF3C30A8))
            .heightIn(min = minHeight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .zIndex(1f)
        ) {
            Text(
                text = "Discover",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Search Bar
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange, // Hubungkan ke ViewModel
                placeholder = {
                    Text("Search places, keywords...", color = Color.Gray.copy(alpha = 0.8f))
                },
                trailingIcon = { Icon(Icons.Filled.Search, "Search", tint = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(30.dp)),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.Black
                )
            )
        }
    }
}

// --- HIGHLIGHT CARD ROW (Updated) ---
@Composable
fun HighlightCardRow(
    places: List<Destination>, // Pakai Destination
    overlapDistance: Dp,
    userLat: Double,
    userLong: Double,
    onClick: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = -overlapDistance),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(places) { place ->
            val distance = calculateDistance(userLat, userLong, place.latitude, place.longitude)
            HighlightCardItem(data = place, distance = distance, onClick = { onClick(place.id) })
        }
    }
}

// --- HIGHLIGHT CARD ITEM (Updated) ---
@Composable
fun HighlightCardItem(
    data: Destination, // Pakai Destination
    distance: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(320.dp)
            .height(250.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }, // Bisa diklik
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Placeholder Gambar (Nanti ganti pakai Coil: AsyncImage)
            AsyncImage(
                model = data.imageUrl, // Ambil URL dari database
                contentDescription = data.name,
                contentScale = ContentScale.Crop, // Agar gambar full memenuhi kartu
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray) // Warna loading sementara
            )

            // Overlay Teks
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                // Lokasi & Jarak
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text(
                        text = "${data.location} • $distance",
                        color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Judul
                Text(text = data.name, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(8.dp))

                // Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, tint = Color.Yellow)
                    Text(
                        text = "%.1f (%d reviews)".format(data.rating, data.reviewCount), // Format Rating
                        color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

// --- SMALL PLACE CARD (Updated) ---
@Composable
fun SmallPlaceCard(
    data: Destination, // Pakai Destination
    distance: String,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.clickable { onClick() }) {
        // Gambar
        AsyncImage(
            model = data.imageUrl,
            contentDescription = data.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(120.dp)
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Gray)
                .clickable { onClick() }
        )

        // Judul
        Text(
            text = data.name,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            modifier = Modifier.padding(top = 4.dp).width(120.dp)
        )

        // Rating & Jarak
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Star, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
            Text(text = "%.1f".format(data.rating), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = distance, style = MaterialTheme.typography.labelSmall, color = Color.Blue)
        }
    }
}

// --- FUNGSI HITUNG JARAK (Helper) ---
fun calculateDistance(startLat: Double, startLng: Double, endLat: Double, endLng: Double): String {
    if (startLat == 0.0 && startLng == 0.0) return "-"

    val results = FloatArray(1)
    Location.distanceBetween(startLat, startLng, endLat, endLng, results)
    val distanceInMeters = results[0]

    return if (distanceInMeters >= 1000) {
        String.format("%.1f km", distanceInMeters / 1000)
    } else {
        "${distanceInMeters.toInt()} m"
    }
}