package com.example.generatorapp.Pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun DateRangePickerDialog(
    onDismissRequest: () -> Unit,
    onDateRangeSelected: (Date, Date) -> Unit
) {
    var showStartPicker by remember { mutableStateOf(true) }
    var fromDate by remember { mutableStateOf(Date()) }
    var toDate by remember { mutableStateOf(Date()) }
    var pickerStep by remember { mutableStateOf(0) }
    var isConfirmed by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = {
                    if (pickerStep == 0) {
                        pickerStep = 1
                    } else {
                        isConfirmed = true
                        onDateRangeSelected(fromDate, toDate)
                        onDismissRequest()
                    }
                }
            ) {
                Text(if (pickerStep == 0) "Next" else "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Cancel") }
        },
        title = { Text("Select Date Range") },
        text = {
            Column {
                Text(if (pickerStep == 0) "Pick start date:" else "Pick end date:", modifier = Modifier.padding(bottom = 8.dp))
                SingleDatePicker(
                    initialDate = if (pickerStep == 0) fromDate else toDate,
                    onDateSelected = {
                        if (pickerStep == 0) fromDate = it else toDate = it
                    }
                )
                if (pickerStep == 1 && toDate.before(fromDate)) {
                    Text(
                        text = "End date must be after start date!",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleDatePicker(
    initialDate: Date,
    onDateSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance().apply { time = initialDate }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = calendar.timeInMillis
    )
    DatePicker(
        state = datePickerState,
        modifier = Modifier.wrapContentSize(),
        showModeToggle = false
    )
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            onDateSelected(Date(it))
        }
    }
}
