package com.example.nawasena.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.nawasena.data.model.Review
import com.example.nawasena.data.model.User
import com.example.nawasena.ui.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    destinationId: String,
    viewModel: DetailViewModel,
    User: com.example.nawasena.data.model.User?,
    onBack: () -> Unit
) {
    // Load data saat pertama kali dibuka
    LaunchedEffect(destinationId) {
        viewModel.loadDestination(destinationId)
    }

    val dataState by viewModel.destination.collectAsState()
    val reviews by viewModel.reviews.collectAsState()

    var inputComment by remember { mutableStateOf("") }
    var inputRating by remember { mutableIntStateOf(0) }

    Scaffold(
        // [FIX 1] imePadding agar Scaffold tau ada keyboard
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = Color.White,

        // BOTTOM BAR (Input Review)
        bottomBar = {
            Surface(
                shadowElevation = 12.dp,
                color = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        // [FIX 2] Agar tidak ketutup garis navigasi HP di paling bawah
                        .navigationBarsPadding()
                ) {
                    // Pilihan Bintang
                    Row(
                        modifier = Modifier.padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Beri Nilai:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(end = 8.dp))
                        (1..5).forEach { star ->
                            Icon(
                                imageVector = if (star <= inputRating) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = null,
                                tint = if (star <= inputRating) Color(0xFFFFC107) else Color.LightGray,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { inputRating = star }
                                    .padding(2.dp)
                            )
                        }
                    }

                    // Input Text & Tombol Kirim
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = inputComment,
                            onValueChange = { inputComment = it },
                            placeholder = { Text("Tulis ulasanmu...") },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 50.dp, max = 100.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF5F5F5),
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))

                        // Tombol Kirim
                        Button(
                            onClick = {
                                // [3] PERBAIKAN LOGIC: Kirim object 'user' asli, bukan null
                                viewModel.submitReview(destinationId, User, inputRating, inputComment)
                                inputComment = ""
                                inputRating = 0
                            },
                            // Pastikan user tidak null saat mau kirim (opsional, tapi lebih aman)
                            enabled = inputRating > 0 && inputComment.isNotBlank() && User != null,
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(50.dp),
                            shape = CircleShape
                        ) {
                            Icon(Icons.Filled.Send, contentDescription = "Kirim")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        // Cek apakah data null
        if (dataState == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Gunakan 'let' agar tidak perlu pakai tanda seru (!!) yang berbahaya
            dataState?.let { data ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {

                    // 1. GAMBAR HEADER
                    item {
                        Box(modifier = Modifier.height(320.dp).fillMaxWidth()) {
                            AsyncImage(
                                model = data.imageUrl,
                                contentDescription = data.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Gradient Hitam Tipis di Atas (Agar tombol back terlihat)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Black.copy(0.6f), Color.Transparent)
                                        )
                                    )
                            )

                            // Tombol Back
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .statusBarsPadding() // [FIX 4] Agar tidak ketutup Jam/Status Bar
                                    .padding(16.dp)
                                    .background(Color.White.copy(0.3f), CircleShape)
                            ) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        }
                    }

                    // 2. INFO DESTINASI
                    item {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // Judul
                            Text(
                                text = data.name,
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Rating & Views
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                                Text(
                                    text = " ${String.format("%.1f", data.rating)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = " (${data.reviewCount} ulasan) • ${data.viewCount}x dilihat",
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFEEEEEE))

                            // Deskripsi
                            Text(
                                text = "Tentang Tempat Ini",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = data.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF666666),
                                lineHeight = 22.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Divider(thickness = 8.dp, color = Color(0xFFF9F9F9))
                    }

                    // 3. HEADER KOMENTAR
                    item {
                        Text(
                            text = "Ulasan Pengunjung",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                        )
                    }

                    // 4. LIST KOMENTAR
                    if (reviews.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Jadilah yang pertama memberi ulasan!", color = Color.Gray)
                            }
                        }
                    } else {
                        items(reviews) { review ->
                            ReviewItemCard(review)
                        }
                    }
                }
            }
        }
    }
}

// [FIX 5] Komponen Item Review yang Lebih Cantik
@Composable
fun ReviewItemCard(review: Review) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            // Avatar Inisial
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = review.userName.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                // Nama User & Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = review.userName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    // Bintang Kecil
                    Row {
                        repeat(review.rating) {
                            Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                        }
                    }
                }

                // Isi Komentar
                Text(
                    text = review.comment,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF444444),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        Divider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(top = 16.dp))
    }
}