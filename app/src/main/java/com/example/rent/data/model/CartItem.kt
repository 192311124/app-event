package com.example.rent.data.model

data class CartItem(
    val docId: String = "",
    val userId: String = "",
    val itemId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val image: String = "",
    val quantity: Int = 1,
    val date: String = "",
    val time: String = "",
    val addedAt: String = ""
) {
    companion object {
        fun fromMap(docId: String, map: Map<String, Any?>): CartItem {
            return CartItem(
                docId = docId,
                userId = (map["userId"] as? String) ?: "",
                itemId = (map["itemId"] as? String) ?: "",
                name = (map["name"] as? String) ?: "Rental Decor Item",
                price = ((map["price"] as? Number)?.toDouble()) ?: 0.0,
                image = (map["image"] as? String) ?: "",
                quantity = ((map["quantity"] as? Number)?.toInt()) ?: 1,
                date = (map["date"] as? String) ?: "",
                time = (map["time"] as? String) ?: "10:00 AM",
                addedAt = (map["addedAt"] as? String) ?: ""
            )
        }
    }
}
