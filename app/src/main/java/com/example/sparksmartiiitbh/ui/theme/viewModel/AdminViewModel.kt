package com.example.sparksmartiiitbh.ui.theme.viewModel
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
@HiltViewModel
class AdminViewModel  @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {
    private val _fuelLevel = MutableStateFlow(0)
    val fuelLevel = _fuelLevel.asStateFlow()

    private val _fuelLogs = MutableStateFlow<List<String>>(emptyList())
    val fuelLogs = _fuelLogs.asStateFlow()

    private val _usageLogs = MutableStateFlow<List<String>>(emptyList())
    val usageLogs = _usageLogs.asStateFlow()

    private val _totalUsageTime = MutableStateFlow(0L)
    val totalUsageTime = _totalUsageTime.asStateFlow()

    private val _monthlyUsageTime = MutableStateFlow(0L)
    val monthlyUsageTime = _monthlyUsageTime.asStateFlow()

    private val _monthlyFuelConsumed = MutableStateFlow(0)
    val monthlyFuelConsumed = _monthlyFuelConsumed.asStateFlow()

    init {
        fetchFuelData()
        fetchUsageLogs()
        fetchMonthlyUsage()
    }

    private fun fetchFuelData() {
        db.collection("fuel_records")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshots, _ ->
                if (snapshots != null && !snapshots.isEmpty) {
                    val latestRecord = snapshots.documents[0]
                    _fuelLevel.value = latestRecord.getLong("fuelLevel")?.toInt() ?: 0
                }
            }

        db.collection("fuel_records")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, _ ->
                val logs = snapshots?.documents?.map { doc ->
                    val timestamp = getFormattedTimestamp(doc.get("timestamp"))
                    val fuelChange = doc.getLong("fuelChange") ?: 0
                    val fuelRemaining = doc.getLong("fuelLevel") ?: 0
                    val type = doc.getString("type") ?: "Unknown"
                    "Time: $timestamp | $type $fuelChange L | Remaining: $fuelRemaining L"
                } ?: emptyList()
                _fuelLogs.value = logs
            }
    }

    private fun fetchUsageLogs() {
        db.collection("generator_usage")
            .orderBy("startTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, _ ->
                val logs = snapshots?.documents?.map { doc ->
                    val start = getFormattedTimestamp(doc.get("startTime"))
                    val end = getFormattedTimestamp(doc.get("endTime"))
                    val duration = doc.getLong("duration") ?: 0L
                    "Start: $start | End: $end | Duration: ${duration / 3600}h ${(duration % 3600) / 60}m"
                } ?: emptyList()
                _usageLogs.value = logs
            }

        db.collection("generator_usage")
            .addSnapshotListener { snapshots, _ ->
                val totalTime = snapshots?.documents?.sumOf {
                    it.getLong("duration") ?: 0L
                } ?: 0L
                _totalUsageTime.value = totalTime
            }
    }

    private fun fetchMonthlyUsage() {
        val calendar = Calendar.getInstance()
        val startOfMonth = calendar.apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        // Convert to string format for lexicographic comparison
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedStartOfMonth = sdf.format(startOfMonth)

        db.collection("generator_usage")
            .whereGreaterThanOrEqualTo("startTime", formattedStartOfMonth)
            .addSnapshotListener { snapshots: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    Log.e("Firestore", "Error fetching generator usage", error)
                    return@addSnapshotListener
                }

                val totalTime = snapshots?.documents?.sumOf {
                    it.getLong("duration") ?: 0L
                } ?: 0L
                _monthlyUsageTime.value = totalTime
                Log.d("Firestore", "Monthly Generator Usage: $totalTime seconds")
            }

        // Fetch monthly fuel consumption
        db.collection("fuel_records")
            .whereGreaterThanOrEqualTo("timestamp", formattedStartOfMonth)
            .whereEqualTo("type", "consumed")
            .addSnapshotListener { snapshots: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    Log.e("Firestore", "Error fetching fuel consumption", error)
                    return@addSnapshotListener
                }

                val totalFuel = snapshots?.documents?.sumOf {
                    it.getLong("fuelChange")?.toInt() ?: 0
                } ?: 0
                _monthlyFuelConsumed.value = totalFuel
                Log.d("Firestore", "Monthly Fuel Consumed: $totalFuel liters")
            }
    }


    private fun getFormattedTimestamp(value: Any?): String {
        return when (value) {
            is Timestamp -> {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                sdf.format(value.toDate())
            }
            is String -> value
            else -> "Unknown Time"
        }
    }
    // Add these to your AdminViewModel
    val _isExporting = MutableStateFlow(false)
    val isExporting = _isExporting.asStateFlow()

    fun filterFuelLogsByDate(startDate: Date, endDate: Date): List<String> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Only date part
        return _fuelLogs.value.filter { log ->
            val timePart = log.split("|").firstOrNull()?.replace("Time:", "")?.trim() ?: return@filter false
            try {
                val logDate = sdf.parse(sdf.format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(timePart) ?: return@filter false))
                val startDateOnly = sdf.parse(sdf.format(startDate)) ?: return@filter false
                val endDateOnly = sdf.parse(sdf.format(endDate)) ?: return@filter false
                logDate in startDateOnly..endDateOnly
            } catch (e: Exception) {
                false
            }
        }
    }

    fun filterUsageLogsByDate(startDate: Date, endDate: Date): List<String> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Only date part
        return _usageLogs.value.filter { log ->
            val startTimePart = log.split("|").firstOrNull()?.replace("Start:", "")?.trim() ?: return@filter false
            try {
                val logDate = sdf.parse(sdf.format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(startTimePart) ?: return@filter false))
                val startDateOnly = sdf.parse(sdf.format(startDate)) ?: return@filter false
                val endDateOnly = sdf.parse(sdf.format(endDate)) ?: return@filter false
                logDate in startDateOnly..endDateOnly
            } catch (e: Exception) {
                false
            }
        }
    }

}