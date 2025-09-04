package com.example.sparksmartiiitbh.ui.theme.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sparksmartiiitbh.ui.theme.Model.AuthState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {


    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            fetchUserRole(currentUser.uid)
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email or Password can't be empty")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fetchUserRole(auth.currentUser!!.uid)
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signup(email: String, password: String, role: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email or Password can't be empty")
            return
        }
        if(password.length<6)
        {
            _authState.value = AuthState.Error("Password should be atleast 6 characters long")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser!!.uid
                    val email=auth.currentUser!!.email
                    saveUserRole(uid, role,email)
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    private fun saveUserRole(uid: String, role: String,email: String?) {
        val userMap = hashMapOf("role" to role,"email" to email)

        firestore.collection("users").document(uid)
            .set(userMap)
            .addOnSuccessListener {
                _authState.value = if (role == "admin") AuthState.Admin
                else if (role=="general") {
                    AuthState.General
                }
                else
                {
                    AuthState.Worker
                }
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error("Failed to set user role")
            }
    }

    private fun fetchUserRole(uid: String) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val role = document.getString("role")
                _authState.value = when (role) {
                    "admin" -> AuthState.Admin
                    "worker" -> AuthState.Worker
                    "general" -> AuthState.General
                    else -> {
                        AuthState.Error("No user role found")
                    }
                }
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error("Failed to fetch user role")
            }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}
