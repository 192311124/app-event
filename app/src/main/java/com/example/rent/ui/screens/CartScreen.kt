package com.example.rent.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.rent.ui.components.GlassCard
import com.example.rent.ui.theme.*
import com.example.rent.ui.viewmodel.MainViewModel

@Composable
fun CartScreen(
    viewModel: MainViewModel,
    onNavigate: (String, Map<String, Any>) -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val deliveryFee = if (subtotal > 0) 499.0 else 0.0
    val total = subtotal + deliveryFee

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onNavigate("back", emptyMap()) }) {
                    Icon(Icons.Default.ArrowBackIos, contentDescription = "Back", tint = PrimaryGold)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text("Shopping Cart Summary", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
            }
            if (cartItems.isNotEmpty()) {
                IconButton(onClick = {
                    val uid = userProfile?.userId ?: ""
                    if (uid.isNotEmpty()) viewModel.clearCart(uid)
                }) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = ErrorRed)
                }
            }
        }

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛒", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Your Cart is Empty", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Select rental decor items to build your event order", color = TextMuted, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { onNavigate("items", emptyMap()) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = BgPrimary)
                    ) {
                        Text("Browse Inventory")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(cartItems) { item ->
                    GlassCard(
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
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Slot: ${item.date} | ${item.time}", fontSize = 11.sp, color = TextMuted)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("₹${(item.price * item.quantity).toInt()}", color = PrimaryGold, fontWeight = FontWeight.Bold)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = {
                                    val uid = userProfile?.userId ?: ""
                                    if (item.quantity > 1) {
                                        viewModel.updateCartQuantity(uid, item.docId, item.quantity - 1)
                                    } else {
                                        viewModel.removeFromCart(uid, item.docId)
                                    }
                                }) {
                                    Icon(Icons.Default.RemoveCircleOutline, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(20.dp))
                                }
                                Text("${item.quantity}", fontWeight = FontWeight.Bold)
                                IconButton(onClick = {
                                    val uid = userProfile?.userId ?: ""
                                    viewModel.updateCartQuantity(uid, item.docId, item.quantity + 1)
                                }) {
                                    Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }

            Surface(
                color = Color(0xFA161226),
                border = BorderStroke(1.dp, GlassBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", color = TextSecondary)
                        Text("₹${subtotal.toInt()}", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Setup & Delivery Fee", color = TextSecondary)
                        Text("₹${deliveryFee.toInt()}", fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = GlassBorder, modifier = Modifier.padding(vertical = 10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Grand Total", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                        Text("₹${total.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryGold)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { onNavigate("checkout", mapOf("totalPrice" to total)) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = BgPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Proceed to Checkout 💳", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }
}
