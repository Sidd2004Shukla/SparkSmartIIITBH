package com.example.sparksmartiiitbh.ui.theme.Screens
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sparksmartiiitbh.ui.theme.viewModel.AdminViewModel
import com.example.sparksmartiiitbh.ui.theme.viewModel.AuthViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.generatorapp.Pages.DateRangePickerDialog
import com.example.sparksmartiiitbh.ui.theme.Model.AuthState
import com.example.sparksmartiiitbh.ui.theme.Utility.DashboardScreen
import com.example.sparksmartiiitbh.ui.theme.viewModel.AdminComplaintsViewModel
import com.example.sparksmartiiitbh.ui.theme.Model.ComplaintStatus
import com.example.sparksmartiiitbh.ui.theme.Model.Complaint
import com.example.sparksmartiiitbh.ui.theme.Utility.LogScreen
import com.example.sparksmartiiitbh.ui.theme.Utility.PdfGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AdminScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    adminViewModel: AdminViewModel = hiltViewModel(),
    adminComplaintsViewModel: AdminComplaintsViewModel = hiltViewModel(),
    onRequestPermissions: () -> Unit
)
{
    val authState = authViewModel.authState.observeAsState()
    val fuelLevel by adminViewModel.fuelLevel.collectAsState()
    val monthlyUsageTime by adminViewModel.monthlyUsageTime.collectAsState()
    val monthlyFuelConsumed by adminViewModel.monthlyFuelConsumed.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedScreen by remember { mutableStateOf("Dashboard") }

    val context = LocalContext.current
    val pdfGenerator = remember { PdfGenerator(context) }

    var showDateRangePicker by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf(Calendar.getInstance().apply { add(Calendar.MONTH, -1) }.time) }
    var endDate by remember { mutableStateOf(Calendar.getInstance().time) }
    val isExporting by adminViewModel.isExporting.collectAsState()
    var logsToExport by remember { mutableStateOf<List<String>?>(null) }

    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    // Handle PDF export when logsToExport changes
    LaunchedEffect(logsToExport) {
        logsToExport?.let { logs ->
            adminViewModel._isExporting.value = true
            when (selectedScreen) {
                "Fuel Logs" -> pdfGenerator.generateFuelLogsPdf(
                    logs,
                    dateFormatter.format(startDate),
                    dateFormatter.format(endDate)
                ) { file ->
                    adminViewModel._isExporting.value = false
                    if (file != null) {
                        Toast.makeText(
                            context,
                            "PDF saved to Downloads",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Failed to generate PDF",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                "Usage Logs" -> pdfGenerator.generateUsageLogsPdf(
                    logs,
                    dateFormatter.format(startDate),
                    dateFormatter.format(endDate)
                ) { file ->
                    adminViewModel._isExporting.value = false
                    if (file != null) {
                        Toast.makeText(
                            context,
                            "PDF saved to Downloads",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Failed to generate PDF",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            logsToExport = null
        }
    }

    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Unauthenticated) {
            navController.navigate("login")
        }
    }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.fillMaxHeight()) {
                    // Main menu content
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Menu", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        NavigationDrawerItem(
                            label = { Text("Dashboard") },
                            selected = selectedScreen == "Dashboard",
                            onClick = {
                                selectedScreen = "Dashboard"
                                scope.launch { drawerState.close() }
                            }
                        )
                        NavigationDrawerItem(
                            label = { Text("Fuel Logs") },
                            selected = selectedScreen == "Fuel Logs",
                            onClick = {
                                selectedScreen = "Fuel Logs"
                                scope.launch { drawerState.close() }
                            }
                        )
                        NavigationDrawerItem(
                            label = { Text("Usage Logs") },
                            selected = selectedScreen == "Usage Logs",
                            onClick = {
                                selectedScreen = "Usage Logs"
                                scope.launch { drawerState.close() }
                            }
                        )
                        NavigationDrawerItem(
                            label = { Text("Complaints") },
                            selected = selectedScreen == "Complaints",
                            onClick = {
                                selectedScreen = "Complaints"
                                scope.launch { drawerState.close() }
                            }
                        )
                        NavigationDrawerItem(
                            label = { Text("Logout") },
                            selected = false,
                            onClick = { authViewModel.signOut() }
                        )
                    }

                    // Footer with credits
                    Column {
                        HorizontalDivider()
                        Text(
                            text = "Developed by Siddharth Shukla",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        drawerState = drawerState
    ) {
        // Your main content here

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(selectedScreen) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        if (selectedScreen == "Fuel Logs" || selectedScreen == "Usage Logs") {
                            IconButton(
                                onClick = {
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                        onRequestPermissions()
                                    } else {
                                        showDateRangePicker = true
                                    }

                                },
                                enabled = !isExporting
                            ) {
                                Icon(Icons.Default.Download, contentDescription = "Export PDF")
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (showDateRangePicker) {
                    DateRangePickerDialog(
                        onDismissRequest = { showDateRangePicker = false },
                        onDateRangeSelected = { fromDate, toDate ->
                            startDate = fromDate
                            endDate = toDate
                            logsToExport = when (selectedScreen) {
                                "Fuel Logs" -> adminViewModel.filterFuelLogsByDate(startDate, endDate)
                                "Usage Logs" -> adminViewModel.filterUsageLogsByDate(startDate, endDate)
                                else -> emptyList()
                            }
                        }
                    )
                }

                when (selectedScreen) {
                    "Dashboard" -> DashboardScreen(
                        fuelLevel = fuelLevel,
                        monthlyUsageTime = monthlyUsageTime,
                        monthlyFuelConsumed = monthlyFuelConsumed
                    )
                    "Fuel Logs" -> LogScreen(
                        title = "Fuel Logs",
                        logs = adminViewModel.fuelLogs.collectAsState().value,
                        isFuelLog = true
                    )
                    "Usage Logs" -> LogScreen(
                        title = "Generator Usage Logs",
                        logs = adminViewModel.usageLogs.collectAsState().value,
                        isFuelLog = false
                    )
                    "Complaints" -> ComplaintsAdminSection(adminComplaintsViewModel)
                }
            }
        }
    }
}

@Composable
private fun ComplaintsAdminSection(vm: AdminComplaintsViewModel) {
    val complaints = vm.allComplaints.collectAsState().value
    LaunchedEffect(Unit) { vm.loadComplaints() }
    val filter = vm.statusFilter.collectAsState().value
    val filtered = complaints.filter { c -> filter == null || c.status == filter }
    val context = LocalContext.current
    val pdf = remember { PdfGenerator(context) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = { vm.setStatusFilter(null) }) { Text("All") }
            TextButton(onClick = { vm.setStatusFilter(ComplaintStatus.Registered) }) { Text("Registered") }
            TextButton(onClick = { vm.setStatusFilter(ComplaintStatus.Accepted) }) { Text("Accepted") }
            TextButton(onClick = { vm.setStatusFilter(ComplaintStatus.Resolved) }) { Text("Resolved") }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { pdf.generateComplaintsPdf(filtered) { } }) {
                Icon(Icons.Default.Download, contentDescription = "Export Complaints")
            }
        }
        filtered.forEach { c ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("${c.userType} | ${c.building} / Fl ${c.floor}${c.room?.let { ", Rm $it" } ?: ""}")
                    Text("${c.appliance}: ${c.description}")
                    Text("Status: ${c.status}")
                }
            }
        }
        if (filtered.isEmpty()) {
            Text("No complaints")
        }
    }
}
