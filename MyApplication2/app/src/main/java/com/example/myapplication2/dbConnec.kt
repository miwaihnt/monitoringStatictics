package com.example.myapplication2

import android.content.pm.verify.domain.DomainVerificationUserState
import android.util.Log
import android.app.usage.UsageStats
import java.text.SimpleDateFormat
import java.util.Locale
import android.app.usage.UsageStatsManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import java.util.ArrayList
import java.util.Date
import java.util.HashMap
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class dbConnection(val db : FirebaseFirestore) {

    val TAG = "dbConnection"
    val tag = "dailyStatus"

    fun uploadusestate(usageStats:List<UsageStats>) {

        try {
            //データの整形
            val data = HashMap<String,Any>()
            for(usageStat in usageStats) {
             val usageData = hashMapOf(
                 "packageName" to usageStat.packageName,
                 "totalTimeInForeground" to usageStat.totalTimeInForeground,
                 "firstTime" to getStringDate(usageStat.firstTimeStamp),
                 "lastTime" to getStringDate(usageStat.lastTimeUsed)
             )
             data[usageStat.packageName] = usageData
            }
            //データの格納
            db.collection("userId")
                .document("dailyStatus")
                .set(data)
                .addOnSuccessListener { Log.d(tag,"successfully upload!") }
                .addOnFailureListener { e -> Log.w(tag,"Failure upload") }


        } catch(e:Exception) {
            Log.e(tag,"Failure upload")

        }

    }

    private fun getStringDate(milliseconds: Long): String {
        val df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPANESE)
        val date = Date(milliseconds)
        return df.format(date)
    }

}