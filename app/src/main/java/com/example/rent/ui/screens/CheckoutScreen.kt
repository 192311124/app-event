package com.example.rent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rent.ui.components.GlassCard
import com.example.rent.ui.theme.*
import com.example.rent.ui.viewmodel.MainViewModel

@Composable
fun CheckoutScreen(
    viewModel: MainViewModel,
    onNavigate: (String, Map<String, Any>) -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var address by remember { mutableStateOf("Flat 403, Cyber Heights, Gachibowli, Hyderabad - 500032") }
    var phone by remember { mutableStateOf("+91 98765 43210") }

    val firstCartItem = cartItems.firstOrNull()
    var deliveryDate by remember { mutableStateOf(firstCartItem?.date ?: "2026-08-01") }
    var timeSlot by remember { mutableStateOf(firstCartItem?.time ?: "10:00 AM") }
    var isSubmitting by remember { mutableStateOf(false) }

    val itemsSubtotal = cartItems.sumOf { it.price * it.quantity }
    val total = if (itemsSubtotal > 0) itemsSubtotal + 499.0 else 0.0

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
            Text("Checkout & Order Confirmation", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("1. Event Venue Delivery Coordinates", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
        Spacer(modifier = Modifier.height(8.dp))
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                val checkoutTextFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    focusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                    focusedContainerColor = BgSecondary,
                    unfocusedContainerColor = BgSecondary,
                    focusedBorderColor = PrimaryGold,
                    unfocusedBorderColor = GlassBorder,
                    cursorColor = Color.White
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Full Venue Address") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryGold) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                    colors = checkoutTextFieldColors,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Phone Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryGold) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                    colors = checkoutTextFieldColors,
                    singleLine = true
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Text("2. Delivery Schedule & Time Slot", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
        Spacer(modifier = Modifier.height(8.dp))
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Delivery Date: $deliveryDate", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Assembly Time: $timeSlot", fontSize = 12.sp, color = TextSecondary)
                }
                Icon(Icons.Default.AccessTimeFilled, contentDescription = null, tint = PrimaryGold)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Text("3. Confirm & Place Order", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
        Spacer(modifier = Modifier.height(8.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text("Order Summary", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                Spacer(modifier = Modifier.height(8.dp))

                if (cartItems.isNotEmpty()) {
                    cartItems.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${item.name} (x${item.quantity})", fontSize = 12.sp, color = TextPrimary)
                            }
                            Text("₹${(item.price * item.quantity).toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Delivery & Setup Fee", fontSize = 12.sp, color = TextMuted)
                        Text("₹499", fontSize = 12.sp, color = TextMuted)
                    }
                } else {
                    Text("No items in cart", fontSize = 12.sp, color = TextMuted)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = GlassBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Payable Amount", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("₹${total.toInt()}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryGold)
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val uid = userProfile?.userId ?: "user_guest"
                        isSubmitting = true
                        val itemsData = cartItems.map { c ->
                            mapOf(
                                "itemId" to c.itemId,
                                "name" to c.name,
                                "price" to c.price,
                                "quantity" to c.quantity,
                                "image" to c.image
                            )
                        }
                        viewModel.addBooking(
                            uid,
                            mapOf(
                                "items" to itemsData,
                                "totalPrice" to total,
                                "address" to address,
                                "phone" to phone,
                                "deliveryDate" to deliveryDate,
                                "timeSlot" to timeSlot,
                                "paymentMethod" to "Payment Confirmed"
                            )
                        ) { bookingId ->
                            isSubmitting = false
                            viewModel.showToast("Order Placed Successfully! 🎉 Booking ID: $bookingId")
                            onNavigate("bookings", emptyMap())
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = BgPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isSubmitting && cartItems.isNotEmpty()
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = BgPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Place Order 🛍️", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}
