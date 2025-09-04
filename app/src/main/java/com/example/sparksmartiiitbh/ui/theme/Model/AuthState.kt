package com.example.sparksmartiiitbh.ui.theme.Model

sealed class AuthState {
    object Admin : AuthState()
    object Worker : AuthState()
    object General : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}