package com.example.rent.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.rent.data.model.RentalItem
import com.example.rent.ui.components.GlassCard
import com.example.rent.ui.theme.*
import com.example.rent.ui.viewmodel.MainViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailsScreen(
    viewModel: MainViewModel,
    onNavigate: (String, Map<String, Any>) -> Unit
) {
    val items by viewModel.items.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()
    val routeParams by viewModel.routeParams.collectAsState()
    val itemId = routeParams["itemId"] as? String

    val item = items.firstOrNull { it.id == itemId } ?: items.firstOrNull() ?: RentalItem(
        id = "led_lights",
        name = "LED Lights",
        description = "High-brightness RGB LED wash lights. Perfect for coloring walls, stages, and outdoor canopies.",
        price = 499.0,
        availability = true,
        rating = 4.8,
        category = "Lighting",
        image = "https://images.unsplash.com/photo-1563245372-f21724e3856d?auto=format&fit=crop&w=400&q=80"
    )

    var quantity by remember { mutableIntStateOf(1) }
    val today = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf(today.toString()) }
    var selectedTime by remember { mutableStateOf("10:00 AM") }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    val quickDates = remember {
        (0..6).map { days ->
            today.plusDays(days.toLong()).toString()
        }
    }

    val timeSlots = listOf("08:00 AM", "10:00 AM", "12:00 PM", "02:00 PM", "05:00 PM", "08:00 PM")

    val cartItems by viewModel.cartItems.collectAsState()
    var justAdded by remember { mutableStateOf(false) }
    val isWishlisted = wishlistIds.contains(item.id)
    val isInCart = cartItems.any { it.itemId == item.id }

    val datePickerState = rememberDatePickerState()

    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            selectedDate = date.toString()
                        }
                        showDatePickerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = BgPrimary)
                ) {
                    Text("Confirm Date", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        bottomBar = {
            Surface(
                color = Color(0xFA161226),
                border = BorderStroke(1.dp, GlassBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Price", fontSize = 11.sp, color = TextMuted)
                        Text("₹${(item.price * quantity).toInt()}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryGold)
                    }
                    Button(
                        onClick = {
                            viewModel.addToCart(null, item, quantity, selectedDate, selectedTime)
                            justAdded = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isInCart || justAdded) SuccessGreen else PrimaryGold,
                            contentColor = if (isInCart || justAdded) Color.White else BgPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isInCart || justAdded) "Added to Cart ✓" else "Add to Cart 🛒", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgPrimary)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Box {
                AsyncImage(
                    model = item.image,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )
                IconButton(
                    onClick = { onNavigate("back", emptyMap()) },
                    modifier = Modifier.padding(12.dp)
                ) {
                    Surface(shape = CircleShape, color = BgPrimary.copy(alpha = 0.8f)) {
                        Box(modifier = Modifier.padding(8.dp)) {
                            Icon(Icons.Default.ArrowBackIos, contentDescription = "Back", tint = PrimaryGold, modifier = Modifier.size(18.dp))
                        }
                    }
                }
                IconButton(
                    onClick = { viewModel.toggleWishlist(null, item.id) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    Surface(shape = CircleShape, color = BgPrimary.copy(alpha = 0.8f)) {
                        Box(modifier = Modifier.padding(8.dp)) {
                            Icon(
                                imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isWishlisted) ErrorRed else PrimaryGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1f))
                    Text("₹${item.price.toInt()}", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryGold)
                }
                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = PrimaryGold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(item.category, fontSize = 11.sp, color = PrimaryGold, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Star, contentDescription = null, tint = LightGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${item.rating} (${item.reviews.size} reviews)", fontSize = 12.sp, color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("Description & Specification", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(item.description, color = TextSecondary, fontSize = 13.sp, lineHeight = 18.sp)
                Spacer(modifier = Modifier.height(20.dp))

                // Slot Pickers Card
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Select Delivery Date & Time Slot", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryGold)
                            Surface(
                                onClick = { showDatePickerDialog = true },
                                color = PrimaryGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, PrimaryGold)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.EditCalendar, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Calendar 📅", fontSize = 11.sp, color = PrimaryGold, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        // Active Date Display Box (Clickable)
                        Surface(
                            onClick = { showDatePickerDialog = true },
                            color = BgSecondary,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, GlassBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Selected Delivery Date", fontSize = 10.sp, color = TextMuted)
                                        Text(selectedDate, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text("Tap to Pick Date ➔", fontSize = 11.sp, color = PrimaryGold, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Quick Date Selection:", fontSize = 12.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(6.dp))

                        // Horizontal Quick Date Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(quickDates) { dStr ->
                                val isSel = selectedDate == dStr
                                FilterChip(
                                    selected = isSel,
                                    onClick = { selectedDate = dStr },
                                    label = { Text(dStr, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryGold,
                                        selectedLabelColor = BgPrimary,
                                        containerColor = BgSecondary,
                                        labelColor = TextPrimary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Available Delivery Time Slots:", fontSize = 12.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(6.dp))

                        // Time Slots Grid Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            timeSlots.take(3).forEach { slot ->
                                val isSel = selectedTime == slot
                                FilterChip(
                                    selected = isSel,
                                    onClick = { selectedTime = slot },
                                    label = { Text(slot, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryGold,
                                        selectedLabelColor = BgPrimary,
                                        containerColor = BgSecondary,
                                        labelColor = TextPrimary
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            timeSlots.drop(3).take(3).forEach { slot ->
                                val isSel = selectedTime == slot
                                FilterChip(
                                    selected = isSel,
                                    onClick = { selectedTime = slot },
                                    label = { Text(slot, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryGold,
                                        selectedLabelColor = BgPrimary,
                                        containerColor = BgSecondary,
                                        labelColor = TextPrimary
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quantity Modifier
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Quantity:", fontWeight = FontWeight.Bold, color = TextPrimary)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { if (quantity > 1) quantity-- }) {
                                    Icon(Icons.Default.RemoveCircleOutline, contentDescription = null, tint = PrimaryGold)
                                }
                                Text("$quantity", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                                IconButton(onClick = { quantity++ }) {
                                    Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = PrimaryGold)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                // Reviews Section
                if (item.reviews.isNotEmpty()) {
                    Text("Customer Reviews", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    item.reviews.forEach { rev ->
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(rev.user, fontWeight = FontWeight.Bold, color = PrimaryGold, fontSize = 13.sp)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = LightGold, modifier = Modifier.size(14.dp))
                                        Text("${rev.rating}", fontSize = 12.sp, color = TextPrimary)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(rev.comment, fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
