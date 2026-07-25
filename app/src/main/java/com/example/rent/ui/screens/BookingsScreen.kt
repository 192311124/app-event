package com.example.rent.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rent.ui.components.GlassCard
import com.example.rent.ui.theme.*
import com.example.rent.ui.viewmodel.MainViewModel

@Composable
fun BookingsScreen(
    viewModel: MainViewModel,
    onNavigate: (String, Map<String, Any>) -> Unit
) {
    val bookings by viewModel.bookings.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

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
            Text("My Event Bookings Tracker", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (bookings.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📅", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No Event Bookings Found", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Place a decor rental order to track progress live", color = TextMuted, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { onNavigate("items", emptyMap()) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = BgPrimary)
                    ) {
                        Text("Explore Rental Inventory")
                    }
                }
            }
        } else {
            LazyColumn {
                items(bookings) { b ->
                    val isCancelled = b.status == "Cancelled"
                    GlassCard(
                        borderColor = if (isCancelled) ErrorRed else PrimaryGold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("ID: ${b.bookingId}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryGold)
                                Surface(
                                    color = if (isCancelled) ErrorRed.copy(alpha = 0.2f) else SuccessGreen.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = b.status,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCancelled) ErrorRed else SuccessGreen,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            HorizontalDivider(color = GlassBorder, modifier = Modifier.padding(vertical = 10.dp))

                            Text("${b.items.size} Rental Item(s):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            b.items.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("• ${item.name} (x${item.quantity})", fontSize = 13.sp, color = TextPrimary)
                                    Text("₹${(item.price * item.quantity).toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(b.address, fontSize = 11.sp, color = TextMuted, maxLines = 1)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Event, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delivery: ${b.deliveryDate} (${b.timeSlot})", fontSize = 11.sp, color = TextMuted)
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total: ₹${b.totalPrice.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryGold)
                                if (!isCancelled) {
                                    OutlinedButton(
                                        onClick = {
                                            val uid = userProfile?.userId ?: ""
                                            if (uid.isNotEmpty()) viewModel.cancelBooking(uid, b.bookingId)
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                                        border = BorderStroke(1.dp, ErrorRed),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text("Cancel Order", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
