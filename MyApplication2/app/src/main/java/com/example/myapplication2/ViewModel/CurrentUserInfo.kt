package com.example.myapplication2.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication2.Data.AllUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class CurrentUserViewModel @Inject constructor(
    private  val db: FirebaseFirestore,
    private  val auth: FirebaseAuth
):ViewModel() {

    private val _user = MutableStateFlow<AllUser>(AllUser("","","", "",))
    val user = _user.asStateFlow()

    private val _userNameState = MutableStateFlow<String>("")
    val userNameState = _userNameState.asStateFlow()


    fun currentUserInfo() {
        val uid = auth.currentUser?.uid
        if(uid !== null) {
            db.collection("User").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        Log.d("currentUserInfo","documentData:${document.data}")
                        val user = AllUser(
                            password = document.getString("password") ?: "",
                            userName = document.getString("userName") as? String ?: "",
                            profileImage = document.getString("profileImage")?:"",
                            email = document.getString("email") as? String ?: ""
                        )
                        _user.value = user
                        Log.d("currentUserInfo","_user:${_user.value}")
                    } else {
                        Log.e("currentUserInfo","document is null")
                    }
                }
                .addOnFailureListener { Exception ->
                    Log.e("CurrentUserInfo","Exception ${Exception.message}")
                }
        } else {
            Log.e("CurrentUserInfo","uid is null")
        }
    }


// user名の変更
    fun updateUserName (newUserName:String) {
        val uid = auth.currentUser?.uid
        if(uid !== null) {
            val docRef= db.collection("User").document(uid)
                docRef.update("userName",newUserName)
                .addOnSuccessListener {
                    _userNameState.value = newUserName
                    Log.d("changeUserName","changeUserName:is succecc,uid:$uid")
                }
                .addOnFailureListener {
                    Log.d("changeUserName","changeUserName:is fail docRef:$docRef,uid:$uid")
                }
        } else {
          Log.e("changeUserName","document is null")
        }
    }
}