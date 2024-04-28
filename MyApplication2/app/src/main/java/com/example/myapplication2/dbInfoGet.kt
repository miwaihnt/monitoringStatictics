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


}