package com.example.sparksmartiiitbh.ui.theme.Model

import com.google.firebase.Timestamp

data class Complaint(
    val id: String = "",
    val userId: String = "",
    val applicantName: String = "",
    val userType: String = "", // faculty or student
    val building: String = "",
    val floor: String = "",
    val room: String? = null, // hostel only
    val appliance: String = "",
    val description: String = "",
    val status: ComplaintStatus = ComplaintStatus.Registered,
    val assignedWorkerId: String? = null,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)

enum class ComplaintStatus { Registered, Accepted, Resolved }


