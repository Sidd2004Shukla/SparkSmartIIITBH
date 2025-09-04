package com.example.sparksmartiiitbh.ui.theme.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sparksmartiiitbh.ui.theme.Model.Complaint
import com.example.sparksmartiiitbh.ui.theme.Model.ComplaintStatus
import com.example.sparksmartiiitbh.ui.theme.Model.UserBrief
import com.example.sparksmartiiitbh.ui.theme.data.ComplaintRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.firestore.ListenerRegistration

@HiltViewModel
class AdminComplaintsViewModel @Inject constructor(
    private val complaintRepository: ComplaintRepository
) : ViewModel() {

    private val _allComplaints = MutableStateFlow<List<Complaint>>(emptyList())
    val allComplaints = _allComplaints.asStateFlow()

    private val _workers = MutableStateFlow<List<UserBrief>>(emptyList())
    val workers = _workers.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _statusFilter = MutableStateFlow<ComplaintStatus?>(null)
    val statusFilter = _statusFilter.asStateFlow()
    private var listener: ListenerRegistration? = null

    fun loadComplaints() {
        listener?.remove()
        listener = complaintRepository.listenAllComplaints { list ->
            _allComplaints.value = list
        }
    }

    fun loadWorkers() {
        viewModelScope.launch { _workers.value = complaintRepository.fetchWorkers() }
    }

    fun assign(complaintId: String, workerId: String) {
        viewModelScope.launch {
            complaintRepository.assignComplaint(complaintId, workerId)
            loadComplaints()
        }
    }

    fun updateStatus(complaintId: String, status: ComplaintStatus) {
        viewModelScope.launch {
            complaintRepository.updateStatus(complaintId, status)
            loadComplaints()
        }
    }

    fun setStatusFilter(status: ComplaintStatus?) { _statusFilter.value = status }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}


