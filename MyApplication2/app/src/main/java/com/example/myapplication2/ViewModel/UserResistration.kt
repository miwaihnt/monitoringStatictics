package com.example.myapplication2.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import com.example.myapplication2.Data.AllUser
import com.example.myapplication2.UserRegistration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class UserResistrationViewModel @Inject constructor(
    private val auth:FirebaseAuth,
    private val db:FirebaseFirestore
) : ViewModel() {

    private val TAG = "UserResistration"

    //ユーザ登録
    fun AuthUserRegistration(
        userName: String,
        email: String,
        password: String
    ) {
        Log.d(TAG,"AuthUserRegistration called")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in 2success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    if (user !== null) {
                        val userId = user.uid
                        Log.d(TAG, "userid:$userId")
                        try {
                            val firebaseUser = hashMapOf(
                                "userName" to userName,
                                "email" to email,
                                "password" to password
                            )
                            db.collection("User")
                                .document(userId)
                                .set(firebaseUser)
                                .addOnSuccessListener {
                                    Log.d(TAG, "userDocCreate:$userId")
                                    val followData = hashMapOf(
                                        "followers" to emptyList<String>(),
                                        "following" to emptyList<String>()
                                    )
                                    db.collection("User").document(userId).collection("FollowData")
                                        .add(followData)
                                        .addOnSuccessListener { subDocumentReference ->
                                            Log.d(
                                                TAG,
                                                "Subcollection added with ID: ${subDocumentReference.id}"
                                            )
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e(TAG, "Error adding subCollection,e")
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error adding document", e)
                                }

                        } catch (e: Exception) {
                            Log.e(TAG, "Failure Registration")
                        }

                    }

                } else {
                    Log.e(TAG, "task is failed: ${task.exception}")
                }
            }
    }

}