package com.example.rent.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rent.ui.theme.BgSecondary
import com.example.rent.ui.theme.ErrorRed
import com.example.rent.ui.theme.PrimaryGold
import com.example.rent.ui.theme.TextMuted

data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun BottomNavBar(
    currentRoute: String,
    cartCount: Int = 0,
    onNavigate: (String) -> Unit
) {
    if (listOf("onboarding", "auth", "location").contains(currentRoute)) {
        return
    }

    val items = listOf(
        NavItem("home", "Home", Icons.Default.Home),
        NavItem("items", "Catalog", Icons.Default.Sell),
        NavItem("recommend", "AI Decor", Icons.Default.AutoAwesome),
        NavItem("cart", "Cart", Icons.Default.ShoppingCart),
        NavItem("bookings", "Bookings", Icons.Default.CalendarToday),
        NavItem("help", "Planner", Icons.Default.ChatBubbleOutline),
        NavItem("profile", "Profile", Icons.Default.Person)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = BgSecondary.copy(alpha = 0.95f),
            border = BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route ||
                            (item.route == "items" && (currentRoute == "categories" || currentRoute == "category-details"))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(item.route) }
                            .padding(vertical = 4.dp)
                    ) {
                        Box {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) PrimaryGold else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            if (item.route == "cart" && cartCount > 0) {
                                Surface(
                                    color = ErrorRed,
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 6.dp, y = (-4).dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = if (cartCount > 99) "99+" else "$cartCount",
                                            color = Color.White,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.label,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) PrimaryGold else TextMuted,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
