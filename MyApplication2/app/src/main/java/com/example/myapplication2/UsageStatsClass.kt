package com.example.myapplication2

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import java.io.FileWriter
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class UsageStatsClass(private val context: Context) {
    private val usageStatsObject: List<UsageStats>
        get() {
            val usageStatsManager =
                context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)

            return usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                calendar.getTimeInMillis(),
                System.currentTimeMillis()
            )
        }

    // 外部から実行するための関数
    fun readOneDayUsageStats() : List<UsageStats> {
        // アプリごとの使用情報をListとして取得
        val usageStats = usageStatsObject
        writeLogToFile(usageStats)
        val resultList = mutableListOf<UsageStats>()

        // for文を使用することで、usageStatに一つのアプリの使用情報を取得する
        for (usageStat in usageStats) {
            // もし、そのアプリを一度も使用していない場合は、スキップする
            if (usageStat.totalTimeInForeground == 0L) {
                continue
            }

            Log.d(
                TAG,
                "packageName: " + usageStat.packageName + "\ttotalTimeDisplayed: " + usageStat.totalTimeInForeground
                        + "\tfirstTime: " + getStringDate(usageStat.firstTimeStamp) + "\tlastTime: " + getStringDate(
                    usageStat.lastTimeUsed
                )
            )
            resultList.add(usageStat)
        }
        return resultList
    }

    private fun writeLogToFile(usageStats: List<UsageStats>){
        try{
            val logFile = File(context.getExternalFilesDir(null),"Log.txt")
            val writer = FileWriter(logFile, true)

            for (usageStat in usageStats){
                if(usageStat.totalTimeInForeground == 0L){
                    continue
                }
                val logMessage = "packageName: ${usageStat.packageName}\ttotalTimeDisplayed: ${usageStat.totalTimeInForeground}\tfirstTime: ${getStringDate(usageStat.firstTimeStamp)}\tlastTime: ${getStringDate(usageStat.lastTimeUsed)}"
                writer.appendln(logMessage)
            }
        } catch (e:Exception) {
            Log.e(TAG, "Error writing log to file",e)
        }
    }

    // long型のミリ秒をString型の人間がわかりやすい形に変換する
    private fun getStringDate(milliseconds: Long): String {
        val df: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPANESE)
        val date = Date(milliseconds)
        return df.format(date)
    }

    companion object {
        // Log.d()で、このクラスが出力したものだと識別するための名前
        private val TAG = UsageStatsClass::class.java.simpleName
    }
}


