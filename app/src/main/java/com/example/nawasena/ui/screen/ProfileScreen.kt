package com.example.nawasena.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nawasena.data.model.User

@Composable
fun ProfileScreen(
    user: User?,
    onNavigateToHome: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToFavorite: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            ProfileBottomBar(
                onSearchClick = onNavigateToSearch,
                onHomeClick = onNavigateToHome,
                onFavoriteClick = onNavigateToFavorite
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header dengan background ungu
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(
                        color = Color(0xFF3C30A8),
                        shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Username di atas
                    Text(
                        text = "$${user?.name ?: "Username"}",
                        color = Color.White,
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Light),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(80.dp),
                            tint = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Form Fields
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileTextField(
                    icon = Icons.Outlined.Person,
                    placeholder = "Nama", // GANTI
                    value = user?.name ?: "" // GANTI
                )

                ProfileTextField(
                    icon = Icons.Outlined.DateRange,
                    placeholder = "DD-MM-YY", // GANTI
                    value = "" // GANTI
                )

                ProfileTextField(
                    icon = Icons.Outlined.Phone,
                    placeholder = "081391742759", // GANTI
                    value = "" // GANTI
                )

                ProfileTextField(
                    icon = Icons.Outlined.Email,
                    placeholder = "ub.ac.id", // GANTI
                    value = "" // GANTI
                )

                ProfileTextField(
                    icon = Icons.Outlined.AccountCircle,
                    placeholder = "@shinysole_", // GANTI
                    value = "" // GANTI
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Edit Profile Button
            Button(
                onClick = { /* Handle edit profile */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3C30A8)
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "Edit Profile",
                    color = Color.White,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                )
            }
        }
    }
}

@Composable
fun ProfileTextField(
    icon: ImageVector,
    placeholder: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF6B5DD3),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = if (value.isNotEmpty()) value else placeholder,
            color = if (value.isNotEmpty()) Color.Black else Color.Gray,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
            color = Color.LightGray
        )
    }

    Divider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 1.dp,
        color = Color.LightGray
    )
}

@Composable
fun ProfileBottomBar(
    onSearchClick: () -> Unit,
    onHomeClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
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
            // Search
            IconButton(onClick = onSearchClick, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Home
            IconButton(onClick = onHomeClick, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Home",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Favorite
            IconButton(onClick = onFavoriteClick, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Profile (Active - dengan background)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF6B5DD3)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}