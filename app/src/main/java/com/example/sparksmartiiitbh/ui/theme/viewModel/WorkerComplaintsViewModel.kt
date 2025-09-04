package com.example.sparksmartiiitbh.ui.theme.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sparksmartiiitbh.ui.theme.Model.Complaint
import com.example.sparksmartiiitbh.ui.theme.Model.ComplaintStatus
import com.example.sparksmartiiitbh.ui.theme.data.ComplaintRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.firestore.ListenerRegistration

@HiltViewModel
class WorkerComplaintsViewModel @Inject constructor(
    private val complaintRepository: ComplaintRepository
) : ViewModel() {
    private val _assigned = MutableStateFlow<List<Complaint>>(emptyList())
    val assigned = _assigned.asStateFlow()
    private var listener: ListenerRegistration? = null
    private val _available = MutableStateFlow<List<Complaint>>(emptyList())
    val available = _available.asStateFlow()
    private var availableListener: ListenerRegistration? = null

    fun loadAssigned() {
        listener?.remove()
        listener = complaintRepository.listenComplaintsAssignedToWorker { list ->
            _assigned.value = list
        }
    }

    fun loadAvailable() {
        availableListener?.remove()
        availableListener = complaintRepository.listenAvailableComplaints { list ->
            _available.value = list
        }
    }

    fun accept(complaintId: String) {
        viewModelScope.launch {
            complaintRepository.updateStatus(complaintId, ComplaintStatus.Accepted)
            loadAssigned()
        }
    }

    fun resolve(complaintId: String) {
        viewModelScope.launch {
            complaintRepository.updateStatus(complaintId, ComplaintStatus.Resolved)
            loadAssigned()
        }
    }

    fun acceptByMe(complaintId: String) {
        viewModelScope.launch {
            complaintRepository.acceptByCurrentWorker(complaintId)
            loadAssigned()
            loadAvailable()
        }
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
        availableListener?.remove()
    }
}


