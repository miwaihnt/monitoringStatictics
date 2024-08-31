package com.example.myapplication2.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication2.Data.AllUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.text.Format
import javax.inject.Inject

@HiltViewModel
class SearchUserViewModel @Inject constructor(
    private val db : FirebaseFirestore,
    private val auth : FirebaseAuth
) : ViewModel() {

    val Tag = "SearchTag"

    //ユーザ検索用
    private val _user = MutableStateFlow<AllUser>(AllUser("", "", "", "",""))
    val user = _user.asStateFlow()
    private var documentId = ""
    private var currentUserdocId: String? = ""


    //documentIdをもとにユーザ検索
    fun fetchReqUser(inputId:String) {
        db.collection("User").document(inputId).get()
            .addOnSuccessListener { document ->
                val user = AllUser(
                    password = document.getString("password") ?: "",
                    userName = document.getString("userName") as? String ?: "",
                    profileImage = document.getString("profileImage") ?: "",
                    email = document.getString("email") as? String ?: "",
                    docId = "$inputId"
                )
                _user.value = user
                Log.d(Tag, "user:${user.password}")
            }

        //現在のログインユーザのドキュメントを取得
        val uid = auth.currentUser?.uid
        currentUserdocId = uid
        Log.d(Tag, "uid:$uid")

        documentId = inputId

    }


    //フォローボタンを押下したらログインユーザのフォローを更新
    fun updateFollow() {
        //現在のログインユーザのフォローコレクションの更新
        val selectedQuery =
            db.collection("User").document("$currentUserdocId")
                .collection("FollowData").get()
        Log.d(Tag,"currentUser:$currentUserdocId")
        selectedQuery.addOnSuccessListener {querySnapshot ->
            // サブコレクション内の最初のドキュメントを取得
            val subDocSnapshot = querySnapshot.documents.firstOrNull()
            Log.d(Tag,"subDocRef:$subDocSnapshot")

            //取得したsubDocSnapshotがnullでない場合、
            subDocSnapshot?.let {
                val subDocRef = db.collection("User").document("$currentUserdocId")
                    .collection("FollowData").document(subDocSnapshot.id)
                Log.d(Tag,"subDocRef:$subDocRef")
                Log.d(Tag,"docId:$documentId")
                //現在のユーザのfollowingに検索したユーザを追加
                subDocRef.get()
                    .addOnSuccessListener {document ->
                        val followingList = document["following"] as List<String>
                        Log.d(Tag,"followingLis:$followingList")

                        //followingListに追加
                        if (followingList.contains(documentId)) {
                            Log.d(Tag,"contains")
                        } else {
                            subDocRef.update("following", FieldValue.arrayUnion(documentId))
                            Log.d(Tag,"not contains")
                        }
                    }

            }
        }
    }

    //フォローしたユーザのドキュメントを更新
    fun followReqUser() {
        try {
            val followerSelectQuery = db.collection("User").document("$documentId")
                .collection("FollowData").get()
            Log.d(Tag,"documentId:$documentId")

            followerSelectQuery.addOnSuccessListener {querySnapshot ->
                val followSubDocSnapshot = querySnapshot.documents.firstOrNull()
                followSubDocSnapshot?.let {
                    val followSubDocRef =  db.collection("User").document("$documentId")
                        .collection("FollowData").document(followSubDocSnapshot.id)
                    followSubDocRef.get()
                        .addOnSuccessListener { document ->
                            try {
                                Log.d(Tag,"document:$document")
                                val folloreqList = document["followreqesting"] as List<String> ?: emptyList()
                                val followersList = document["followers"] as List<String>
                                Log.d(Tag,"followreqesting:$folloreqList")
                                Log.d(Tag,"followers:$followersList")

                                if (folloreqList.contains(currentUserdocId)) {
                                    Log.d(Tag,"contains")
                                } else {
                                    followSubDocRef.update("followreqesting", FieldValue.arrayUnion(currentUserdocId))
                                        .addOnSuccessListener {
                                            Log.d(Tag,"request success")
                                        }.addOnFailureListener {exception ->
                                            Log.d(Tag,"update failed:${exception.message}")
                                        }
                                }

                                if (followersList.contains(currentUserdocId)) {
                                    Log.d(Tag,"contains")
                                } else {
                                    followSubDocRef.update("followers",FieldValue.arrayUnion(currentUserdocId))
                                        .addOnSuccessListener {
                                            Log.d(Tag,"request success")
                                        }.addOnFailureListener {exception ->
                                            Log.d(Tag,"update failed:${exception.message}")
                                        }
                                }

                            } catch (e:Exception) {
                                Log.d(Tag,"failed: ${e.message}")
                            }
                        }.addOnFailureListener { exception ->
                            Log.d(Tag,"update failed: ${exception.message}")
                        }
                }

            }
        } catch (e:Exception)  {

            Log.d(Tag,"unexpextedErro:${e.message}")

        }
    }

}