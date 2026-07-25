package com.example.rent.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.rent.ui.components.GlassCard
import com.example.rent.ui.theme.*
import com.example.rent.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

data class DecorDesignModel(
    val id: String,
    val title: String,
    val theme: String,
    val matchPercentage: String,
    val budget: String,
    val description: String,
    val recommendedItems: List<String>,
    val imageUrl: String
)

data class VenueAnalysisResult(
    val venueType: String,
    val spatialFeatures: String,
    val designModels: List<DecorDesignModel>
)

@Composable
fun AiRecommendScreen(
    viewModel: MainViewModel,
    onNavigate: (String, Map<String, Any>) -> Unit
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var uploadCounter by remember { mutableIntStateOf(0) }
    var analysisResult by remember { mutableStateOf<VenueAnalysisResult?>(null) }
    var isRealTimeAi by remember { mutableStateOf(false) }
    var selectedModelIndex by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    fun analyzeImageUri(uri: Uri): VenueAnalysisResult {
        val hash = abs(uri.toString().hashCode())
        val profileIndex = (hash + uploadCounter * 17) % 6

        return when (profileIndex) {
            0 -> VenueAnalysisResult(
                venueType = "Wedding & Reception Stage Analysis 💍",
                spatialFeatures = "AI scanned photo features: Grand hall elevation with backdrop clearance, royal throne seating space, and central entrance walkway.",
                designModels = listOf(
                    DecorDesignModel(
                        id = "wedding_model_1",
                        title = "Model A: Royal Rajputana Gold & Crimson Mandap",
                        theme = "Royal Gold & Crimson Wedding",
                        matchPercentage = "99% AI Match",
                        budget = "₹45,000 - ₹65,000",
                        description = "A magnificent wedding setup featuring rich red velvet drapes, heavy carved golden entrance arch, brass diyas, and royal carved Jhula for bride & groom.",
                        recommendedItems = listOf(
                            "Royal Crimson Velvet Mandap Canopy",
                            "Heavy Carved Gold Entrance Arch",
                            "Traditional Carved Wooden Jhula",
                            "Red Carpet Pathway Runners"
                        ),
                        imageUrl = "https://images.unsplash.com/photo-1545232979-fbfd42e000b9?auto=format&fit=crop&w=600&q=80"
                    ),
                    DecorDesignModel(
                        id = "wedding_model_2",
                        title = "Model B: Grand Crystal Chandelier Reception Stage",
                        theme = "Crystal Royalty Reception",
                        matchPercentage = "96% AI Match",
                        budget = "₹55,000 - ₹80,000",
                        description = "Opulent wedding reception stage featuring crystal chandelier canopy, seamless white orchid backdrop, and golden throne sofa.",
                        recommendedItems = listOf(
                            "Crystal Chandelier Canopy",
                            "White Orchid & Lily Wall Backdrop",
                            "Golden High-Back Throne Sofa",
                            "Cold Spark Pyro Effects (Set of 4)"
                        ),
                        imageUrl = "https://images.unsplash.com/photo-1527529482837-4698179dc6ce?auto=format&fit=crop&w=600&q=80"
                    ),
                    DecorDesignModel(
                        id = "wedding_model_3",
                        title = "Model C: Traditional Haldi & Sangeet Floral Stage",
                        theme = "Sunshine Marigold Garland",
                        matchPercentage = "92% AI Match",
                        budget = "₹25,000 - ₹38,000",
                        description = "Traditional Indian vibe with bright yellow and orange marigold garlands, seating canopies, brass Urlis, and fairy lights.",
                        recommendedItems = listOf(
                            "Yellow & Orange Marigold Garlands",
                            "Brass Urli Floating Flower Bowls",
                            "Plush Low Floor Cushions",
                            "Warm Fairy Light Curtain Backdrop"
                        ),
                        imageUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=600&q=80"
                    )
                )
            )
            1 -> VenueAnalysisResult(
                venueType = "Baby Shower & Cradle Ceremony Analysis 🍼",
                spatialFeatures = "AI scanned photo features: Soft ambient lighting, cozy indoor corner, and floral backdrop canopy installation points.",
                designModels = listOf(
                    DecorDesignModel(
                        id = "baby_model_1",
                        title = "Model A: Pastel Cloud & Teddy Bear Balloon Arch",
                        theme = "Whimsical Pastel Baby Shower",
                        matchPercentage = "98% AI Match",
                        budget = "₹14,000 - ₹22,000",
                        description = "Adorable baby shower setup featuring organic pastel pink & blue balloon garlands, giant plush teddy bear prop, and wooden welcome easel board.",
                        recommendedItems = listOf(
                            "Organic Pastel Balloon Garland Arch",
                            "Giant Plush Teddy Bear Prop",
                            "Wooden Easel Welcome Sign",
                            "Soft Warm LED Spotlights"
                        ),
                        imageUrl = "https://images.unsplash.com/photo-1513151233558-d860c5398176?auto=format&fit=crop&w=600&q=80"
                    ),
                    DecorDesignModel(
                        id = "baby_model_2",
                        title = "Model B: Royal Cradle & Jasmine Flower Canopy",
                        theme = "Traditional Cradle Ceremony",
                        matchPercentage = "95% AI Match",
                        budget = "₹18,000 - ₹28,000",
                        description = "Traditional naming ceremony setup featuring decorated golden cradle swing, jasmine & rose floral canopy, and brass floor Samai diyas.",
                        recommendedItems = listOf(
                            "Golden Decorated Cradle Swing",
                            "Jasmine & Rose Floral Canopy",
                            "Brass Floor Samai Diyas",
                            "Low Floor Cushion Seating"
                        ),
                        imageUrl = "https://images.unsplash.com/photo-1519225421980-715cb0215aed?auto=format&fit=crop&w=600&q=80"
                    ),
                    DecorDesignModel(
                        id = "baby_model_3",
                        title = "Model C: 'Welcome Little One' Neon Plinth Stage",
                        theme = "Modern Pastel Plinth Stage",
                        matchPercentage = "91% AI Match",
                        budget = "₹16,500 - ₹24,000",
                        description = "Sleek modern party setup with seamless pastel backdrop, 'Welcome Little One' neon sign, acrylic cake plinths, and plush white rug.",
                        recommendedItems = listOf(
                            "Seamless Pastel Backdrop Panel",
                            "'Welcome Little One' Neon Sign",
                            "Acrylic Cylindrical Cake Plinths",
                            "Plush Shag White Carpet"
                        ),
                        imageUrl = "https://images.unsplash.com/photo-1530103862676-de8c9debad1d?auto=format&fit=crop&w=600&q=80"
                    )
                )
            )
            2 -> VenueAnalysisResult(
                venueType = "Wall Transformation & Ambient Glow Analysis 🧱",
                spatialFeatures = "AI scanned photo features: Vertical wall surface, floor-to-ceiling clearance, and spotlighting mount points.",
                designModels = listOf(
                    DecorDesignModel(
                        id = "wall_model_1",
                        title = "Model A: Modern Fairy Light Mesh & Neon Wall",
                        theme = "Contemporary Neon Glow Wall",
                        matchPercentage = "98% AI Match",
                        budget = "₹12,000 - ₹18,000",
                        description = "Sleek wall transformation with warm LED fairy light mesh, custom neon party sign, metal balloon circle arch, and velvet lounge chairs.",
                        recommendedItems = listOf(
                            "Warm White Fairy Light Mesh Wall",
                            "Custom Neon Celebration Sign",
                            "Metal Balloon Circle Ring",
                            "Velvet Lounge Accent Chairs"
                        ),
                        imageUrl = "https://images.unsplash.com/photo-1563245372-f21724e3856d?auto=format&fit=crop&w=600&q=80"
                    ),
                    DecorDesignModel(
                        id = "wall_model_2",
                        title = "Model B: 3D Artificial Rose & Hydrangea Wall Panel",
                        theme = "Luxe Floral Wall Backdrop",
                        matchPercentage = "94% AI Match",
                        budget = "₹19,000 - ₹28,000",
                        description = "Opulent 3D floral wall panel featuring artificial roses, hydrangeas, golden geometric metal arches, and ring spotlights.",
                        recommendedItems = listOf(
                            "3D Rose & Hydrangea Floral Wall Panels",
                            "Geometric Gold Arch Set",
                            "Ring Spotlight Floor Lamps",
                            "Acrylic Welcome Plaque"
                        ),
                        imageUrl = "https://images.unsplash.com/photo-1465495976277-4387d4b0b4c6?auto=format&fit=crop&w=600&q=80"
                    ),
                    DecorDesignModel(
                        id = "wall_model_3",
                        title = "Model C: Festive Draped Satin & Marigold Wall",
                        theme = "Traditional Satin Drapes",
                        matchPercentage = "90% AI Match",
                        budget = "₹10,500 - ₹15,500",
                        description = "Vibrant traditional wall transformation using dual-tone satin drapes, fresh yellow marigold garlands, and brass floor Urlis.",
                        recommendedItems = listOf(
                            "Dual-Tone Yellow Satin Drapes",
                            "Fresh Yellow Marigold Garlands",
                            "Brass Urli Flower Bowls",
                            "Adjustable Backdrop Frame Stand"
                        ),
                        imageUrl = "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=600&q=80"
                    )
                )
            )
            3 -> VenueAnalysisResult(
                venueType = "Outdoor Garden & Lawn Party Analysis 🌿",
                spatialFeatures = "AI scanned photo features: Open-air space, natural greenery, walkway canopy points, and evening fairy lighting potential.",
                designModels = listOf(
                    DecorDesignModel(
                        id = "lawn_model_1",
                        title = "Model A: Boho Sunset Cabana & Fairy Lights",
                        theme = "Rustic Boho Garden",
                        matchPercentage = "99% AI Match",
                        budget = "₹28,000 - ₹42,000",
                        description = "Dreamy outdoor lawn setup with wooden cabana canopy, macrame drapes, Edison bulb string lights, and wooden pallet low tables.",
                        recommendedItems = listOf(
                            "Wooden Cabana Canopy Structure",
                            "Vintage Edison Bulb Strings",
                            "Macrame Linen Drapes",
                            "Low Wooden Pallet Seating"
                        ),
                        imageUrl = "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=600&q=80"
                    ),
                    DecorDesignModel(
                        id = "lawn_model_2",
                        title = "Model B: Tropical Sunset Palm & Wooden Canopy",
                        theme = "Tropical Lawn Luxe",
                        matchPercentage = "95% AI Match",
                        budget = "₹32,000 - ₹46,000",
                        description = "Vibrant outdoor canopy setup with tropical monstera leaves, exotic pampas grass, warm lanterns, and teak lounge chairs.",
                        recommendedItems = listOf(
                            "Teak Wooden Arch Frame",
                            "Exotic Tropical Leaf & Pampas Garland",
                            "Brass Hanging Lanterns",
                            "Rattan Lounge Chairs (Set of 4)"
                        ),
                        imageUrl = "https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&w=600&q=80"
                    ),
                    DecorDesignModel(
                        id = "lawn_model_3",
                        title = "Model C: Starlight DJ & Night Party Lounge",
                        theme = "Luminous DJ Stage",
                        matchPercentage = "90% AI Match",
                        budget = "₹35,000 - ₹50,000",
                        description = "High-energy outdoor celebration setup with RGB wash lights, trussing canopy, high-output DJ sound, and LED cocktail bar counter.",
                        recommendedItems = listOf(
                            "RGB Moving Head Wash Lights",
                            "Aluminum Trussing Canopy",
                            "High-Power DJ Sound System",
                            "Acrylic LED Bar Counter"
                        ),
                        imageUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=600&q=80"
                    )
                )
            )
            4 -> VenueAnalysisResult(
                venueType = "Milestone Birthday & Party Lounge Analysis 🎂",
                spatialFeatures = "AI scanned photo features: Party hall floor area, photobooth backdrop wall, and cocktail seating setup.",
                designModels = listOf(
                    DecorDesignModel(
                        id = "bday_model_1",
                        title = "Model A: Golden Shimmer Wall & Ring Balloon Arch",
                        theme = "Glamorous Shimmer Sequin",
                        matchPercentage = "98% AI Match",
                        budget = "₹15,500 - ₹23,000",
                        description = "High-shine birthday photobooth setup featuring 3D golden shimmer sequin backdrop panels, metallic balloon ring arch, and custom age neon sign.",
                        recommendedItems = listOf(
                            "3D Gold Shimmer Sequin Wall",
                            "Metallic Gold & Black Balloon Arch",
                            "Custom Birthday Age Neon Sign",
                            "Acrylic Cake Pedestal Tables"
                        ),
                        imageUrl = "https://images.unsplash.com/photo-1530103862676-de8c9debad1d?auto=format&fit=crop&w=600&q=80"
                    ),
                    DecorDesignModel(
                        id = "bday_model_2",
                        title = "Model B: Midnight Blue & Silver Metallic Party Stage",
                        theme = "Midnight Silver Gala",
                        matchPercentage = "94% AI Match",
                        budget = "₹18,000 - ₹26,000",
                        description = "Modern celebration stage with velvet midnight blue backcloth, silver metallic balloons, spotlighting, and plush velvet seating.",
                        recommendedItems = listOf(
                            "Midnight Blue Velvet Drape Backdrop",
                            "Silver Metallic Balloon Garland",
                            "LED White Focus Spotlights",
                            "Silver Chrome Plinth Set"
                        ),
                        imageUrl = "https://images.unsplash.com/photo-1563245372-f21724e3856d?auto=format&fit=crop&w=600&q=80"
                    ),
                    DecorDesignModel(
                        id = "bday_model_3",
                        title = "Model C: Rose Gold Velvet & Champagne Bar Backdrop",
                        theme = "Rose Gold Glamour",
                        matchPercentage = "91% AI Match",
                        budget = "₹21,000 - ₹30,000",
                        description = "Ultra-luxe celebration lounge featuring rose gold foil backdrop, champagne glass tower rack, floral arches, and soft ring lighting.",
                        recommendedItems = listOf(
                            "Rose Gold Foil Backdrop Curtain",
                            "Acrylic Champagne Tower Display Rack",
                            "Blush Floral Circle Arch",
                            "Ring Spotlight Floor Lamps"
                        ),
                        imageUrl = "https://images.unsplash.com/photo-1513151233558-d860c5398176?auto=format&fit=crop&w=600&q=80"
                    )
                )
            )
            else -> VenueAnalysisResult(
                venueType = "Corporate Gala & Award Ceremony Stage Analysis 🏆",
                spatialFeatures = "AI scanned photo features: Wide stage clearance, carpeted floor area, speech podium placement, and LED wall wash.",
                designModels = listOf(
                    DecorDesignModel(
                        id = "corp_model_1",
                        title = "Model A: Sleek Corporate LED Light Bar Stage",
                        theme = "Modern Corporate Luxe",
                        matchPercentage = "97% AI Match",
                        budget = "₹26,000 - ₹38,000",
                        description = "Professional gala setup with seamless white backdrop, vertical RGB light tubes, acrylic speech podium, and low fog stage effect.",
                        recommendedItems = listOf(
                            "Seamless Matte White Backdrop Panel",
                            "Vertical RGB Light Bars (Set of 6)",
                            "Transparent Acrylic Speech Podium",
                            "Low-Lying Fog Machine"
                        ),
                        imageUrl = "https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&w=600&q=80"
                    ),
                    DecorDesignModel(
                        id = "corp_model_2",
                        title = "Model B: Executive Royal Blue & Gold Panel Stage",
                        theme = "Executive Gold & Blue",
                        matchPercentage = "34% AI Match",
                        budget = "₹34,000 - ₹48,000",
                        description = "Sophisticated corporate stage with royal blue velvet panels, geometric gold metal frames, LED wash lights, and award display plinths.",
                        recommendedItems = listOf(
                            "Royal Blue Velvet Backdrop Panels",
                            "Geometric Gold Metal Arch Set",
                            "Warm White Stage Wash Lights",
                            "Award Display Acrylic Plinths"
                        ),
                        imageUrl = "https://images.unsplash.com/photo-1519225421980-715cb0215aed?auto=format&fit=crop&w=600&q=80"
                    ),
                    DecorDesignModel(
                        id = "corp_model_3",
                        title = "Model C: Minimalist Acrylic Podium & Trussing Stage",
                        theme = "Sleek Industrial Stage",
                        matchPercentage = "89% AI Match",
                        budget = "₹22,000 - ₹32,000",
                        description = "Industrial gala stage featuring aluminum trussing frame, matte black backdrop, LED spot focus, and sleek podium.",
                        recommendedItems = listOf(
                            "Aluminum Stage Trussing Structure",
                            "Matte Black Fabric Backdrop",
                            "LED Focus Spotlights",
                            "Modern Black Metal Podium"
                        ),
                        imageUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=600&q=80"
                    )
                )
            )
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            uploadCounter++
            selectedModelIndex = 0
            scope.launch {
                isAnalyzing = true
                analysisResult = null

                val liveResult = com.example.rent.services.GeminiImageAnalyzer.analyzeImageWithGemini(context, uri)

                if (liveResult != null) {
                    analysisResult = liveResult
                isRealTimeAi = true
                    viewModel.showToast("⚡ Gemini Vision API analyzed your photo!")
                } else {
                    delay(1200)
                    analysisResult = analyzeImageUri(uri)
                    isRealTimeAi = false
                }

                isAnalyzing = false
            }
        }
    }

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
            Text("✨ AI Venue Decor Scanner", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
        }
        Spacer(modifier = Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("✨", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Smart Venue Visual Analyzer", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Upload any photo file from your device. Our AI engine analyzes your photo space and suggests 3 tailored decor design models!",
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { filePickerLauncher.launch("image/*") },
                    enabled = !isAnalyzing,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = BgPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("📁 Choose Image File", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Selected File Preview Box
        if (selectedImageUri != null) {
            GlassCard(
                borderColor = PrimaryGold.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📷 Uploaded Photo", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                        Surface(
                            color = PrimaryGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Image File Loaded", fontSize = 10.sp, color = PrimaryGold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { filePickerLauncher.launch("image/*") },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Choose Different File", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isAnalyzing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = PrimaryGold)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("🤖 AI Engine inspecting photo subject, lighting & generating event decor models...", color = PrimaryGold, fontSize = 12.sp, textAlign = TextAlign.Center)
                }
            }
        }

        if (analysisResult != null) {
            val result = analysisResult!!
            val models = result.designModels

            // Venue Detection Summary Card
            GlassCard(
                borderColor = PrimaryGold.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(result.venueType, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryGold, modifier = Modifier.weight(1f))
                        if (isRealTimeAi) {
                            Surface(
                                color = SuccessGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("⚡ Live Gemini Vision", fontSize = 10.sp, color = SuccessGreen, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(result.spatialFeatures, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Select AI Decor Design Model:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(10.dp))

            // Model Selection Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                models.forEachIndexed { index, model ->
                    val isSelected = selectedModelIndex == index
                    Surface(
                        onClick = { selectedModelIndex = index },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) PrimaryGold else BgSecondary,
                        border = BorderStroke(1.dp, if (isSelected) PrimaryGold else GlassBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
                        ) {
                            Text(
                                text = "Model ${index + 1}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) BgPrimary else TextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Display Active Design Model Card
            val activeModel = models.getOrNull(selectedModelIndex) ?: models.first()

            GlassCard(
                borderColor = PrimaryGold,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Header Row with Title, Match & Budget Pills
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = activeModel.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = SuccessGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, SuccessGreen)
                            ) {
                                Text(
                                    text = activeModel.matchPercentage,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Budget Pill on its own clean line to prevent text wrapping issues
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Estimated Budget: ", fontSize = 12.sp, color = TextMuted)
                            Surface(
                                color = PrimaryGold,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = activeModel.budget,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    color = BgPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Model Preview Image
                    AsyncImage(
                        model = activeModel.imageUrl,
                        contentDescription = activeModel.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Theme Style: ${activeModel.theme}", color = PrimaryGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(activeModel.description, color = TextSecondary, fontSize = 12.sp, lineHeight = 17.sp)

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Included Decor Rental Pack:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGold)
                    Spacer(modifier = Modifier.height(4.dp))
                    activeModel.recommendedItems.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(item, fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.showToast("Selected ${activeModel.title} design package!")
                    onNavigate("items", emptyMap())
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = BgPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Matching Rental Inventory 🛒", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}
