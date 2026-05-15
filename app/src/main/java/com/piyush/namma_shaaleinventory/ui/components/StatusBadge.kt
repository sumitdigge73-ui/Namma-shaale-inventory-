package com.piyush.namma_shaaleinventory.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StatusBadge(status: String) {
    val color = when (status) {
        "Green" -> Color(0xFFC8E6C9)
        "Yellow" -> Color(0xFFFFF9C4)
        "Red", "Missing" -> Color(0xFFFFCDD2)
        else -> Color.LightGray
    }
    val textColor = when (status) {
        "Green" -> Color(0xFF1B5E20)
        "Yellow" -> Color(0xFFF57F17)
        "Red", "Missing" -> Color(0xFFB71C1C)
        else -> Color.DarkGray
    }
    Surface(
        color = color,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}
