package com.example.sparksmartiiitbh.ui.theme.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sparksmartiiitbh.ui.theme.viewModel.ComplaintDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintDetailScreen(
    navController: NavController,
    complaintId: String,
    viewModel: ComplaintDetailViewModel = hiltViewModel()
) {
    val complaint = viewModel.complaint.collectAsState().value
    LaunchedEffect(complaintId) { viewModel.start(complaintId) }
    Scaffold(topBar = { TopAppBar(title = { Text("Complaint Detail") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            complaint?.let { c ->
                Text("User: ${c.userType}")
                Text("Location: ${c.building} / Floor ${c.floor}${c.room?.let { ", Room $it" } ?: ""}")
                Text("Appliance: ${c.appliance}")
                Text("Description: ${c.description}")
                Text("Status: ${c.status}")
            } ?: run {
                Text("Loading...")
            }
        }
    }
}


