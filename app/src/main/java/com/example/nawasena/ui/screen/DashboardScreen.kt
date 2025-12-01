package com.example.nawasena.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.example.nawasena.data.model.User
import androidx.compose.material3.ExperimentalMaterial3Api

@Composable
fun DashboardScreen(
    user: User?,
    onLogout: () -> Unit,
    onDestinationClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            HomeTopBar()
        },
        bottomBar = {
            Button(
                onClick = { onDestinationClick("Home") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Home")
            }
        }
    ) { padding ->
        // CONTENT UTAMA
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Selamat Datang,", style = MaterialTheme.typography.headlineSmall)
            Text(text = user?.name ?: "Pengguna", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { onDestinationClick("Settings") }) {
                Text("Akses Pengaturan")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onLogout) {
                Text("Keluar (Logout)")
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar() {
    var searchQuery by remember { mutableStateOf("") }

    // Parent container
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(
                color = Color(0xFF3C30A8),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .padding(16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },

            // Placeholder tetap di sini
            placeholder = {
                Text(
                    "Where you up to?",
                    color = Color.Gray.copy(alpha = 0.7f),
                    fontStyle = FontStyle.Italic
                )
            },

            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Icon",
                    tint = Color.Gray.copy(alpha = 0.7f)
                )
            },


            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(30.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.9f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            singleLine = true
        )
        // Label
        Text(
            text = "Top list over 2 month",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Horizontal Scrollable Row for Places
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(5) { index ->
                PlaceCard(placeName = "place${index + 1}")
            }
        }
    }
}
@Composable
fun PlaceCard(placeName: String) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF8B7FC7)) // Warna ungu muda
            .clickable { /* Handle click */ },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = placeName,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}