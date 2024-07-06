package com.example.myapplication2.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication2.Data.AllUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class FollowData @Inject constructor (
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    val userList = mutableStateListOf<AllUser>()

    init {
        fetchFollow()
    }

    fun fetchFollow() {
        val authUser = auth.currentUser
        viewModelScope.launch {
            try {
                if (authUser !== null) {
                    userList.clear()
                    val selectedQuery =
                        db.collection("User").document(authUser.uid).collection("FollowData").get()
                            .await()
                    if (!selectedQuery.isEmpty) {
                        for (document in selectedQuery) {
                            val Following =
                                document.get("following") as? List<String> ?: emptyList()
                            val followers =
                                document.get("followers") as? List<String> ?: emptyList()
                            Log.d("ListFriends", "Following:$Following")
                            Log.d("ListFriends", "followers:$followers")

                            //フォローしているユーザの名前を抽出
                            for (userData in Following) {
                                val userDoc = db.collection("User").document(userData).get().await()
                                Log.d("ListFriends", "userDoc:${userDoc.data}")
                                if (userDoc !== null) {
                                    val userName = AllUser(
                                        password = userDoc["password"] as? String ?: "",
                                        userName = userDoc["userName"] as? String ?: "",
                                        email = userDoc["userName"] as? String ?: "",
                                        profileImage = userDoc["profileImage"] as? String ?: "",
                                    )
                                    userList.add(userName)
                                }
                            }
                        }
                    } else {
                        Log.d("ListFriends", "No data found in FollowData")
                    }
                }
            } catch (e: Exception) {
                Log.e("ListError", "Firestre not getting :${e.message}", e)
            }
        }
    }
}


