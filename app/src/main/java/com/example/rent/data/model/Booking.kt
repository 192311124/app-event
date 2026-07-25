package com.example.rent.data.model

data class BookingItemInfo(
    val itemId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val image: String = ""
)

data class Booking(
    val bookingId: String = "",
    val userId: String = "",
    val items: List<BookingItemInfo> = emptyList(),
    val totalPrice: Double = 0.0,
    val address: String = "",
    val deliveryDate: String = "",
    val timeSlot: String = "",
    val paymentMethod: String = "UPI / Card",
    val status: String = "Confirmed",
    val createdAt: String = ""
) {
    companion object {
        fun fromMap(docId: String, map: Map<String, Any?>): Booking {
            val rawItems = (map["items"] as? List<*>) ?: emptyList<Any>()
            val itemList = rawItems.mapNotNull { i ->
                if (i is Map<*, *>) {
                    BookingItemInfo(
                        itemId = (i["itemId"] as? String) ?: "",
                        name = (i["name"] as? String) ?: "",
                        price = ((i["price"] as? Number)?.toDouble()) ?: 0.0,
                        quantity = ((i["quantity"] as? Number)?.toInt()) ?: 1,
                        image = (i["image"] as? String) ?: ""
                    )
                } else null
            }

            return Booking(
                bookingId = docId,
                userId = (map["userId"] as? String) ?: "",
                items = itemList,
                totalPrice = ((map["totalPrice"] as? Number)?.toDouble()) ?: 0.0,
                address = (map["address"] as? String) ?: "",
                deliveryDate = (map["deliveryDate"] as? String) ?: "",
                timeSlot = (map["timeSlot"] as? String) ?: "",
                paymentMethod = (map["paymentMethod"] as? String) ?: "UPI / Card",
                status = (map["status"] as? String) ?: "Confirmed",
                createdAt = (map["createdAt"] as? String) ?: ""
            )
        }
    }
}
