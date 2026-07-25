package com.example.rent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rent.ui.components.GlassCard
import com.example.rent.ui.theme.*

@Composable
fun OnboardingScreen(
    onNavigate: (String, Map<String, Any>) -> Unit
) {
    var activeSlide by remember { mutableIntStateOf(0) }

    val slides = listOf(
        mapOf(
            "title" to "Elevate Your Events with Royal Decor",
            "desc" to "Discover 30+ event categories & premium rental setups delivered & assembled seamlessly across India.",
            "icon" to "✨"
        ),
        mapOf(
            "title" to "AI Venue Decoration Recommendations",
            "desc" to "Upload photos of your venue to get smart theme suggestions, budget estimates in ₹, and matching rental items.",
            "icon" to "📸"
        ),
        mapOf(
            "title" to "Flexible Rental Slots & Instant Trackers",
            "desc" to "Book lights, sound systems, stages, and sofas with guaranteed slot timings and live order tracking.",
            "icon" to "📅"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Brand Header
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(PrimaryGold.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text("✨", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "VibeCraft",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryGold
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Slide GlassCard
        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(slides[activeSlide]["icon"]!!, fontSize = 54.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = slides[activeSlide]["title"]!!,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = slides[activeSlide]["desc"]!!,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Indicators
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    slides.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(8.dp)
                                .width(if (activeSlide == index) 24.dp else 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (activeSlide == index) PrimaryGold else TextMuted.copy(alpha = 0.4f))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onNavigate("auth", mapOf("initialMode" to "signup")) },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = BgPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Get Started - Sign Up Free", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = { onNavigate("auth", mapOf("initialMode" to "signin")) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Sign In Existing Account", color = PrimaryGold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(
            onClick = { onNavigate("home", emptyMap()) }
        ) {
            Text("Skip & Explore Guest Catalog", color = TextMuted)
        }
    }
}
