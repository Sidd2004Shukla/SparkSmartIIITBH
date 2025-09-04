package com.example.sparksmartiiitbh.ui.theme.viewModel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WorkerViewModel @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {

    private var stopwatchJob: Job? = null
    private var fuelListener: ListenerRegistration? = null

    var elapsedTime by mutableStateOf(0L)
        private set
    var startTime by mutableStateOf("")
        private set
    var endTime by mutableStateOf("")
        private set
    var fuelAdded by mutableStateOf("")
        private set
    var fuelConsumed by mutableStateOf("")
        private set
    var fuelLevel by mutableStateOf(0)
        private set
    var showConsumptionDialog by mutableStateOf(false)
        private set
    private var startTimestamp: Long = 0L

    init {
        fetchFuelLevelFromFirebase()
        listenForFuelUpdates()
    }

    fun fetchFuelLevelFromFirebase() {
        db.collection("fuel_records")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val latestRecord = documents.documents[0]
                    fuelLevel = latestRecord.getLong("fuelLevel")?.toInt() ?: 0
                }
            }
            .addOnFailureListener {
                println("Failed to fetch fuel level")
            }
    }

    private fun listenForFuelUpdates() {
        fuelListener?.remove()

        fuelListener = db.collection("fuel_records")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    println("Fuel level listener failed: $error")
                    return@addSnapshotListener
                }

                snapshots?.documents?.firstOrNull()?.let { latestRecord ->
                    fuelLevel = latestRecord.getLong("fuelLevel")?.toInt() ?: 0
                }
            }
    }

    fun startTimer() {
        startTimestamp = System.currentTimeMillis()
        startTime = formatTimestamp(startTimestamp)
        stopwatchJob?.cancel()
        stopwatchJob = viewModelScope.launch {
            while (true) {
                elapsedTime = System.currentTimeMillis() - startTimestamp
                delay(1000)
            }
        }
    }

    fun stopTimer() {
        stopwatchJob?.cancel()
        val endTimestamp = System.currentTimeMillis()
        endTime = formatTimestamp(endTimestamp)
        showConsumptionDialog = true
    }

    fun updateFuelAdded(value: String) {
        fuelAdded = value
    }

    fun updateFuelConsumed(value: String) {
        fuelConsumed = value
    }

    fun submitFuelConsumption(context: Context) {
        val consumed = fuelConsumed.toIntOrNull() ?: 0
        if (consumed <= 0)  {
            Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }
        if (consumed > fuelLevel) {
            Toast.makeText(context, "Insufficient fuel level", Toast.LENGTH_SHORT).show()
            return
        }

        val newFuelLevel = (fuelLevel - consumed).coerceAtLeast(0)
        storeUsageToFirebase(consumed, newFuelLevel)
        showConsumptionDialog = false
        fuelConsumed = ""
        elapsedTime = 0L
        startTime = ""
        endTime = ""
    }

    fun cancelConsumption() {
        showConsumptionDialog = false
        fuelConsumed = ""
        elapsedTime = 0L
        startTime = ""
        endTime = ""
    }

    fun addFuel(context: Context) {
        val fuelToAdd = fuelAdded.toIntOrNull() ?: 0
        if (fuelToAdd <= 0) {
            Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val newFuelLevel = fuelLevel + fuelToAdd
        if (newFuelLevel > 200) {
            Toast.makeText(context, "Cannot add fuel beyond 500L!", Toast.LENGTH_SHORT).show()
            return
        }

        storeFuelAdditionToFirebase(fuelToAdd, newFuelLevel)
        fuelAdded = ""
        Toast.makeText(context, "Fuel added successfully!", Toast.LENGTH_SHORT).show()
    }

    private fun storeUsageToFirebase(fuelConsumed: Int, updatedFuelLevel: Int) {
        if (startTime.isBlank() || endTime.isBlank()) {
            println("Error: Start or End time is missing")
            return
        }

        val duration = elapsedTime / 1000
        val usageData = hashMapOf(
            "startTime" to startTime,
            "endTime" to endTime,
            "duration" to duration,
            "fuelConsumed" to fuelConsumed
        )

        db.collection("generator_usage").add(usageData)
            .addOnSuccessListener {
                println("Generator usage stored successfully")
                storeFuelConsumptionToFirebase(fuelConsumed, updatedFuelLevel)
            }
            .addOnFailureListener {
                println("Failed to store generator usage")
            }
    }

    private fun storeFuelConsumptionToFirebase(fuelConsumed: Int, updatedFuelLevel: Int) {
        val fuelData = hashMapOf(
            "timestamp" to formatTimestamp(System.currentTimeMillis()),
            "fuelChange" to fuelConsumed,
            "fuelLevel" to updatedFuelLevel,
            "type" to "consumed"
        )

        db.collection("fuel_records").add(fuelData)
            .addOnSuccessListener {
                println("Fuel consumption recorded successfully")
            }
            .addOnFailureListener {
                println("Failed to store fuel consumption record")
            }
    }

    private fun storeFuelAdditionToFirebase(fuelChange: Int, updatedFuelLevel: Int) {
        val fuelData = hashMapOf(
            "timestamp" to formatTimestamp(System.currentTimeMillis()),
            "fuelChange" to fuelChange,
            "fuelLevel" to updatedFuelLevel,
            "type" to "added"
        )

        db.collection("fuel_records").add(fuelData)
            .addOnSuccessListener {
                println("Fuel addition recorded successfully")
            }
            .addOnFailureListener {
                println("Failed to store fuel record")
            }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    override fun onCleared() {
        super.onCleared()
        fuelListener?.remove()
        stopwatchJob?.cancel()
    }
}