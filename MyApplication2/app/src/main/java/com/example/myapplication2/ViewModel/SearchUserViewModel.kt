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
//    private var currentUser = AllUser("","","","")

    //名前検索したユーザを特定
    fun FetchFoReqUser(inputName: String) {

        //検索したユーザのドキュメントIDの取得
        db.collection("User").whereEqualTo("userName", "$inputName").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    documentId = document.id
                    Log.d(Tag, "documentId:$documentId")

                    //取得したドキュメントIDを使用してユーザを表示する情報を取得
                    db.collection("User").document("$documentId").get()
                        .addOnSuccessListener { document ->
                            val user = AllUser(
                                password = document.getString("password") ?: "",
                                userName = document.getString("userName") as? String ?: "",
                                profileImage = document.getString("profileImage") ?: "",
                                email = document.getString("email") as? String ?: "",
                                docId = ""
                            )
                            _user.value = user
                            Log.d(Tag, "user:${user.password}")
                        }.addOnFailureListener { Exception ->
                            Log.e(Tag, Exception.toString())
                        }
                }
            }.addOnFailureListener { Exception ->
                Log.e(Tag, Exception.toString())
            }

        //現在のログインユーザのドキュメントを取得
        val uid = auth.currentUser?.uid
        currentUserdocId = uid
        Log.d(Tag, "uid:$uid")
    }

    //フォローボタンを押下したら対象ユーザのフォロワー、フォローを更新
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
                subDocRef.update("following", FieldValue.arrayUnion(documentId))
                    .addOnSuccessListener {
                        Log.d(Tag,"update success")
                    }.addOnFailureListener {
                        Log.d(Tag,"update failed")
                    }
            }
        }
    }

    //フォローしたユーザのドキュメントを更新
    fun followReqUser() {
        val followerSelectQuery = db.collection("User").document("$documentId")
            .collection("FollowData").get()
        Log.d(Tag,"documentId:$documentId")

        followerSelectQuery.addOnSuccessListener {querySnapshot ->
            val followSubDocSnapshot = querySnapshot.documents.firstOrNull()
            followSubDocSnapshot?.let {
               val followSubDocRef =  db.collection("User").document("$documentId")
                    .collection("FollowData").document(followSubDocSnapshot.id)
                followSubDocRef.update("followreqesting", FieldValue.arrayUnion(currentUserdocId))
                    .addOnSuccessListener {
                        Log.d(Tag,"request success")
                    }.addOnFailureListener {
                        Log.d(Tag,"update failed")
                    }
            }

        }

    }

}