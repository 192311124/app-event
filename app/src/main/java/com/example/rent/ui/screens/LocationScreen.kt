package com.example.rent.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rent.ui.components.GlassCard
import com.example.rent.ui.theme.*
import com.example.rent.ui.viewmodel.MainViewModel

private data class PincodeArea(val pincode: String, val area: String, val city: String)

@Composable
fun LocationScreen(
    viewModel: MainViewModel,
    onNavigate: (String, Map<String, Any>) -> Unit
) {
    val currentLocation by viewModel.currentLocation.collectAsState()
    var searchInput by remember { mutableStateOf("") }

    val popularPincodes = listOf(
        PincodeArea("500081", "Hitech City / Madhapur", "Hyderabad"),
        PincodeArea("500032", "Gachibowli / Financial Dist", "Hyderabad"),
        PincodeArea("500001", "Abids / Koti", "Hyderabad"),
        PincodeArea("500033", "Jubilee Hills", "Hyderabad"),
        PincodeArea("560001", "MG Road / Indiranagar", "Bengaluru"),
        PincodeArea("560100", "Electronic City", "Bengaluru"),
        PincodeArea("400001", "Fort / Colaba", "Mumbai"),
        PincodeArea("400050", "Bandra West", "Mumbai"),
        PincodeArea("110001", "Connaught Place", "Delhi NCR"),
        PincodeArea("122001", "DLF Cyber City / Gurgaon", "Delhi NCR"),
        PincodeArea("600001", "George Town", "Chennai"),
        PincodeArea("411001", "Shivajinagar", "Pune")
    )

    val availableCities = listOf("Hyderabad", "Bengaluru", "Mumbai", "Delhi NCR", "Chennai", "Pune", "Kolkata", "Ahmedabad")

    // Filter cities or pincodes based on user input
    val trimmedInput = searchInput.trim().lowercase()

    val matchedPincodes = popularPincodes.filter {
        it.pincode.contains(trimmedInput) ||
                it.area.lowercase().contains(trimmedInput) ||
                it.city.lowercase().contains(trimmedInput)
    }

    val matchedCities = availableCities.filter {
        it.lowercase().contains(trimmedInput)
    }

    val isCustomPincode = trimmedInput.length == 6 && trimmedInput.all { it.isDigit() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onNavigate("back", emptyMap()) }) {
                Icon(Icons.Default.ArrowBackIos, contentDescription = "Back", tint = PrimaryGold)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Select Delivery Location 📍",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Enter your 6-digit Pincode or choose your city for event decoration delivery",
            fontSize = 12.sp,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Pincode Search Bar
        OutlinedTextField(
            value = searchInput,
            onValueChange = { searchInput = it },
            placeholder = { Text("Enter 6-Digit Pincode (e.g. 500081) or area...", color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.PinDrop, contentDescription = null, tint = PrimaryGold) },
            trailingIcon = {
                if (searchInput.isNotEmpty()) {
                    IconButton(onClick = { searchInput = "" }) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = TextMuted)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
            colors = OutlinedTextFieldDefaults.colors(
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
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Live Custom Pincode Match Alert
        if (isCustomPincode) {
            val customPincodeArea = popularPincodes.firstOrNull { it.pincode == trimmedInput }
            val displayLocation = if (customPincodeArea != null) {
                "${customPincodeArea.area}, ${customPincodeArea.city} (${customPincodeArea.pincode})"
            } else {
                "Pincode $trimmedInput (Serviceable Area)"
            }

            Surface(
                color = SuccessGreen.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, SuccessGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.setLocation(displayLocation, null)
                        viewModel.showToast("Location updated to $displayLocation!")
                        onNavigate("home", emptyMap())
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Service Available!", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                        Text(displayLocation, fontSize = 12.sp, color = TextPrimary)
                    }
                    Text("Select ➔", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Available Locations List
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            // Pincodes Section
            if (matchedPincodes.isNotEmpty() && searchInput.isNotBlank()) {
                item {
                    Text("Matching Pincodes & Areas:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(matchedPincodes) { pin ->
                    val pinText = "${pin.area}, ${pin.city} (${pin.pincode})"
                    val isSelected = currentLocation.contains(pin.pincode) || currentLocation == pin.city

                    GlassCard(
                        borderColor = if (isSelected) PrimaryGold else GlassBorder,
                        onClick = {
                            viewModel.setLocation(pinText, null)
                            viewModel.showToast("Selected $pinText!")
                            onNavigate("home", emptyMap())
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${pin.pincode} - ${pin.area}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(pin.city, fontSize = 11.sp, color = TextSecondary)
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryGold)
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(14.dp)) }
            }

            // Cities Section
            item {
                Text("Select Service City:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(matchedCities) { city ->
                val isSelected = currentLocation == city
                GlassCard(
                    borderColor = if (isSelected) PrimaryGold else GlassBorder,
                    onClick = {
                        viewModel.setLocation(city, null)
                        viewModel.showToast("Service location updated to $city!")
                        onNavigate("home", emptyMap())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) PrimaryGold.copy(alpha = 0.2f) else BgSecondary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.LocationCity, contentDescription = null, tint = if (isSelected) PrimaryGold else TextMuted, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = city,
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) PrimaryGold else TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryGold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                if (searchInput.isNotBlank()) {
                    viewModel.setLocation(searchInput, null)
                }
                onNavigate("home", emptyMap())
            },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = BgPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Confirm Location: $currentLocation", fontWeight = FontWeight.Bold)
        }
    }
}
