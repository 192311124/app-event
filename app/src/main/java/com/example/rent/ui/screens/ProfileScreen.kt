package com.example.rent.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
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
import com.example.rent.ui.viewmodel.AuthViewModel
import com.example.rent.ui.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel,
    onNavigate: (String, Map<String, Any>) -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val currentLocation by mainViewModel.currentLocation.collectAsState()
    val bookings by mainViewModel.bookings.collectAsState()
    val notifications by mainViewModel.notifications.collectAsState()

    if (currentUser == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgPrimary)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("👤", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Sign In Required", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("You need to be logged in to view your profile dashboard.", color = TextMuted, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { onNavigate("auth", mapOf("initialMode" to "signin")) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = BgPrimary)
                    ) {
                        Text("Sign In Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

    val username = currentUser?.email?.split("@")?.get(0) ?: "User"

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
            Text("User Profile & Settings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Header Profile Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = PrimaryGold,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = BgPrimary, modifier = Modifier.size(40.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(username, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                Text("@$username", fontSize = 13.sp, color = TextMuted)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Menu Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                ListItem(
                    headlineContent = { Text("Active Service City") },
                    supportingContent = { Text(currentLocation) },
                    leadingContent = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = PrimaryGold) },
                    trailingContent = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp)) },
                    modifier = Modifier.clickable { onNavigate("location", emptyMap()) }
                )
                HorizontalDivider(color = GlassBorder)
                ListItem(
                    headlineContent = { Text("My Event Bookings") },
                    supportingContent = { Text("${bookings.size} Orders") },
                    leadingContent = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = PrimaryGold) },
                    trailingContent = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp)) },
                    modifier = Modifier.clickable { onNavigate("bookings", emptyMap()) }
                )
                HorizontalDivider(color = GlassBorder)
                ListItem(
                    headlineContent = { Text("Notification Hub") },
                    supportingContent = { Text("${notifications.size} Alerts") },
                    leadingContent = { Icon(Icons.Default.Notifications, contentDescription = null, tint = PrimaryGold) },
                    trailingContent = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp)) },
                    modifier = Modifier.clickable {
                        mainViewModel.showToast("You have ${notifications.size} notification alerts.")
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = {
                authViewModel.logout()
                onNavigate("onboarding", emptyMap())
            },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
            border = BorderStroke(1.dp, ErrorRed),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log Out Account")
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}
