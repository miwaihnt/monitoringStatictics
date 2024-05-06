package com.example.myapplication2

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class dbAddFollowData(val db : FirebaseFirestore) {

    val TAG = "addFollowData"

    fun addFollowingData(newFollowing : String) {

        // FirebaseAuthから現在のユーザーを取得
        val currentUser = FirebaseAuth.getInstance().currentUser

        // 現在のユーザーが存在する場合は、ユーザーIDを取得してログに出力
        currentUser?.let { user ->
            val userId = user.uid
            val userEmail = user.email

            db.collection("User")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        // メールアドレスが一致するユーザーが見つからなかった場合の処理
                        Log.d("getUserNameByEmail", "User with email $userEmail not found")
                    } else {
                        // メールアドレスが一致するユーザーが見つかった場合の処理
                        for (document in querySnapshot) {
                            // ログインしているユーザーのドキュメントIDを取得
                            val loginUserId = document.id
                            if (document.id != null) {
                                // ドキュメントIDが取得できた場合の処理
                                Log.d("getDocumentIdByEmail", "User name for email $userEmail: $loginUserId")

                                // ログインしているユーザーの「followdata」コレクションへの参照を取得
                                val followDataCollectionRef = db.collection("User").document(loginUserId).collection("FollowData")
                                // 「followdata」コレクションの情報を取得
                                followDataCollectionRef.get()
                                    .addOnSuccessListener { querySnapshot ->
                                        for (document in querySnapshot) {
                                            val documentId = document.id
                                            // 既存のフォロワーを取得
                                            val existingFollowing = document.get("following") as? List<String> ?: emptyList()
                                            // 既存のフォロワーリストに新しいフォロワーを追加し、重複を排除
                                            val updatedFollowing = (existingFollowing + newFollowing).distinct()

                                            // 更新用のデータを作成
                                            val newData = mapOf(
                                                "following" to updatedFollowing
                                            )

                                            // ドキュメントを更新
                                            followDataCollectionRef.document(documentId)
                                                .update(newData)
                                                .addOnSuccessListener {
                                                    Log.d(
                                                        "addNewFollowingToAllUsers",
                                                        "New following added to user $documentId successfully"
                                                    )
                                                }
                                                .addOnFailureListener { exception ->
                                                    // エラー処理
                                                    Log.e(
                                                        "addNewFollowingToAllUsers",
                                                        "Error adding new following to user $documentId",
                                                        exception
                                                    )
                                                }
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        // エラー処理
                                        Log.e("addNewFollowingToAllUsers", "Error getting follow data documents", exception)
                                    }

                            } else {
                                // userNameが取得できなかった場合の処理
                                Log.d("getDocumentIdByEmail", "User name is null for email $userEmail")
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // クエリの実行中にエラーが発生した場合の処理
                    Log.e("getUserNameByEmail", "Error getting documents: ", exception)
                }

        }

    }

    fun addFollowersData(newFollowers : String) {

        // FirebaseAuthから現在のユーザーを取得
        val currentUser = FirebaseAuth.getInstance().currentUser

        // 現在のユーザーが存在する場合は、ユーザーIDを取得してログに出力
        currentUser?.let { user ->
            val userId = user.uid
            val userEmail = user.email

            db.collection("User")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        // メールアドレスが一致するユーザーが見つからなかった場合の処理
                        Log.d("getUserNameByEmail", "User with email $userEmail not found")
                    } else {
                        // メールアドレスが一致するユーザーが見つかった場合の処理
                        for (document in querySnapshot) {
                            // ログインしているユーザーのドキュメントIDを取得
                            val loginUserId = document.id
                            if (document.id != null) {
                                // ドキュメントIDが取得できた場合の処理
                                Log.d("getDocumentIdByEmail", "User name for email $userEmail: $loginUserId")

                                // 登録する相手ユーザーの「followdata」コレクションへの参照を取得
                                val followDataCollectionRef = db.collection("User").document(newFollowers).collection("FollowData")
                                // 「followdata」コレクションの情報を取得
                                followDataCollectionRef.get()
                                    .addOnSuccessListener { querySnapshot ->
                                        for (document in querySnapshot) {
                                            val documentId = document.id
                                            // 既存のフォロワーを取得
                                            val existingFollowers = document.get("followers") as? List<String> ?: emptyList()
                                            // 既存のフォロワーリストに新しいフォロワーを追加し、重複を排除
                                            val updatedFollowers = (existingFollowers + loginUserId).distinct()

                                            // 更新用のデータを作成
                                            val newData = mapOf(
                                                "followers" to updatedFollowers
                                            )

                                            // ドキュメントを更新
                                            followDataCollectionRef.document(documentId)
                                                .update(newData)
                                                .addOnSuccessListener {
                                                    Log.d(
                                                        "addNewFollowersToAllUsers",
                                                        "New followers added to user $newFollowers successfully"
                                                    )
                                                }
                                                .addOnFailureListener { exception ->
                                                    // エラー処理
                                                    Log.e(
                                                        "addNewFollowersToAllUsers",
                                                        "Error adding new followers to user $newFollowers",
                                                        exception
                                                    )
                                                }
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        // エラー処理
                                        Log.e("addNewFollowersToAllUsers", "Error getting follow data documents", exception)
                                    }

                            } else {
                                // userNameが取得できなかった場合の処理
                                Log.d("getDocumentIdByEmail", "User name is null for email $userEmail")
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // クエリの実行中にエラーが発生した場合の処理
                    Log.e("getUserNameByEmail", "Error getting documents: ", exception)
                }

        }

    }

}


class dbgetDocumentId(val db : FirebaseFirestore) {

    val TAG = "getDocumentId"

    fun getUserDocumentId(onComplete: (String?) -> Unit) {
        // FirebaseAuthから現在のユーザーを取得
        val currentUser = FirebaseAuth.getInstance().currentUser

        // 現在のユーザーが存在する場合は、ユーザーIDを取得してログに出力
        currentUser?.let { user ->
            val userId = user.uid
            val userEmail = user.email

            db.collection("User")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        // メールアドレスが一致するユーザーが見つからなかった場合の処理
                        Log.d("getUserNameByEmail", "User with email $userEmail not found")
                    } else {
                        // メールアドレスが一致するユーザーが見つかった場合の処理
                        for (document in querySnapshot) {
                            // ユーザーのuserNameを取得
                            val documentId = document.id
                            if (document.id != null) {
                                // userNameが取得できた場合の処理
                                Log.d(
                                    "getDocumentIdByEmail",
                                    "User name for email $userEmail: $documentId"
                                )
                                // ドキュメントが存在する場合、そのIDを返す
                                onComplete(document.id)
                            } else {
                                // userNameが取得できなかった場合の処理
                                Log.d(
                                    "getDocumentIdByEmail",
                                    "User name is null for email $userEmail"
                                )
                                // ドキュメントが存在しない場合、nullを返す
                                onComplete(null)
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // クエリの実行中にエラーが発生した場合の処理
                    Log.e("getUserNameByEmail", "Error getting documents: ", exception)
                }

        }
    }

}