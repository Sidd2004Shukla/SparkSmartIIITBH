package com.example.sparksmartiiitbh.ui.theme.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sparksmartiiitbh.ui.theme.Model.Complaint
import com.example.sparksmartiiitbh.ui.theme.data.ComplaintRepository
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComplaintDetailViewModel @Inject constructor(
    private val repository: ComplaintRepository
) : ViewModel() {
    private val _complaint = MutableStateFlow<Complaint?>(null)
    val complaint = _complaint.asStateFlow()
    private var listener: ListenerRegistration? = null

    fun start(id: String) {
        listener?.remove()
        listener = repository.listenComplaintById(id) { _complaint.value = it }
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}


