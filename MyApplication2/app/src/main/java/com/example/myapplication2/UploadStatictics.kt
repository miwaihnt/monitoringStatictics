package com.example.myapplication2

import android.util.Log
import android.app.usage.UsageStats
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.util.HashMap
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class UploadStatictics(val db : FirebaseFirestore) {

    val TAG = "dbConnection"
    val tag = "dailyStatus"
    private val currentDateTime = LocalDateTime.now()
    private val FormatDate = getFormattedDateTime()


    private fun getFormattedDateTime () :String {
        return currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    private fun getStringDate(milliseconds: Long): String {
        val df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPANESE)
        val date = Date(milliseconds)
        return df.format(date)
    }


    fun uploadusestate(usageStats:List<UsageStats>) {

        val currentUser = FirebaseAuth.getInstance().currentUser
        Log.d("TAG", "currentUser:$currentUser")

        val auth = FirebaseAuth.getInstance()
        val cachedUser = auth.currentUser

        if (cachedUser != null) {
            // キャッシュされたユーザー情報がある場合
            val uid = cachedUser.uid
            val email = cachedUser.email
            // 他のユーザー情報を取得するなどの処理を行う
            Log.d("CachedUserInfo", "UID: $uid, Email: $email")
        } else {
            // キャッシュされたユーザー情報がない場合
            Log.d("CachedUserInfo", "No cached user information found")
        }




        if (currentUser != null) {
            val uid = currentUser.uid
            Log.d(TAG,"currentuserはnullジャない")

            try {
                //データの整形
                val data = HashMap<String, Any>()
                for (usageStat in usageStats) {
                    val usageData = hashMapOf(
                        "packageName" to usageStat.packageName,
                        "totalTimeInForeground" to usageStat.totalTimeInForeground,
                        "firstTime" to getStringDate(usageStat.firstTimeStamp),
                        "lastTime" to getStringDate(usageStat.lastTimeUsed)
                    )
                    data[usageStat.packageName] = usageData
                }

                db.collection("statistics")
                    .document("$uid")
                    .collection("$FormatDate")
                    .document("$FormatDate")
                    .set(data)

            } catch (e: Exception) {
                Log.e(tag, "Failure upload")

            }

        } else {
            Log.e("TAG", "currentUserはnullです")
        }
    }
}