package com.example.myapplication2.ViewModel

import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.myapplication2.Data.AllUser
import com.example.myapplication2.UserRegistration
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import com.google.firebase.auth.PhoneAuthCredential


@HiltViewModel
class UserResistrationViewModel @Inject constructor(
    private val auth:FirebaseAuth,
    private val db:FirebaseFirestore
) : ViewModel() {

    private val TAG = "UserResistration"
    var alertDialog = mutableStateOf(false)
    var alertDialogMessage = mutableStateOf("")

//    val phoneDialeg = mutableStateOf(false)
//    var phoneAlertDialogMessage = mutableStateOf("")
//    val phoneNumber = mutableStateOf("")
//    val smsNumber = mutableStateOf("")

    //ユーザ登録
    fun AuthUserRegistration(
        userName: String,
        email: String,
        password: String,
        navController: NavController,
    ) {
        Log.d(TAG, "AuthUserRegistration called")

        if (userName.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val firebaseUser = hashMapOf(
                            "userName" to userName,
                            "email" to email,
                            "password" to password
                        )
                        if (user!==null) {
                            val userId = user.uid
                            Log.d(TAG, "useId:$userId")
                            if (user!==null) {
                                db.collection("User")
                                    .document(userId)
                                    .set(firebaseUser)
                                    .addOnSuccessListener {
                                        Log.d(TAG, "userDocCreate:$userId")
                                        val followData = hashMapOf(
                                            "followers" to emptyList<String>(),
                                            "following" to emptyList<String>(),
                                            "followreqesting" to emptyList<String>()
                                        )
                                        db.collection("User").document(userId).collection("FollowData")
                                            .add(followData)
                                            .addOnSuccessListener { subDocumentReference ->
                                                Log.d(TAG, "Subcollection added with ID: ${subDocumentReference.id}")
                                                navController.navigate("LogInView")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e(TAG, "Error adding subCollection,e")
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(TAG, "Error adding document", e)
                                    }
                            } else {
                                Log.e(TAG,"user is null")
                            }
                        }
                    }
                }.addOnFailureListener {exception->
                    Log.e(TAG,"task is fail:${exception.message}")
                }

        } else {
            Log.d(TAG,"input information is empty")
            alertDialog.value = true
            alertDialogMessage.value = "入力されていない項目があります"
        }
    }
}

