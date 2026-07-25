package com.example.rent.data.model

data class UserProfile(
    val userId: String = "",
    val name: String = "Guest User",
    val phone: String = "",
    val location: String = "Hyderabad",
    val avatar: String = ""
) {
    companion object {
        fun fromMap(uId: String, map: Map<String, Any?>): UserProfile {
            return UserProfile(
                userId = uId,
                name = (map["name"] as? String) ?: "Guest User",
                phone = (map["phone"] as? String) ?: "",
                location = (map["location"] as? String) ?: "Hyderabad",
                avatar = (map["avatar"] as? String) ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80"
            )
        }
    }
}

data class AppNotification(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = "info",
    val createdAt: String = "",
    val read: Boolean = false
) {
    companion object {
        fun fromMap(docId: String, map: Map<String, Any?>): AppNotification {
            return AppNotification(
                id = docId,
                title = (map["title"] as? String) ?: "",
                body = (map["body"] as? String) ?: "",
                type = (map["type"] as? String) ?: "info",
                createdAt = (map["createdAt"] as? String) ?: "",
                read = (map["read"] as? Boolean) ?: false
            )
        }
    }
}
