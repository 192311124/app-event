package com.example.rent.data.model

data class RentalReview(
    val user: String = "Anonymous",
    val rating: Double = 5.0,
    val comment: String = ""
)

data class RentalItem(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val availability: Boolean = true,
    val rating: Double = 4.8,
    val category: String = "Decoratives",
    val image: String = "",
    val reviews: List<RentalReview> = emptyList()
) {
    companion object {
        fun fromMap(docId: String, map: Map<String, Any?>): RentalItem {
            val rawReviews = (map["reviews"] as? List<*>) ?: emptyList<Any>()
            val reviewList = rawReviews.mapNotNull { r ->
                if (r is Map<*, *>) {
                    RentalReview(
                        user = (r["user"] as? String) ?: "Anonymous",
                        rating = ((r["rating"] as? Number)?.toDouble()) ?: 5.0,
                        comment = (r["comment"] as? String) ?: ""
                    )
                } else null
            }

            return RentalItem(
                id = docId,
                name = (map["name"] as? String) ?: "",
                description = (map["description"] as? String) ?: "",
                price = ((map["price"] as? Number)?.toDouble()) ?: 0.0,
                availability = (map["availability"] as? Boolean) ?: true,
                rating = ((map["rating"] as? Number)?.toDouble()) ?: 4.8,
                category = (map["category"] as? String) ?: "Decoratives",
                image = (map["image"] as? String) ?: "https://images.unsplash.com/photo-1563245372-f21724e3856d?auto=format&fit=crop&w=400&q=80",
                reviews = reviewList
            )
        }
    }
}
