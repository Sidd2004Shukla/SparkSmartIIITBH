package com.example.sparksmartiiitbh.ui.theme.Screens
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sparksmartiiitbh.ui.theme.Model.AuthState
import com.example.sparksmartiiitbh.ui.theme.viewModel.AuthViewModel
import com.example.sparksmartiiitbh.ui.theme.viewModel.WorkerViewModel
import com.example.sparksmartiiitbh.ui.theme.viewModel.WorkerComplaintsViewModel
import com.example.sparksmartiiitbh.ui.theme.Model.ComplaintStatus
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    workerViewModel: WorkerViewModel = hiltViewModel(),
    workerComplaintsViewModel: WorkerComplaintsViewModel = hiltViewModel()
) {
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope() // Add this line to get the coroutine scope

    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Unauthenticated) {
            navController.navigate("login")
        }
    }

    LaunchedEffect(Unit) {
        workerViewModel.fetchFuelLevelFromFirebase()
        workerComplaintsViewModel.loadAssigned()
    }

    if (workerViewModel.showConsumptionDialog) {
        AlertDialog(
            onDismissRequest = { workerViewModel.cancelConsumption() },
            title = { Text("Fuel Consumption") },
            text = {
                Column {
                    Text("Elapsed Time: ${workerViewModel.elapsedTime / 1000} sec")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = workerViewModel.fuelConsumed,
                        onValueChange = { workerViewModel.updateFuelConsumed(it) },
                        label = { Text("Fuel Consumed (liters)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { workerViewModel.submitFuelConsumption(context) }
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                Button(
                    onClick = { workerViewModel.cancelConsumption() }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.fillMaxHeight()) {
                    // Main menu content
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            " Menu",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                        HorizontalDivider()
                        NavigationDrawerItem(
                            label = { Text("Complaints") },
                            selected = false,
                            onClick = { navController.navigate("workerComplaints") }
                        )
                        NavigationDrawerItem(
                            label = { Text("Logout") },
                            selected = false,
                            onClick = { authViewModel.signOut() })
                    }
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
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Fuel Monitor") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
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
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(240.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { workerViewModel.fuelLevel.toFloat() / 200 },
                        modifier = Modifier.size(240.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.LightGray,
                        strokeCap = StrokeCap.Round,
                        strokeWidth = 12.dp
                    )
                    Text(
                        text = "${workerViewModel.fuelLevel}L / 200L",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Elapsed Time: ${workerViewModel.elapsedTime / 1000} sec",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )

                if (workerViewModel.startTime.isNotEmpty()) {
                    Text("Start Time: ${workerViewModel.startTime}", fontSize = 16.sp)
                }
                if (workerViewModel.endTime.isNotEmpty()) {
                    Text("End Time: ${workerViewModel.endTime}", fontSize = 16.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { workerViewModel.startTimer() },
                        enabled = workerViewModel.elapsedTime == 0L
                    ) {
                        Text("Start")
                    }

                    Button(
                        onClick = { workerViewModel.stopTimer() },
                        enabled = workerViewModel.elapsedTime > 0
                    ) {
                        Text("Stop")
                    }
                }

                Button(onClick = { navController.navigate("workerComplaints") }) { Text("View Assigned Complaints") }

                OutlinedTextField(
                    value = workerViewModel.fuelAdded,
                    onValueChange = { workerViewModel.updateFuelAdded(it) },
                    label = { Text("Enter Fuel (liters)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done // Change Enter key to "Done"
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { workerViewModel.addFuel(context) },
                    enabled = workerViewModel.fuelAdded.isNotEmpty()
                ) {
                    Text("Add Fuel")
                }
            }
        }
    }
}