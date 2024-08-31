package com.example.myapplication2.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication2.Data.AllUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class FollowReqUserViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel(){

    val userList = mutableStateListOf<AllUser>(AllUser("","","","",""))
    private val Tag = "FollowReqUserViewMode"
    var currentUserdocId :String = ""
    private var followReqUserDoc = listOf<String>()
    private var followdocumentId: String = ""

    //フォローリクエストされているユーザを取得
    fun fetchFollowReqUserList() {

        //現在のユーザIDを取得
        val uid = auth.currentUser?.uid
        currentUserdocId = uid?: ""
        Log.d(Tag, "uid:$uid")

        //FollowDataの取得
        viewModelScope.launch {
            try {
                userList.clear()
                val querySnapshot = db.collection("User").document("$currentUserdocId")
                    .collection("FollowData").get().await()
                        for (document in querySnapshot.documents) {
                            val followreqesting =  document.get("followreqesting") as? List<String> ?: emptyList()
                            followReqUserDoc = followreqesting
                            Log.d(Tag,"followreqesting:$followreqesting")
                            val uList = mutableListOf<AllUser>()
                            //対象者のユーザ情報を取得
                            for (userData in followreqesting) {
                                val userDoc = db.collection("User").document(userData).get().await()
                                Log.d(Tag,"userDoc:$userDoc")
                                if (userDoc !== null) {
                                    val user = AllUser(
                                        password = userDoc["password"] as? String ?: "",
                                        userName = userDoc["userName"] as? String ?: "",
                                        email = userDoc["email"] as? String ?: "",
                                        profileImage = userDoc["profileImage"] as? String ?: "",
                                        docId = userDoc.id
                                    )
                                    uList.add(user)
                                }
                            }
                            userList.addAll(uList)
                            Log.d(Tag,"userList:${userList.toList()}")
                        }
            } catch (e: Exception) {
                Log.e("ListError", "Firestre not getting :${e.message}", e)
            }
        }

    }

    //友達かも？からフォローしたら、自身のフォロー対象と相手のフォロワーズに追加
    fun followReqUser(dcumentId:String) {
        //自分のドキュメントのフォローを更新して、foloowreqを消す
        Log.d(Tag,"currentUserdocId:$currentUserdocId")
        Log.d(Tag,"dcumentId:$dcumentId")

        followdocumentId = dcumentId

        //自身のフォローに追加
        db.collection("User").document(currentUserdocId)
            .collection("FollowData").get()
            .addOnSuccessListener {querySnapshot ->
                val followDataDoc = querySnapshot.documents.firstOrNull()
                followDataDoc?.let {
                    val subdocRef = db.collection("User").document(currentUserdocId)
                        .collection("FollowData").document(followDataDoc.id)

                    subdocRef.get()
                        .addOnSuccessListener { document ->
                            Log.d(Tag,"document:${document.data}")
                            Log.d(Tag,"document:${document["following"]}")
                            val followingList = document["following"] as List<String>
                            Log.d(Tag,"followingList:$followingList")
                            if (followingList.contains(followdocumentId)) {
                                Log.d(Tag,"すでにFollow済みです。")
                            } else {
                                subdocRef.update("following", FieldValue.arrayUnion(dcumentId))
                                subdocRef.update("followreqesting",FieldValue.arrayRemove(dcumentId))
                            }
                        }
                    userList.removeIf{it.docId == dcumentId}
                    Log.d(Tag,"userList:${userList.toList()}")
                }
            }

        //相手のフォロワーズに追加
        db.collection("User").document(followdocumentId).collection("FollowData").get()
            .addOnSuccessListener { querySnapshot ->
                val sucdocRef = querySnapshot.documents.firstOrNull()
                sucdocRef?.let {
                    val followDoc = db.collection("User").document(followdocumentId).collection("FollowData").document(sucdocRef.id)
                        followDoc.get()
                        .addOnSuccessListener{ document ->
                            val followersList = document["followers"] as List<String>
                            if (followersList.contains(followdocumentId)) {
                                Log.d(Tag,"すでにFollow済みです。")
                            } else {
                                followDoc.update("followers",FieldValue.arrayUnion(currentUserdocId))
                            }
                        }.addOnFailureListener { exception ->
                            Log.d(Tag,"${exception.message}")
                            }
                }
            }

    }
}




