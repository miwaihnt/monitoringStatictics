package com.example.myapplication2

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class UserRegistration(val db : FirebaseFirestore){

    val TAG = "dbUserRegist"

    fun registration(
        email:String,
        password:String,
        auth: FirebaseAuth,
        userName:String
    ){
        // firebase authenticationにユーザ登録
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in 2success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    if (user !== null) {
                        val userId = user.uid
                        Log.d(TAG,"userid:$userId")
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
                                    Log.d(TAG,"userDocCreate:$userId")
                                    val followData =  hashMapOf(
                                        "followers" to emptyList<String>(),
                                        "following" to emptyList<String>()
                                    )
                                    db.collection("User").document(userId).collection("FollowData")
                                        .add(followData)
                                        .addOnSuccessListener { subDocumentReference ->
                                            Log.d(TAG,"Subcollection added with ID: ${subDocumentReference.id}")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e(TAG,"Error adding subCollection,e")
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error adding document", e)
                                }

                        } catch (e:Exception) {
                            Log.e(TAG,"Failure Registration")
                        }

                    }




                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                }
            }

        //firebase firestoreにユーザドキュメントを作成
        try {
            val user = hashMapOf(
                "userName" to userName,
                "email" to email,
                "password" to password
            )
            db.collection("User")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG,"DocumentSnapshot added with ID: ${documentReference.id}")
                    val userID = documentReference.id
                    val followData =  hashMapOf(
                        "followers" to emptyList<String>(),
                        "following" to emptyList<String>()
                    )
                    db.collection("User").document(userID).collection("FollowData")
                        .add(followData)
                        .addOnSuccessListener { subDocumentReference ->
                            Log.d(TAG,"Subcollection added with ID: ${subDocumentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG,"Error adding subCollection,e")
                        }

                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error adding document", e)
                }


        } catch (e:Exception){
            Log.e(TAG,"Failure Registration")
        }

    }



}