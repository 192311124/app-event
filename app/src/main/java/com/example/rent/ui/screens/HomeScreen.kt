package com.example.rent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.rent.data.model.RentalItem
import com.example.rent.ui.components.GlassCard
import com.example.rent.ui.theme.*
import com.example.rent.ui.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigate: (String, Map<String, Any>) -> Unit
) {
    val currentLocation by viewModel.currentLocation.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val items by viewModel.items.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()

    val topCategories = categories.take(8)
    val featuredItems = items.take(6)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "VibeCraft",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PrimaryGold.copy(alpha = 0.15f),
                        modifier = Modifier.clickable { onNavigate("location", emptyMap()) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(currentLocation, fontSize = 11.sp, color = PrimaryGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Text("Explore 30+ Event Decor Categories", fontSize = 12.sp, color = TextSecondary)
            }

            IconButton(onClick = { onNavigate("wishlist", emptyMap()) }) {
                Box {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(26.dp))
                    if (wishlistIds.isNotEmpty()) {
                        Surface(
                            shape = CircleShape,
                            color = ErrorRed,
                            modifier = Modifier
                                .size(14.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("${wishlistIds.size}", fontSize = 8.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search fairy lights, stages, sofas, DJ sound...", fontSize = 13.sp, color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryGold) },
            trailingIcon = {
                IconButton(onClick = { onNavigate("items", emptyMap()) }) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = PrimaryGold)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = androidx.compose.ui.text.TextStyle(color = androidx.compose.ui.graphics.Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = androidx.compose.ui.graphics.Color.White,
                unfocusedTextColor = androidx.compose.ui.graphics.Color.White,
                focusedContainerColor = BgSecondary,
                unfocusedContainerColor = BgSecondary,
                focusedBorderColor = PrimaryGold,
                unfocusedBorderColor = GlassBorder,
                cursorColor = PrimaryGold
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Festive Hero Promo Banner
        GlassCard(
            borderColor = PrimaryGold.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = PrimaryGold,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "FESTIVE OFFER 🪔",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BgPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("15% Off Wedding & Haldi Setups", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Use Code: DIWALI15 at checkout", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onNavigate("items", emptyMap()) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = BgPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Explore Catalog", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("👑", fontSize = 44.sp)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        // AI Decor Scanner Banner Card
        GlassCard(
            onClick = { onNavigate("recommend", emptyMap()) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = PrimaryGold.copy(alpha = 0.15f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("✨", fontSize = 22.sp)
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("AI Venue Scanner", fontWeight = FontWeight.Bold, color = PrimaryGold, fontSize = 15.sp)
                    Text("Scan venue photos for instant theme & budget suggestions", color = TextSecondary, fontSize = 11.sp)
                }
                Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(14.dp))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Categories Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Event Categories (${categories.size})", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            TextButton(onClick = { onNavigate("categories", emptyMap()) }) {
                Text("See All", color = PrimaryGold)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal Categories List
        LazyRow {
            items(topCategories) { cat ->
                GlassCard(
                    onClick = { onNavigate("category-details", mapOf("catId" to cat.id)) },
                    modifier = Modifier
                        .width(84.dp)
                        .padding(end = 10.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(cat.icon, fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = cat.name,
                            fontSize = 11.sp,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Featured Inventory Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Featured Rental Inventory", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            TextButton(onClick = { onNavigate("items", emptyMap()) }) {
                Text("View All", color = PrimaryGold)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        // Grid of Featured Items
        Column {
            featuredItems.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { item ->
                        Box(modifier = Modifier.weight(1f)) {
                            ItemGridCard(
                                item = item,
                                isWishlisted = wishlistIds.contains(item.id),
                                onToggleWishlist = { viewModel.toggleWishlist(null, item.id) },
                                onClick = { onNavigate("item-details", mapOf("itemId" to item.id)) }
                            )
                        }
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun ItemGridCard(
    item: RentalItem,
    isWishlisted: Boolean,
    onToggleWishlist: () -> Unit,
    onClick: () -> Unit
) {
    GlassCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Box {
                AsyncImage(
                    model = item.image,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Surface(
                    shape = CircleShape,
                    color = BgPrimary.copy(alpha = 0.8f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(28.dp)
                        .clickable { onToggleWishlist() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isWishlisted) ErrorRed else PrimaryGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = LightGold, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(2.dp))
                Text("${item.rating}", fontSize = 11.sp, color = TextSecondary)
                Spacer(modifier = Modifier.width(6.dp))
                Text(item.category, fontSize = 10.sp, color = TextMuted, maxLines = 1)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("₹${item.price.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = PrimaryGold)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PrimaryGold,
                    modifier = Modifier.size(22.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = BgPrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
