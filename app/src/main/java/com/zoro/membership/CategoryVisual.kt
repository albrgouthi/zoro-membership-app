package com.zoro.membership

import androidx.compose.ui.graphics.Color

data class CategoryVisual(val emoji: String, val color: Color)

fun visualFor(iconKey: String?): CategoryVisual = when (iconKey) {
    "restaurant" -> CategoryVisual("🍽️", Color(0xFFE07A3F))
    "health" -> CategoryVisual("🏥", Color(0xFF3F8FE0))
    "shopping" -> CategoryVisual("🛍️", Color(0xFFB03FE0))
    "car" -> CategoryVisual("🚗", Color(0xFF3FB0E0))
    "fitness" -> CategoryVisual("💪", Color(0xFF3FE07A))
    else -> CategoryVisual("⭐", Color(0xFF1B8A5A))
}
