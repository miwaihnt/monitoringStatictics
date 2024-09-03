package com.example.myapplication2.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication2.View2.AppUsageData
import com.example.myapplication2.View2.DailyStatistics
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(private val db: FirebaseFirestore) : ViewModel() {

    val dailyStatistics = MutableLiveData<List<DailyStatistics>>()
    val errorMessage = MutableLiveData<String?>()

    fun getUserStatistics(userId: String) {
        db.collection("statistics")
            .document(userId)
            .collection("dailyStatistics")
            .get()
            .addOnSuccessListener { dailyQuerySnapshot ->
                if (dailyQuerySnapshot.isEmpty) {
                    dailyStatistics.value = emptyList()
                    errorMessage.value = "No data found"
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
                    dailyStatistics.value = allStatisticsData
                    errorMessage.value = null
                }
            }
            .addOnFailureListener { exception ->
                errorMessage.value = "Failed to retrieve daily statistics"
                dailyStatistics.value = emptyList()
            }
    }
}