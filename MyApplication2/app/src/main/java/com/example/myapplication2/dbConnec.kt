package com.example.myapplication2

import android.content.pm.verify.domain.DomainVerificationUserState
import android.util.Log
import android.app.usage.UsageStats
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.util.HashMap


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
            db.collection("statistics")
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