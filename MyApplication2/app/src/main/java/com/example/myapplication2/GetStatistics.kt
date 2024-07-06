package com.example.myapplication2

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

// 統計情報の取得関数
class getStatistics(private val db: FirebaseFirestore) {

    val TAG = "getStatisTics"

    fun getStatisticsInfo(userId: String, onResult: (List<DailyStatistics>) -> Unit) {
        Log.d(TAG, "getStatisticsInfo called for userId: $userId")

        db.collection("statistics")
            .document(userId)
            .collection("dailyStatistics")
            .get()
            .addOnSuccessListener { dailyQuerySnapshot ->
                if (dailyQuerySnapshot.isEmpty) {
                    Log.d(TAG, "dailyQuerySnapshot is empty")
                    onResult(emptyList())  // Return an empty list if no data is found
                } else {
                    val allStatisticsData = mutableListOf<DailyStatistics>()
                    for (doc in dailyQuerySnapshot) {
                        val date = doc.id
                        val apps = mutableListOf<AppUsageData>()
                        for ((key, value) in doc.data) {
                            val usageData = value as Map<*, *>
                            val packageName = usageData["packageName"] as String
                            val totalTime = usageData["totalTimeInForeground"] as Long
                            apps.add(AppUsageData(packageName, totalTime))
                        }
                        allStatisticsData.add(DailyStatistics(date, apps))
                    }
                    onResult(allStatisticsData)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to retrieve dailyStatistics collection", exception)
                onResult(emptyList())  // Return an empty list in case of error
            }


        //コレクショングループによる一括取得
//        db.collectionGroup("dailyStatistics")
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                if( querySnapshot.isEmpty() ){
//                    Log.d(TAG,"documentSnapshot NotExissts")
//                }
//                else {
//                    Log.d(TAG,"documentSnapshot Exsits")
//                    Log.d(TAG,"documentSnapshot:$querySnapshot")
//                    val StaticDataList = mutableMapOf<String,Map<String,Any>>()
//                    for( document in querySnapshot ) {
//                        val date = document.id
//                        val data = document.data
//                        StaticDataList[date] = data
//                    }
//                    Log.d(TAG,"SttaticDataList:$StaticDataList")
//                }
//            }
//            .addOnFailureListener { Exception ->
//                Log.d(TAG,"collectionGroup query get failed")
//            }

    }

}