package com.example.myapplication2.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.myapplication2.UploadStatictics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor (
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) :ViewModel() {

    fun logIn(
        email: String,
        password: String,
        navController: NavController,
        ) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navController.navigate("Home")
                    Log.d("performLogin", "Succcess")
                } else {
                    Log.d("performLogin", "Failed")
                    val errorCode = task.exception?.message
                    Log.e("performLogin", "ErrorCode: $errorCode")
                }
            }
    }
}

