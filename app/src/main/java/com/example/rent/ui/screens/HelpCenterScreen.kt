package com.example.rent.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(val sender: String, val text: String)

@Composable
fun HelpCenterScreen(
    onNavigate: ((String, Map<String, Any>) -> Unit)? = null
) {
    var messageText by remember { mutableStateOf("") }
    val chatMessages = remember {
        mutableStateListOf(
            ChatMessage("assistant", "Hello! 👋 I am your VibeCraft AI Event Planner. Ask me anything about decor themes, item pricing in ₹ INR, package availability, or booking rules!")
        )
    }
    var isSending by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val quickQuestions = listOf(
        "✨ Wedding Stage Prices",
        "🌼 Haldi & Mehendi Decor",
        "🍼 Baby Shower Packages",
        "💡 Stage Lights & FX",
        "📦 Delivery & Assembly"
    )

    fun generateAiReply(userText: String): String {
        val q = userText.lowercase().trim()

        return when {
            q.contains("haldi") || q.contains("mehendi") || q.contains("sangeet") || q.contains("yellow") || q.contains("marigold") ->
                "For Haldi & Mehendi events, we recommend:\n• Marigold Flower Wall Garland Panel (₹5,200/day)\n• Dual-Tone Yellow Satin Drapes (₹2,900)\n• Traditional Brass Samai Diyas (₹2,500)\n• Brass Urlis for Floating Petals (₹3,400)\n\nTip: You can view these directly in the Catalog tab!"

            q.contains("wedding") || q.contains("reception") || q.contains("mandap") || q.contains("marriage") || q.contains("bride") || q.contains("groom") || q.contains("throne") ->
                "For Weddings & Receptions, we offer:\n• Royal Rajputana Golden Mandap Canopy (₹18,500/day)\n• Crystal Chandelier Stage Canopy (₹12,000/day)\n• Red Crimson Velvet Entrance Arch (₹8,500)\n• Golden High-Back Throne Sofas (₹14,000/pair)\n• Red Velvet Carpet Runner 30ft (₹2,800)"

            q.contains("baby") || q.contains("infant") || q.contains("cradle") || q.contains("shower") || q.contains("naming") || q.contains("teddy") ->
                "For Baby Showers & Naming Ceremonies, we recommend:\n• Organic Pastel Cloud Balloon Arch (₹4,500/day)\n• Golden Decorated Cradle Swing (₹6,800/day)\n• Giant 5ft Plush Teddy Bear Prop (₹2,200/day)\n• 'Welcome Little One' Neon Sign (₹2,800)"

            q.contains("birthday") || q.contains("bday") || q.contains("party") || q.contains("neon") || q.contains("sequin") || q.contains("cake") ->
                "For Birthday Celebrations, we recommend:\n• Gold Shimmer Sequin Photobooth Wall (₹6,500/day)\n• Custom Neon Signboard (₹2,800)\n• Geometric Gold Arch Set (₹5,800)\n• Acrylic Cake Plinths (₹3,200/set of 3)"

            q.contains("light") || q.contains("led") || q.contains("fog") || q.contains("spark") || q.contains("laser") || q.contains("edison") || q.contains("uplight") || q.contains("spot") ->
                "We have 15+ Lighting & Special FX rentals!\n• Outdoor Waterproof LED Par Lights (₹4,200/set of 8)\n• Vintage Edison Bulb Strings (₹2,400)\n• Wireless Smart Battery Uplights (₹5,600)\n• RGB Moving Head Wash Lights (₹7,500)\n• Cold Spark Pyros (₹4,800)\n• Heavy Low-Lying Fog Machine (₹3,800)"

            q.contains("furniture") || q.contains("chair") || q.contains("sofa") || q.contains("seating") || q.contains("jhula") || q.contains("bar") || q.contains("pallet") ->
                "Our Event Furniture Rentals include:\n• Golden High-Back Throne Sofas (₹14,000/pair)\n• Carved Wooden Royal Jhula Swing (₹9,800)\n• Velvet Lounge Accent Chairs (₹4,800/pair)\n• Acrylic LED Illuminated Bar Counter (₹9,500)\n• Low Wooden Pallet Seating with Cushions (₹4,200)"

            q.contains("price") || q.contains("cost") || q.contains("rate") || q.contains("budget") || q.contains("cheap") || q.contains("fee") || q.contains("inr") || q.contains("rs") || q.contains("₹") ->
                "Our decoration rentals start from ₹1,500 up to ₹18,500/day. Delivery & professional assembly is a flat ₹499 per order across all locations. All prices are transparent in ₹ INR."

            q.contains("delivery") || q.contains("time") || q.contains("date") || q.contains("slot") || q.contains("setup") || q.contains("assembly") || q.contains("address") || q.contains("location") ->
                "We deliver and professionally assemble all items at your venue address on your selected date and time slot (08:00 AM, 10:00 AM, 12:00 PM, 02:00 PM, 05:00 PM, 08:00 PM)."

            q.contains("book") || q.contains("order") || q.contains("cart") || q.contains("checkout") || q.contains("cancel") || q.contains("track") || q.contains("status") ->
                "To book items:\n1. Browse Catalog or AI Decor tab\n2. Pick Delivery Date & Time Slot\n3. Tap 'Add to Cart' & Go to Checkout\n4. Tap 'Place Order'\n\nYou can track all your orders anytime in the Bookings tab!"

            q.contains("ai") || q.contains("scan") || q.contains("photo") || q.contains("upload") || q.contains("image") || q.contains("recommend") ->
                "Try our AI Decor Scanner! Go to the 'AI Decor' tab, upload any venue, human, baby, or wall photo, and our AI will generate 3 custom decor design models with budget breakdowns."

            q.startsWith("hi") || q.startsWith("hello") || q.startsWith("hey") ->
                "Hello! 👋 I am your VibeCraft AI Event Planner. Ask me anything about event decor, pricing in ₹ INR, delivery setup, or decor suggestions for your occasion!"

            q.contains("thank") || q.contains("thanks") ->
                "You're very welcome! 😊 Feel free to ask if you need more decor suggestions or package recommendations for your event!"

            else ->
                "AI Planner Response for '$userText':\nBased on your question, I recommend exploring our Catalog & AI Decor Scanner tabs! We offer 40+ rental items for Weddings, Birthdays, Haldi, Baby Showers, and Corporate events. Ask me about specific items, pricing in ₹, or delivery slots!"
        }
    }

    fun sendMessage(customText: String? = null) {
        val text = (customText ?: messageText).trim()
        if (text.isEmpty()) return

        chatMessages.add(ChatMessage("user", text))
        if (customText == null) messageText = ""
        isSending = true

        scope.launch {
            delay(1000)
            val reply = generateAiReply(text)
            chatMessages.add(ChatMessage("assistant", reply))
            isSending = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            if (onNavigate != null) {
                IconButton(onClick = { onNavigate("back", emptyMap()) }) {
                    Icon(Icons.Default.ArrowBackIos, contentDescription = "Back", tint = PrimaryGold)
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "💬 AI Planner & Support Assistant",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGold
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        // Quick Suggestion Chips Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            items(quickQuestions) { q ->
                Surface(
                    onClick = { sendMessage(q) },
                    shape = RoundedCornerShape(12.dp),
                    color = BgSecondary,
                    border = BorderStroke(1.dp, GlassBorder)
                ) {
                    Text(
                        text = q,
                        fontSize = 11.sp,
                        color = PrimaryGold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            items(chatMessages) { msg ->
                val isUser = msg.sender == "user"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Surface(
                        color = if (isUser) PrimaryGold else GlassSurface,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, if (isUser) PrimaryGold else GlassBorder)
                    ) {
                        Text(
                            text = msg.text,
                            fontSize = 13.sp,
                            color = if (isUser) BgPrimary else TextPrimary,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }

        if (isSending) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AI Planner is analyzing & typing response...",
                    fontSize = 11.sp,
                    color = PrimaryGold
                )
            }
        }

        Surface(
            color = BgSecondary,
            border = BorderStroke(1.dp, GlassBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Ask AI Planner anything about decor, pricing or setup...", fontSize = 12.sp, color = TextMuted) },
                    modifier = Modifier.weight(1f),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = BgPrimary,
                        unfocusedContainerColor = BgPrimary,
                        focusedBorderColor = PrimaryGold,
                        unfocusedBorderColor = GlassBorder,
                        cursorColor = PrimaryGold
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { sendMessage() },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = PrimaryGold, contentColor = BgPrimary)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null)
                }
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}
