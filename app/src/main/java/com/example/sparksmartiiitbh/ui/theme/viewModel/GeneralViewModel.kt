package com.example.sparksmartiiitbh.ui.theme.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sparksmartiiitbh.ui.theme.Model.Complaint
import com.example.sparksmartiiitbh.ui.theme.Model.ComplaintStatus
import com.example.sparksmartiiitbh.ui.theme.data.ComplaintRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeneralViewModel @Inject constructor(
    private val complaintRepository: ComplaintRepository
) : ViewModel() {

    private val _userType = MutableStateFlow("")
    val userType = _userType.asStateFlow()

    private val _applicantName = MutableStateFlow("")
    val applicantName = _applicantName.asStateFlow()

    private val _building = MutableStateFlow("")
    val building = _building.asStateFlow()

    private val _floor = MutableStateFlow("")
    val floor = _floor.asStateFlow()

    private val _room = MutableStateFlow("")
    val room = _room.asStateFlow()

    private val _appliance = MutableStateFlow("")
    val appliance = _appliance.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _submitting = MutableStateFlow(false)
    val submitting = _submitting.asStateFlow()

    private val _myComplaints = MutableStateFlow<List<Complaint>>(emptyList())
    val myComplaints = _myComplaints.asStateFlow()
    private var myComplaintsListener: ListenerRegistration? = null

    fun setUserType(value: String) { _userType.value = value }
    fun setApplicantName(value: String) { _applicantName.value = value }
    fun setBuilding(value: String) { _building.value = value }
    fun setFloor(value: String) { _floor.value = value }
    fun setRoom(value: String) { _room.value = value }
    fun setAppliance(value: String) { _appliance.value = value }
    fun setDescription(value: String) { _description.value = value }

    fun submitComplaint() {
        if (_applicantName.value.isBlank() || _userType.value.isBlank() || _building.value.isBlank() || _floor.value.isBlank() || _appliance.value.isBlank() || _description.value.isBlank()) {
            return
        }
        viewModelScope.launch {
            _submitting.value = true
            try {
                complaintRepository.createComplaint(
                    Complaint(
                        applicantName = _applicantName.value,
                        userType = _userType.value,
                        building = _building.value,
                        floor = _floor.value,
                        room = _room.value.ifBlank { null },
                        appliance = _appliance.value,
                        description = _description.value,
                        status = ComplaintStatus.Registered
                    )
                )
                loadMyComplaints()
            } finally {
                _submitting.value = false
            }
        }
    }

    fun loadMyComplaints() {
        myComplaintsListener?.remove()
        myComplaintsListener = complaintRepository.listenComplaintsForCurrentUser { list ->
            _myComplaints.value = list
        }
    }

    override fun onCleared() {
        super.onCleared()
        myComplaintsListener?.remove()
    }
}
