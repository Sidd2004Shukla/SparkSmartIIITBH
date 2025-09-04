package com.example.sparksmartiiitbh.ui.theme.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.example.sparksmartiiitbh.ui.theme.Model.ComplaintStatus
import com.example.sparksmartiiitbh.ui.theme.viewModel.WorkerComplaintsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerComplaintsScreen(
    navController: NavController,
    viewModel: WorkerComplaintsViewModel = hiltViewModel()
) {
    val assigned = viewModel.assigned.collectAsState().value
    val available = viewModel.available.collectAsState().value
    LaunchedEffect(Unit) {
        viewModel.loadAssigned(); viewModel.loadAvailable()
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text("Complaints") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Available Complaints")
            available.forEach { c ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "${c.building} / Floor ${c.floor}${c.room?.let { ", Room $it" } ?: ""}")
                        Text(text = "${c.appliance}: ${c.description}")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = { viewModel.acceptByMe(c.id) }) { Text("Accept") }
                        }
                    }
                }
            }

            Text("My Assigned Complaints")
            assigned.forEach { c ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "${c.building} / Floor ${c.floor}${c.room?.let { ", Room $it" } ?: ""}")
                        Text(text = "${c.appliance}: ${c.description}")
                        Text(text = "Status: ${c.status}")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = { viewModel.accept(c.id) }, enabled = c.status == ComplaintStatus.Registered) { Text("Accept") }
                            Button(onClick = { viewModel.resolve(c.id) }, enabled = c.status != ComplaintStatus.Resolved) { Text("Resolve") }
                        }
                    }
                }
            }
        }
    }
}


