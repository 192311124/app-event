package com.example.rent.data.model

data class EventCategory(
    val id: String = "",
    val name: String = "",
    val icon: String = "🎉",
    val desc: String = ""
) {
    companion object {
        fun fromMap(docId: String, map: Map<String, Any?>): EventCategory {
            return EventCategory(
                id = docId,
                name = (map["name"] as? String) ?: "",
                icon = (map["icon"] as? String) ?: "🎉",
                desc = (map["desc"] as? String) ?: ""
            )
        }
    }
}
