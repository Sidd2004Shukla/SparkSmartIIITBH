package com.example.sparksmartiiitbh.ui.theme.Utility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen(
    fuelLevel: Int,
    monthlyUsageTime: Long,
    monthlyFuelConsumed: Int
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Current Fuel Level", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
            CircularProgressIndicator(
                progress = { fuelLevel.toFloat() / 200 },
                modifier = Modifier.size(240.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.LightGray,
                strokeWidth = 12.dp
            )
            Text("$fuelLevel L / 200L", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Text("Monthly Generator Usage", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "${monthlyUsageTime / 3600}h ${(monthlyUsageTime % 3600) / 60}m",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text("Monthly Fuel Consumption", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("$monthlyFuelConsumed L / 500L", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}