package com.example.myapplication2.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetEmailPasswordViewModel @Inject constructor(
    private val auth:FirebaseAuth,
):ViewModel() {

    private val TAG = "ResetEmailPassword"

    fun resetEmailPassword(
        Email:String,
        navController: NavController
    ) {
        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(Email)
                    .addOnSuccessListener {
                        Log.d(TAG,"sendPasswordResetEmail is success")
                        navController.navigate("LogInView")

                    }.addOnFailureListener {
                        Log.d(TAG,"sendPasswordResetEmail is fail")
                    }
            } catch(e:Exception) {
                Log.d(TAG,"${e.message}")
            }
        }

    }


}