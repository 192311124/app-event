package com.example.rent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rent.ui.components.GlassCard
import com.example.rent.ui.theme.*
import com.example.rent.ui.viewmodel.MainViewModel

@Composable
fun ItemsCatalogScreen(
    viewModel: MainViewModel,
    onNavigate: (String, Map<String, Any>) -> Unit
) {
    val items by viewModel.items.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()

    var selectedCategory by remember { mutableStateOf("All") }
    val categoriesList = listOf("All", "Lighting", "Balloons", "Floral", "Stages", "Furniture", "Tents & Canopies", "AV & Sound", "Special Effects")

    val query = searchQuery.lowercase()
    val filteredItems = items.filter { item ->
        val matchesQuery = item.name.lowercase().contains(query) ||
                item.description.lowercase().contains(query) ||
                item.category.lowercase().contains(query)

        val matchesCat = selectedCategory == "All" || item.category == selectedCategory
        matchesQuery && matchesCat
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onNavigate("back", emptyMap()) }) {
                Icon(Icons.Default.ArrowBackIos, contentDescription = "Back", tint = PrimaryGold)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text("Rental Items Inventory", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
        }
        Spacer(modifier = Modifier.height(14.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search items by name, tag, category...", color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryGold) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = androidx.compose.ui.text.TextStyle(color = androidx.compose.ui.graphics.Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = androidx.compose.ui.graphics.Color.White,
                unfocusedTextColor = androidx.compose.ui.graphics.Color.White,
                focusedBorderColor = PrimaryGold,
                unfocusedBorderColor = GlassBorder,
                cursorColor = PrimaryGold
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Category Chips Row
        LazyRow {
            items(categoriesList) { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) PrimaryGold else GlassSurface,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { selectedCategory = cat }
                ) {
                    Text(
                        text = cat,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) BgPrimary else TextPrimary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Inventory Grid
        Column {
            filteredItems.chunked(2).forEach { rowItems ->
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
