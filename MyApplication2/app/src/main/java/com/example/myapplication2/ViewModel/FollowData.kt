package com.example.myapplication2.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication2.Data.AllUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class FollowData @Inject constructor (
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    val userList = mutableStateListOf<AllUser>()
    private val uiState = MutableStateFlow(UiState())
    val _uiState = uiState.asStateFlow()

    init {
        fetchFollow()
    }

    fun fetchFollow() {
        val authUser = auth.currentUser
        Log.d("ListFriends","authUser:${authUser?.uid}")
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
                                        docId = ""
                                    )
                                    userList.add(userName)
                                    Log.d("fetchFollow","userList:${userList.toList()}")
                                }
                            }
                        }
                    } else {
                        Log.d("ListFriends", "No data found in FollowData")
                    }
                    uiState.value = _uiState.value.copy(
                        userList = userList
                    )
                }
            } catch (e: Exception) {
                Log.e("ListError", "Firestre not getting :${e.message}", e)
            }
        }
    }

    //SerachBar用
    fun onEvent(event: SearchBarEvent) {
        when(event)  {
            is SearchBarEvent.QueryChange -> {
                var isQuerying = false
                viewModelScope.launch {
                    if(event.query.isNotEmpty()){
                        isQuerying = true
                        val filterdUserList = userList.filter {
                            it.userName.startsWith(prefix = event.query,ignoreCase = true) }
                        uiState.value = _uiState.value.copy(
                            query = event.query,
                            isQuerying = isQuerying,
                            userList = filterdUserList
                        )
                    }
                    else {
                        uiState.value = _uiState.value.copy(
                            query = event.query,
                            isQuerying = isQuerying,
                            userList = userList.toList()
                        )
                    }

                    Log.d("onEvent","query:${event.query}")
                    Log.d("onEvent","userList:${userList.toList()}")

                }
            }
            is SearchBarEvent.Select -> {
                event.user.let {
                    _history.remove(it)
                    _history.add(index = 0, it)
                }

                uiState.value =
                    _uiState.value.copy(
                        query = event.user.userName,
                        isQuerying = true,
                        selected = event.user,
                        searchHistory = _history,
                        userList = listOf(event.user)
                    )
            }

            is SearchBarEvent.Back -> {
                uiState.value =
                    _uiState.value.copy(
                        selected = null
                    )
            }

            is SearchBarEvent.Cancel -> {
                uiState.value =
                    _uiState.value.copy(
                        query = "",
                        isQuerying = false,
                        userList= userList.toList(),
                        selected= null,
                        searchHistory = _history
                    )
            }
        }
    }
}


private val _history = mutableListOf<AllUser>()
data class UiState(
    val query: String = "",
    val isQuerying: Boolean = false,
    val userList:List<AllUser> = listOf(),
    val selected: AllUser? = null,
    val searchHistory: List<AllUser> = _history
)


sealed class SearchBarEvent{
    data class QueryChange(val query: String): SearchBarEvent()
    data class Select(val user: AllUser): SearchBarEvent()
    object Back : SearchBarEvent()
    object Cancel : SearchBarEvent()
}






