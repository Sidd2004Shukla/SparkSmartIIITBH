@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.sparksmartiiitbh.ui.theme.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.runtime.livedata.observeAsState
import com.example.sparksmartiiitbh.ui.theme.Model.AuthState
import com.example.sparksmartiiitbh.ui.theme.viewModel.AuthViewModel
import com.example.sparksmartiiitbh.ui.theme.viewModel.GeneralViewModel


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun General(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    viewModel: GeneralViewModel = hiltViewModel()
) {
    val userType by viewModel.userType.collectAsState()
    val applicantName by viewModel.applicantName.collectAsState()
    val building by viewModel.building.collectAsState()
    val floor by viewModel.floor.collectAsState()
    val room by viewModel.room.collectAsState()
    val appliance by viewModel.appliance.collectAsState()
    val description by viewModel.description.collectAsState()
    val myComplaints by viewModel.myComplaints.collectAsState()
    val submitting by viewModel.submitting.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadMyComplaints() }
    val authState = authViewModel.authState.observeAsState()
    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Unauthenticated) {
            navController.navigate("login")
        }
    }

    val (selectedTab, setSelectedTab) = remember { mutableStateOf("register") }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complaint System") },
                actions = {
                    IconButton(onClick = { authViewModel.signOut() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = selectedTab == "register", onClick = { setSelectedTab("register") }, label = { Text("Complain") }, icon = {})
                NavigationBarItem(selected = selectedTab == "active", onClick = { setSelectedTab("active") }, label = { Text("Active") }, icon = {})
                NavigationBarItem(selected = selectedTab == "history", onClick = { setSelectedTab("history") }, label = { Text("Previous") }, icon = {})
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (selectedTab == "register") {
                Text("Register Complaint")

                OutlinedTextField(
                    value = applicantName,
                    onValueChange = viewModel::setApplicantName,
                    label = { Text("Your Name") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )

                DropdownRow(
                    label = "Who are you?",
                    options = listOf("faculty", "student"),
                    selected = userType,
                    onSelected = viewModel::setUserType
                )

                DropdownRow(
                    label = "Building",
                    options = listOf("Academic", "Library", "Administration", "Hostel", "Faculty HQ"),
                    selected = building,
                    onSelected = viewModel::setBuilding
                )

                OutlinedTextField(
                    value = floor,
                    onValueChange = viewModel::setFloor,
                    label = { Text("Floor") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                if (building == "Hostel") {
                    OutlinedTextField(
                        value = room,
                        onValueChange = viewModel::setRoom,
                        label = { Text("Room (Hostel only)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )
                }

                DropdownRow(
                    label = "Appliance",
                    options = listOf("Light rod", "Fan", "Lamp", "Switchboard", "AC"),
                    selected = appliance,
                    onSelected = viewModel::setAppliance
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = viewModel::setDescription,
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )

                Button(onClick = { viewModel.submitComplaint() }, enabled = !submitting) { Text("Submit Complaint") }
            } else if (selectedTab == "active") {
                Text("Active Complaints")
                myComplaints.filter { it.status.name != "Resolved" }.forEach { c ->
                    val containerColor = when (c.status.name) {
                        "Accepted" -> androidx.compose.ui.graphics.Color(0xFFE3F2FD)
                        else -> androidx.compose.ui.graphics.Color(0xFFFFEBEE)
                    }
                    Card(modifier = Modifier.fillMaxWidth(), colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = containerColor)) {
                        Column(modifier = Modifier
                            .padding(12.dp)
                            .clickable { navController.navigate("complaint/${c.id}") }) {
                            Text("${c.building} / Floor ${c.floor}${c.room?.let { ", Room $it" } ?: ""}")
                            Text("${c.appliance}: ${c.description}")
                            ComplaintStepper(status = c.status.name)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                Text("Previous Complaints")
                myComplaints.filter { it.status.name == "Resolved" }.forEach { c ->
                    val containerColor = when (c.status.name) {
                        "Resolved" -> androidx.compose.ui.graphics.Color(0xFFE8F5E9)
                        "Accepted" -> androidx.compose.ui.graphics.Color(0xFFE3F2FD)
                        else -> androidx.compose.ui.graphics.Color(0xFFFFEBEE)
                    }
                    Card(modifier = Modifier.fillMaxWidth(), colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = containerColor)) {
                        Column(modifier = Modifier
                            .padding(12.dp)
                            .clickable { navController.navigate("complaint/${c.id}") }) {
                            Text("${c.building} / Floor ${c.floor}${c.room?.let { ", Room $it" } ?: ""}")
                            Text("${c.appliance}: ${c.description}")
                            ComplaintStepper(status = c.status.name)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownRow(label: String, options: List<String>, selected: String, onSelected: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label)
        var expanded = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded.value, onExpandedChange = { expanded.value = it }) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                label = { Text(label) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
                options.forEach { opt ->
                    DropdownMenuItem(text = { Text(opt) }, onClick = {
                        onSelected(opt)
                        expanded.value = false
                    })
                }
            }
        }
    }
}

@Composable
private fun ComplaintStepper(status: String) {
    val steps = listOf("Registered", "Accepted", "Resolved")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        steps.forEach { s ->
            val active = steps.indexOf(s) <= steps.indexOf(status)
            Text(if (active) "[${s}]" else s)
        }
    }
}
