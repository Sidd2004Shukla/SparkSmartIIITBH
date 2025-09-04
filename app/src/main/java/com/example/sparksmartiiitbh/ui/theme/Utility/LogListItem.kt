package com.example.sparksmartiiitbh.ui.theme.Utility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LogListItem(parts: List<String>, isFuelLog: Boolean, index: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFuelLog) {
                if (parts.getOrNull(1)?.contains("added") == true) Color(0xFFE8F5E9)
                else Color(0xFFFFEBEE)
            } else {
                Color(0xFFE3F2FD)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$index)",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF673AB7),
                modifier = Modifier.width(24.dp)
            )

            Text(
                text = parts.getOrNull(0) ?: "",
                fontSize = 14.sp,
                color = Color(0xFF673AB7),
                modifier = Modifier.weight(1f)
            )

            Text(
                text = parts.getOrNull(1) ?: "",
                fontSize = 14.sp,
                fontWeight = if (isFuelLog) FontWeight.Bold else FontWeight.Normal,
                color = if (isFuelLog && parts.getOrNull(1)?.contains("added") == true) {
                    Color(0xFF2E7D32)
                } else if (isFuelLog) {
                    Color(0xFFC62828)
                } else {
                    Color(0xFF673AB7)
                },
                modifier = Modifier.weight(1f)
            )

            Text(
                text = parts.getOrNull(2) ?: "",
                fontSize = 14.sp,
                color = Color(0xFF673AB7),
                modifier = Modifier.weight(1f)
            )
        }
    }
}