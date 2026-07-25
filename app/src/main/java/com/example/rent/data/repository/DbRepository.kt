package com.example.rent.data.repository

import com.example.rent.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DbRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // 1. Fetch Categories
    suspend fun getCategories(): List<EventCategory> {
        return try {
            val snapshot = db.collection("categories").get().await()
            if (!snapshot.isEmpty) {
                val dbCats = snapshot.documents.map { doc ->
                    EventCategory.fromMap(doc.id, doc.data ?: emptyMap())
                }
                (dbCats + getSampleCategories()).distinctBy { it.name }
            } else {
                getSampleCategories()
            }
        } catch (e: Exception) {
            getSampleCategories()
        }
    }

    // 2. Fetch Items
    suspend fun getItems(): List<RentalItem> {
        return try {
            val snapshot = db.collection("items").get().await()
            if (!snapshot.isEmpty) {
                val dbItems = snapshot.documents.map { doc ->
                    RentalItem.fromMap(doc.id, doc.data ?: emptyMap())
                }
                (dbItems + getSampleItems()).distinctBy { it.name }
            } else {
                getSampleItems()
            }
        } catch (e: Exception) {
            getSampleItems()
        }
    }

    private fun getSampleCategories(): List<EventCategory> {
        return listOf(
            EventCategory("cat_wedding", "Wedding & Reception", "💍", "Grand Mandap setups, Royal Throne Sofas & Stage Drapes"),
            EventCategory("cat_baby", "Baby Shower & Cradle", "🍼", "Pastel Balloon Arches, Golden Cradles & Teddy Bear Props"),
            EventCategory("cat_haldi", "Haldi & Mehendi", "🌼", "Marigold Garlands, Yellow Satin Drapes & Brass Urlis"),
            EventCategory("cat_lawn", "Outdoor Lawn & Garden", "🌿", "Wooden Cabanas, Edison String Lights & Rustic Lounges"),
            EventCategory("cat_bday", "Milestone Birthdays", "🎂", "Gold Shimmer Sequin Walls, Neon Signs & Cake Plinths"),
            EventCategory("cat_corp", "Corporate & Stages", "🏆", "LED Wash Stage Bars, Acrylic Podiums & Trussing"),
            EventCategory("cat_lights", "Lighting & Special FX", "✨", "RGB Wash Lights, Moving Heads, Cold Pyros & Low Fog"),
            EventCategory("cat_furniture", "Furniture & Seating", "🛋️", "Golden Throne Sofas, Wooden Jhulas & Velvet Chairs"),
            EventCategory("cat_tents", "Tents & Canopies", "⛺", "German Marquees, Pagoda Tents, Arabian Canopies & Shamianas")
        )
    }

    private fun getSampleItems(): List<RentalItem> {
        return listOf(
            RentalItem(
                id = "item_1",
                name = "Royal Rajputana Golden Mandap Canopy",
                description = "Grand royal wedding mandap featuring hand-carved golden pillars, rich red velvet ceiling drape, and brass diya stands. Perfect for luxury wedding ceremonies.",
                price = 18500.0,
                availability = true,
                rating = 4.9,
                category = "Wedding & Reception",
                image = "https://images.unsplash.com/photo-1545232979-fbfd42e000b9?auto=format&fit=crop&w=600&q=80",
                reviews = listOf(
                    RentalReview("Priya Sharma", 5.0, "Stunning mandap! Looked absolutely majestic for our wedding."),
                    RentalReview("Rahul Verma", 4.8, "Top quality gold finish and seamless setup by the team.")
                )
            ),
            RentalItem(
                id = "item_2",
                name = "Crystal Chandelier Canopy & Stage Light",
                description = "Luxury crystal chandelier arrangement mounted on heavy steel canopy frame with warm spotlight wash for grand reception stages.",
                price = 12000.0,
                availability = true,
                rating = 4.8,
                category = "Lighting & Special FX",
                image = "https://images.unsplash.com/photo-1527529482837-4698179dc6ce?auto=format&fit=crop&w=600&q=80",
                reviews = listOf(
                    RentalReview("Ananya Gupta", 5.0, "Added such an opulent sparkle to our stage backdrop!")
                )
            ),
            RentalItem(
                id = "item_3",
                name = "Red Crimson Velvet Entrance Arch",
                description = "Plush red crimson velvet entrance arch with floral gold trim highlights, suitable for grand wedding and reception entryways.",
                price = 8500.0,
                availability = true,
                rating = 4.7,
                category = "Wedding & Reception",
                image = "https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_4",
                name = "Traditional Brass Floor Samai Diyas (Pair)",
                description = "Heavy handcrafted 5-tier brass Samai diya set for traditional Indian welcoming, Haldi, Mehendi, and auspicious occasions.",
                price = 2500.0,
                availability = true,
                rating = 4.9,
                category = "Haldi & Mehendi",
                image = "https://images.unsplash.com/photo-1605371924599-2d0365da1ae0?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_5",
                name = "Carved Wooden Royal Jhula Swing",
                description = "Hand-carved teakwood royal swing with brass chain mount and silk cushion seating for Bride & Groom or Haldi ceremony.",
                price = 9800.0,
                availability = true,
                rating = 4.9,
                category = "Furniture & Seating",
                image = "https://images.unsplash.com/photo-1519225421980-715cb0215aed?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_6",
                name = "Golden High-Back Throne Sofas (Pair)",
                description = "Royal bride & groom throne chairs with rich gold leaf polish, tufted velvet upholstery, and matching footstool.",
                price = 14000.0,
                availability = true,
                rating = 4.8,
                category = "Furniture & Seating",
                image = "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_7",
                name = "Marigold Flower Wall Garland Panel (10x8 ft)",
                description = "Vibrant yellow and orange fresh marigold floral backdrop panel for Haldi, Sangeet, Pooja, and festive photo booths.",
                price = 5200.0,
                availability = true,
                rating = 4.9,
                category = "Haldi & Mehendi",
                image = "https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_8",
                name = "Warm White LED Fairy Light Curtain (10x10 ft)",
                description = "High-density warm white LED curtain backdrop with 8 shimmer mode settings for ambient indoor & outdoor decor.",
                price = 3200.0,
                availability = true,
                rating = 4.7,
                category = "Lighting & Special FX",
                image = "https://images.unsplash.com/photo-1563245372-f21724e3856d?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_9",
                name = "Custom Neon Party Signboard",
                description = "Bright LED flex neon signs (options: 'Better Together', 'Lets Party', 'Welcome Little One', 'Happily Ever After').",
                price = 2800.0,
                availability = true,
                rating = 4.8,
                category = "Milestone Birthdays",
                image = "https://images.unsplash.com/photo-1563245372-f21724e3856d?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_10",
                name = "Organic Pastel Cloud Balloon Arch",
                description = "Whimsical pastel pink, powder blue, and white organic balloon garland arch with metal frame for baby showers and 1st birthdays.",
                price = 4500.0,
                availability = true,
                rating = 4.8,
                category = "Baby Shower & Cradle",
                image = "https://images.unsplash.com/photo-1513151233558-d860c5398176?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_11",
                name = "Golden Decorated Cradle Swing",
                description = "Luxury golden carved baby cradle swing with silk lining, floral canopy trim, and soft mattress for naming ceremonies.",
                price = 6800.0,
                availability = true,
                rating = 4.9,
                category = "Baby Shower & Cradle",
                image = "https://images.unsplash.com/photo-1519225421980-715cb0215aed?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_12",
                name = "Giant 5ft Plush Teddy Bear Prop",
                description = "Adorable 5-foot oversized plush brown teddy bear photo prop for baby shower, nursery, and kids birthday setup.",
                price = 2200.0,
                availability = true,
                rating = 4.9,
                category = "Baby Shower & Cradle",
                image = "https://images.unsplash.com/photo-1559454403-b8fb88521f11?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_13",
                name = "3D Artificial Rose & Hydrangea Wall Panel",
                description = "High-density 3D floral wall panel with blush roses, white hydrangeas, and greenery accent foliage.",
                price = 11500.0,
                availability = true,
                rating = 4.8,
                category = "Wedding & Reception",
                image = "https://images.unsplash.com/photo-1465495976277-4387d4b0b4c6?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_14",
                name = "Wooden Cabana Canopy with Edison Bulbs",
                description = "Rustic teakwood cabana canopy structure with linen drapes and warm vintage Edison bulb string lights for lawn parties.",
                price = 15000.0,
                availability = true,
                rating = 4.9,
                category = "Outdoor Lawn & Garden",
                image = "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_15",
                name = "RGB Moving Head Wash Stage Lights (Set of 4)",
                description = "Pro DMX 36x3W RGBW LED moving head wash lights for dynamic stage washes, concert lighting, and dance floor effects.",
                price = 7500.0,
                availability = true,
                rating = 4.7,
                category = "Lighting & Special FX",
                image = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_16",
                name = "Pro DJ Sound System & Dual Wireless Mics",
                description = "2000W active PA speaker system with subwoofer, 4-channel audio mixer, and dual UHF wireless handheld microphones.",
                price = 12500.0,
                availability = true,
                rating = 4.8,
                category = "Outdoor Lawn & Garden",
                image = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_17",
                name = "Acrylic LED Illuminated Bar Counter",
                description = "Modular RGB multi-color LED illuminated cocktail bar counter with remote color control and bottle display shelf.",
                price = 9500.0,
                availability = true,
                rating = 4.8,
                category = "Furniture & Seating",
                image = "https://images.unsplash.com/photo-1514933651103-005eec06c04b?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_18",
                name = "Gold Shimmer Sequin Photobooth Wall (8x8 ft)",
                description = "Dynamic 3D shimmer sequin wall panels that flutter with air movement for dazzling glam photo backgrounds.",
                price = 6500.0,
                availability = true,
                rating = 4.8,
                category = "Milestone Birthdays",
                image = "https://images.unsplash.com/photo-1530103862676-de8c9debad1d?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_19",
                name = "Cold Spark Fountain Pyros (Set of 4)",
                description = "Smokeless indoor-safe cold spark fountain machine system (up to 3m height) for wedding entry and stage climaxes.",
                price = 4800.0,
                availability = true,
                rating = 4.9,
                category = "Lighting & Special FX",
                image = "https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_20",
                name = "Low-Lying Heavy Fog Dry Ice Machine",
                description = "Professional low fog machine that produces dense floor-hugging cloud fog for grand bridal entries and dance performances.",
                price = 3800.0,
                availability = true,
                rating = 4.7,
                category = "Lighting & Special FX",
                image = "https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_21",
                name = "Royal Brass Urli Set for Floating Petals (Set of 3)",
                description = "Decorative hand-hammered antique brass Urlis in 3 nested sizes for flower petals, floating candles, and entrance decor.",
                price = 3400.0,
                availability = true,
                rating = 4.9,
                category = "Haldi & Mehendi",
                image = "https://images.unsplash.com/photo-1605371924599-2d0365da1ae0?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_22",
                name = "Geometric Gold Metal Backdrop Arch Set (3 Pcs)",
                description = "Modular geometric gold metal arch frames for floral arrangements, balloon backdrops, and modern stage styling.",
                price = 5800.0,
                availability = true,
                rating = 4.8,
                category = "Milestone Birthdays",
                image = "https://images.unsplash.com/photo-1465495976277-4387d4b0b4c6?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_23",
                name = "Velvet Lounge Accent Chairs (Pair)",
                description = "Plush emerald green or navy blue velvet shell chairs with gold pin legs for VIP seating and photobooths.",
                price = 4800.0,
                availability = true,
                rating = 4.8,
                category = "Furniture & Seating",
                image = "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_24",
                name = "Acrylic Cylindrical Cake Plinths (Set of 3)",
                description = "Sleek transparent & white cylindrical display plinth pedestals in varied heights for cake and dessert tables.",
                price = 3200.0,
                availability = true,
                rating = 4.7,
                category = "Milestone Birthdays",
                image = "https://images.unsplash.com/photo-1530103862676-de8c9debad1d?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_25",
                name = "Wooden Easel Welcome Sign Board",
                description = "Natural oak wooden tripod easel stand with chalkboard or acrylic welcome plaque for event entrances.",
                price = 1500.0,
                availability = true,
                rating = 4.8,
                category = "Wedding & Reception",
                image = "https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_26",
                name = "Pampas Grass Corner Pedestals (Pair)",
                description = "Tall natural fluffy pampas grass arrangements set in rustic ceramic pedestals for boho weddings and photo zones.",
                price = 3600.0,
                availability = true,
                rating = 4.8,
                category = "Outdoor Lawn & Garden",
                image = "https://images.unsplash.com/photo-1465495976277-4387d4b0b4c6?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_27",
                name = "Red Velvet Carpet Pathway Runner (30 ft)",
                description = "Premium thick red carpet runner (30 feet x 5 feet) for royal wedding aisles, VIP arrivals, and red carpet events.",
                price = 2800.0,
                availability = true,
                rating = 4.9,
                category = "Wedding & Reception",
                image = "https://images.unsplash.com/photo-1545232979-fbfd42e000b9?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_28",
                name = "Jasmine & Rose Floral Tunnel Canopy",
                description = "Walkthrough entrance canopy lined with artificial white jasmine garlands, fresh pink roses, and hanging fairy lights.",
                price = 14500.0,
                availability = true,
                rating = 4.9,
                category = "Wedding & Reception",
                image = "https://images.unsplash.com/photo-1519225421980-715cb0215aed?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_29",
                name = "Aluminum Stage Trussing Structure (12x8 ft)",
                description = "Heavy-duty aluminum box trussing frame for lighting rig mounts, banner backdrops, and stage canopy support.",
                price = 10500.0,
                availability = true,
                rating = 4.7,
                category = "Corporate & Stages",
                image = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_30",
                name = "Transparent Acrylic Speech Podium",
                description = "Modern clear plexiglass lectern podium with built-in mic holder clip and cable management for corporate conferences.",
                price = 3500.0,
                availability = true,
                rating = 4.8,
                category = "Corporate & Stages",
                image = "https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_31",
                name = "Dual-Tone Yellow Satin Backdrop Drapes",
                description = "20ft wide double-layered satin fabric backdrop drapes with pre-stitched rod pockets for easy frame mounting.",
                price = 2900.0,
                availability = true,
                rating = 4.7,
                category = "Haldi & Mehendi",
                image = "https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_32",
                name = "Low Wooden Pallet Seating with Boho Cushions",
                description = "Set of 4 low wooden pallet tables with 16 plush bohemian floor seating cushions and woven floor rugs.",
                price = 4200.0,
                availability = true,
                rating = 4.8,
                category = "Furniture & Seating",
                image = "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_33",
                name = "Outdoor Waterproof LED Par Lights (Set of 8)",
                description = "54x3W high-output RGBW LED Par uplights for outdoor venue tree wash, wall washing, and lawn perimeter illumination.",
                price = 4200.0,
                availability = true,
                rating = 4.8,
                category = "Lighting & Special FX",
                image = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_34",
                name = "Vintage Festoon Edison Bulb Strings (50 ft)",
                description = "Heavy-duty weatherproof festoon string lights with warm amber dimmable Edison filament bulbs for outdoor lawns and dining canopies.",
                price = 2400.0,
                availability = true,
                rating = 4.9,
                category = "Lighting & Special FX",
                image = "https://images.unsplash.com/photo-1507676184212-d03ab07a01bf?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_35",
                name = "High-Power Stage Follow Spot Light with Stand",
                description = "350W LED follow spotlight with manual iris zoom, color wheel, and heavy-duty tripod stand for stage entries and bride/groom spotlighting.",
                price = 3500.0,
                availability = true,
                rating = 4.8,
                category = "Lighting & Special FX",
                image = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_36",
                name = "Wireless Smart Battery LED Uplights (Set of 6)",
                description = "Cable-free rechargeable App & Remote controlled RGBWA+UV uplights for clean wall wash without visible power wires.",
                price = 5600.0,
                availability = true,
                rating = 4.9,
                category = "Lighting & Special FX",
                image = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_37",
                name = "RGB Pixel Tube Light Bars (Set of 4)",
                description = "360-degree wireless pixel tube lights with dynamic motion chasing effects for DJ stages, modern photobooths, and music stages.",
                price = 4800.0,
                availability = true,
                rating = 4.8,
                category = "Lighting & Special FX",
                image = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_38",
                name = "Heavy Brass Antique Hanging Lanterns (Set of 4)",
                description = "Moroccan antique brass glass lanterns with flameless LED pillar candles for walkway lining and royal entrance decor.",
                price = 3100.0,
                availability = true,
                rating = 4.8,
                category = "Lighting & Special FX",
                image = "https://images.unsplash.com/photo-1507676184212-d03ab07a01bf?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_39",
                name = "Laser Light Show System with Pattern Projector",
                description = "High-power RGB animation graphic laser light projector for Sangeet nights, DJ dance floors, and aerial beam shows.",
                price = 5000.0,
                availability = true,
                rating = 4.7,
                category = "Lighting & Special FX",
                image = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_40",
                name = "Warm Yellow Gobo Pattern Projector Light",
                description = "Custom monogram and floral pattern Gobo light projector for entrance carpet pathways and stage backdrop projection.",
                price = 2900.0,
                availability = true,
                rating = 4.8,
                category = "Lighting & Special FX",
                image = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_41",
                name = "Vintage Illuminated Marquee Bulb Frame Sign",
                description = "4-foot giant freestanding illuminated metal marquee letters/numbers with warm vintage filament bulbs for photobooths.",
                price = 3800.0,
                availability = true,
                rating = 4.9,
                category = "Lighting & Special FX",
                image = "https://images.unsplash.com/photo-1563245372-f21724e3856d?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_42",
                name = "Outdoor Water Wave & Aurora Projection Light",
                description = "High-intensity water ripple motion projector for ocean blue and Northern Lights ambient lighting effect on venue ceilings and walls.",
                price = 2600.0,
                availability = true,
                rating = 4.8,
                category = "Lighting & Special FX",
                image = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_43",
                name = "Luxury White German Marquee Tent (40x60 ft)",
                description = "Heavy-duty waterproof German aluminum structure marquee tent with PVC sidewalls, silk lining ceiling drapes, and chandelier mounts.",
                price = 28500.0,
                availability = true,
                rating = 4.9,
                category = "Tents & Canopies",
                image = "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_44",
                name = "Arabian Bedouin Stretch Canopy Tent",
                description = "Flexible waterproof organic stretch canvas canopy tent in sand gold color with eucalyptus pole supports and festoon lighting support.",
                price = 18000.0,
                availability = true,
                rating = 4.8,
                category = "Tents & Canopies",
                image = "https://images.unsplash.com/photo-1545232979-fbfd42e000b9?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_45",
                name = "Traditional Royal Shamiana Wedding Tent",
                description = "Rich embroidered cotton fabric Shamiana canopy tent with floral motif inner lining, scalloped borders, and corner tassels.",
                price = 15000.0,
                availability = true,
                rating = 4.7,
                category = "Tents & Canopies",
                image = "https://images.unsplash.com/photo-1519225421980-715cb0215aed?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_46",
                name = "High-Peak Pagoda Canopy Tent (20x20 ft)",
                description = "High-peaked white pagoda gazebo canopy tent suitable for food stalls, outdoor VIP shade lounges, and reception entries.",
                price = 9500.0,
                availability = true,
                rating = 4.8,
                category = "Tents & Canopies",
                image = "https://images.unsplash.com/photo-1527529482837-4698179dc6ce?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_47",
                name = "Bohemian Lawn Canopy with Fairy Lights",
                description = "Rustic wooden cabana canopy structure draped with sheer white organza curtains and warm fairy string lights.",
                price = 12500.0,
                availability = true,
                rating = 4.9,
                category = "Tents & Canopies",
                image = "https://images.unsplash.com/photo-1465495976277-4387d4b0b4c6?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_48",
                name = "Transparent Crystal Glass Marquee Tent",
                description = "Panoramic clear-span transparent roof marquee tent offering 360-degree night sky views for royal wedding receptions.",
                price = 35000.0,
                availability = true,
                rating = 5.0,
                category = "Tents & Canopies",
                image = "https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_49",
                name = "Haldi Marigold Draped Bamboo Canopy",
                description = "Natural bamboo framework canopy draped with dual-tone yellow drapes and fresh marigold flower garlands for Haldi ceremonies.",
                price = 8200.0,
                availability = true,
                rating = 4.8,
                category = "Tents & Canopies",
                image = "https://images.unsplash.com/photo-1605371924599-2d0365da1ae0?auto=format&fit=crop&w=600&q=80"
            ),
            RentalItem(
                id = "item_50",
                name = "VIP Air-Conditioned Geodesic Dome Canopy Tent",
                description = "Futuristic 30ft geodesic dome canopy tent with climate control AC duct mounts and projection mapping clear PVC skin.",
                price = 42000.0,
                availability = true,
                rating = 4.9,
                category = "Tents & Canopies",
                image = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=600&q=80"
            )
        )
    }

    // 3. Cart Methods
    suspend fun getCart(userId: String): List<CartItem> {
        return try {
            val snapshot = db.collection("cart").whereEqualTo("userId", userId).get().await()
            snapshot.documents.map { doc ->
                CartItem.fromMap(doc.id, doc.data ?: emptyMap())
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addToCart(userId: String, item: RentalItem, quantity: Int, date: String, time: String) {
        val newItem = hashMapOf(
            "userId" to userId,
            "itemId" to item.id,
            "name" to item.name,
            "price" to item.price,
            "image" to item.image,
            "quantity" to quantity,
            "date" to date,
            "time" to time,
            "addedAt" to java.time.Instant.now().toString()
        )
        db.collection("cart").add(newItem).await()
    }

    suspend fun updateCartQuantity(docId: String, quantity: Int) {
        db.collection("cart").document(docId).update("quantity", quantity).await()
    }

    suspend fun removeFromCart(docId: String) {
        db.collection("cart").document(docId).delete().await()
    }

    suspend fun clearCart(userId: String) {
        val snapshot = db.collection("cart").whereEqualTo("userId", userId).get().await()
        for (doc in snapshot.documents) {
            doc.reference.delete().await()
        }
    }

    // 4. Wishlist Methods
    suspend fun getWishlist(userId: String): List<String> {
        return try {
            val snapshot = db.collection("wishlist").whereEqualTo("userId", userId).get().await()
            snapshot.documents.mapNotNull { it.getString("itemId") }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun toggleWishlist(userId: String, itemId: String): List<String> {
        val snapshot = db.collection("wishlist")
            .whereEqualTo("userId", userId)
            .whereEqualTo("itemId", itemId)
            .get().await()

        if (!snapshot.isEmpty) {
            snapshot.documents.first().reference.delete().await()
        } else {
            val data = hashMapOf(
                "userId" to userId,
                "itemId" to itemId,
                "addedAt" to java.time.Instant.now().toString()
            )
            db.collection("wishlist").add(data).await()
        }
        return getWishlist(userId)
    }

    // 5. Booking Methods
    suspend fun getBookings(userId: String): List<Booking> {
        return try {
            val snapshot = db.collection("bookings").whereEqualTo("userId", userId).get().await()
            snapshot.documents.map { doc ->
                Booking.fromMap(doc.id, doc.data ?: emptyMap())
            }.sortedByDescending { it.createdAt }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addBooking(userId: String, bookingData: Map<String, Any>): String {
        val newBooking = hashMapOf<String, Any>(
            "userId" to userId,
            "status" to "Confirmed",
            "createdAt" to java.time.Instant.now().toString()
        ).apply { putAll(bookingData) }

        val docRef = db.collection("bookings").add(newBooking).await()

        val numItems = (bookingData["items"] as? List<*>)?.size ?: 1
        val deliveryDate = bookingData["deliveryDate"] as? String ?: ""
        val totalPrice = bookingData["totalPrice"] as? Number ?: 0

        val notif = hashMapOf(
            "userId" to userId,
            "title" to "Booking Confirmed! 🎉",
            "body" to "Your order for $numItems item(s) is confirmed for $deliveryDate. Total: ₹$totalPrice.",
            "type" to "confirmation",
            "createdAt" to java.time.Instant.now().toString(),
            "read" to false
        )
        db.collection("notifications").add(notif).await()

        return docRef.id
    }

    suspend fun cancelBooking(userId: String, bookingId: String) {
        db.collection("bookings").document(bookingId).update("status", "Cancelled").await()

        val notif = hashMapOf(
            "userId" to userId,
            "title" to "Booking Cancelled",
            "body" to "Your booking ID $bookingId has been cancelled. Refund in ₹ has been initiated.",
            "type" to "cancellation",
            "createdAt" to java.time.Instant.now().toString(),
            "read" to false
        )
        db.collection("notifications").add(notif).await()
    }

    // 6. User Profile Methods
    suspend fun getUserProfile(userId: String): UserProfile {
        return try {
            val snapshot = db.collection("userProfile").whereEqualTo("userId", userId).get().await()
            if (!snapshot.isEmpty) {
                UserProfile.fromMap(userId, snapshot.documents.first().data ?: emptyMap())
            } else {
                UserProfile(userId = userId)
            }
        } catch (e: Exception) {
            UserProfile(userId = userId)
        }
    }

    suspend fun updateUserProfile(userId: String, data: Map<String, Any>) {
        val snapshot = db.collection("userProfile").whereEqualTo("userId", userId).get().await()
        if (!snapshot.isEmpty) {
            snapshot.documents.first().reference.update(data).await()
        } else {
            val newProfile = hashMapOf<String, Any>("userId" to userId).apply { putAll(data) }
            db.collection("userProfile").add(newProfile).await()
        }
    }

    // 7. Notifications
    suspend fun getNotifications(userId: String): List<AppNotification> {
        return try {
            val snapshot = db.collection("notifications").whereEqualTo("userId", userId).get().await()
            snapshot.documents.map { doc ->
                AppNotification.fromMap(doc.id, doc.data ?: emptyMap())
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
