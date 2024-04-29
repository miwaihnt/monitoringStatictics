package com.example.myapplication2

import android.content.pm.verify.domain.DomainVerificationUserState
import android.util.Log
import android.app.usage.UsageStats
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.util.HashMap

class dbInfoGet(val db : FirebaseFirestore) {

    val TAG = "InfoGet"

    // コレクション配下取得
    fun getInfo() {
        Log.d(TAG,"Calling")
        db.collection("User")
            .get()
            .addOnSuccessListener { querySnapshot ->
               for (document in querySnapshot )
                    Log.d(TAG, "${document.id} => ${document.data}")
            }
            .addOnFailureListener{exception ->
                Log.e(TAG,"getUser Failer",exception)
            }
    }

//     サブコレクション配下取得
    fun subCollecInfoget() {
    db.collection("User")
        .get()
        .addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                val userId = document.id
                Log.d(TAG, "User ID : $userId")
                val subCollectionRef = db.collection("User").document(userId)
                    .collection("FollowData")
                //　サブコレクションの参照を取得
                subCollectionRef.get()
                    .addOnSuccessListener { subCollectionSnapshot ->
                        for (subDocument in subCollectionSnapshot.documents) {
                            Log.d(TAG,"${subDocument.id} => ${subDocument.data}")
                        }
                    }
                    .addOnFailureListener {Exception ->
                        Log.e(TAG,"subCollectionGet Failed")
                    }

            }


        }
        .addOnFailureListener { Exception ->
            Log.e(TAG,"subCollectionGet Start Failed")
        }
    }
}


