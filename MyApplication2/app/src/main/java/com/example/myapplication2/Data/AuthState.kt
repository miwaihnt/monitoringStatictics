package com.example.myapplication2.Data

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val uid: String, val isNewUser: Boolean) : AuthState()
    data class Failure(val message: String) : AuthState()
}