package com.example.rent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.rent.ui.components.GlassCard
import com.example.rent.ui.theme.*
import com.example.rent.ui.viewmodel.MainViewModel

@Composable
fun WishlistScreen(
    viewModel: MainViewModel,
    onNavigate: (String, Map<String, Any>) -> Unit
) {
    val items by viewModel.items.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()
    val wishlistedItems = items.filter { wishlistIds.contains(it.id) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onNavigate("back", emptyMap()) }) {
                Icon(Icons.Default.ArrowBackIos, contentDescription = "Back", tint = PrimaryGold)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text("Saved Wishlist Folder", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (wishlistedItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("❤️", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Your Wishlist is Empty", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Tap the heart icon on any rental item to save it here", color = TextMuted, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { onNavigate("items", emptyMap()) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = BgPrimary)
                    ) {
                        Text("Explore Catalog")
                    }
                }
            }
        } else {
            LazyColumn {
                items(wishlistedItems) { item ->
                    GlassCard(
                        onClick = { onNavigate("item-details", mapOf("itemId" to item.id)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = item.image,
                                contentDescription = item.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(MaterialTheme.shapes.medium)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("₹${item.price.toInt()}", color = PrimaryGold, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { viewModel.toggleWishlist(null, item.id) }) {
                                Icon(Icons.Default.Favorite, contentDescription = null, tint = ErrorRed)
                            }
                        }
                    }
                }
            }
        }
    }
}
