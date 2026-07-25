package com.example.rent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.rent.ui.components.GlassCard
import com.example.rent.ui.theme.*
import com.example.rent.ui.viewmodel.MainViewModel

@Composable
fun CategoriesScreen(
    viewModel: MainViewModel,
    onNavigate: (String, Map<String, Any>) -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    val items by viewModel.items.collectAsState()
    val routeParams by viewModel.routeParams.collectAsState()
    val activeCatId = routeParams["catId"] as? String

    if (activeCatId != null) {
        val category = categories.firstOrNull { it.id == activeCatId } ?: categories.firstOrNull()
        val matchingItems = items.filter { item ->
            category != null && (item.category.lowercase().contains(category.name.lowercase()) ||
                    category.name.lowercase().contains(item.category.lowercase()))
        }

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
                Text(
                    text = "${category?.icon ?: "🎉"} ${category?.name ?: "Category"} Setup",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text(category?.icon ?: "🎉", fontSize = 44.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(category?.name ?: "", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(category?.desc ?: "", fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.Center)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Text("Available Packages (${if (matchingItems.isNotEmpty()) matchingItems.size else items.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(10.dp))

            val displayList = if (matchingItems.isNotEmpty()) matchingItems else items.take(5)

            LazyColumn {
                items(displayList) { item ->
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
                                    .size(64.dp)
                                    .clip(MaterialTheme.shapes.medium)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("₹${item.price.toInt()}", color = PrimaryGold, fontWeight = FontWeight.Bold)
                            }
                            Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
        return
    }

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
            Text("30 Event Categories Catalog", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Column {
            categories.chunked(2).forEach { rowCats ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowCats.forEach { cat ->
                        Box(modifier = Modifier.weight(1f)) {
                            GlassCard(
                                onClick = { onNavigate("category-details", mapOf("catId" to cat.id)) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(cat.icon, fontSize = 32.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(cat.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary, textAlign = TextAlign.Center)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(cat.desc, fontSize = 10.sp, color = TextMuted, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                    if (rowCats.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
