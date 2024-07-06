package com.example.myapplication2

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class getStatistics(private val db: FirebaseFirestore)  {

    val TAG = "getStatisTics"

    fun getStatisticsInfo(){


    Log.d(TAG,"getStatisticsInfo called")

        db.collection("statistics")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val userList = mutableListOf<String>()
               if(querySnapshot.isEmpty){
                Log.d(TAG,"querySnapshot is Empty")
               } else {
                   Log.d(TAG, "Statistics collection retrieved. Number of documents: ${querySnapshot.size()}")
                   for ( document in querySnapshot ){
                       val userId = document.id
                       userList.add(userId)
                       Log.d(TAG, "Processing userId: $userId")
                       db.collection("statistics")
                           .document(userId)
                           .collection("dailyStatistics")
                           .get()
                           .addOnSuccessListener {dailyQuerySnapshot ->
                               if(dailyQuerySnapshot.isEmpty){
                                   Log.d(TAG,"dailyQuerySnaphoto is empty")
                               }else {
                                   val statisticsData = mutableMapOf<String,Map<String,Any>>()
                                   for (document in dailyQuerySnapshot) {
                                       val date = document.id
                                       val data = document.data
                                       statisticsData[date] = data
                                   }
                                   Log.d(TAG,"staticsDate:$userId,$statisticsData")
                               }
                           }
                           .addOnFailureListener { exception ->
                               Log.d(TAG,"dailyQuerySnapshot is Empty")
                           }
                   }
                   Log.d(TAG,"userList:$userList")
               }


            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to retrieve statistics collection", exception)
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