package com.example.myapplication2.ViewModel

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication2.UsageStatsClass
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext


@HiltViewModel
class UsageStatsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    // 権限が付与されているか確認するメソッド
    fun checkUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    // 権限がない場合に設定画面を開いてユーザーに要求するメソッド
    fun requestUsageStatsPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    private val usageStatsObject: List<UsageStats>
        get() {
            val usageStatsManager =
                context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            Log.d("usageStatsManager","usageStatsManager:$usageStatsManager")
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startTime = calendar.timeInMillis

            // 翌日の00:00に終了時間を設定
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val endTime = calendar.timeInMillis

            return usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            )
        }


    // 外部から実行するための関数
    fun readOneDayUsageStats() : List<UsageStats> {
        // アプリごとの使用情報をListとして取得
        val usageStats = usageStatsObject
        Log.d("usageStats","usageStats:$usageStats")
        //writeLogToFile(usageStats)
        val resultList = mutableListOf<UsageStats>()

        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val endOfDay = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis


        // for文を使用することで、usageStatに一つのアプリの使用情報を取得する
        for (usageStat in usageStats) {
            // もし、そのアプリを5秒以上使用していない場合は、スキップする
            if (usageStat.totalTimeInForeground <= 5000L) {
                continue
            }

            Log.d(
                TAG,
                "packageName: " + usageStat.packageName + "\ttotalTimeDisplayed: " + usageStat.totalTimeInForeground
                        + "\tfirstTime: " + getStringDate(usageStat.firstTimeStamp) + "\tlastTime: " + getStringDate(
                    usageStat.lastTimeUsed
                )
            )

            // アプリの使用が当日の範囲内であるかを確認
            if (usageStat.totalTimeInForeground > 0 &&
                usageStat.firstTimeStamp < endOfDay &&
                usageStat.lastTimeUsed >= startOfDay) {

                // データをフィルタリングしてリストに追加
                resultList.add(usageStat)
            }

        }
        return resultList
    }

//    private fun writeLogToFile(usageStats: List<UsageStats>){
//        try{
//            val logFile = File(context.getExternalFilesDir(null),"Log.txt")
//            val writer = FileWriter(logFile, true)
//
//            for (usageStat in usageStats){
//                if(usageStat.totalTimeInForeground == 0L){
//                    continue
//                }
//                val logMessage = "packageName: ${usageStat.packageName}\ttotalTimeDisplayed: ${usageStat.totalTimeInForeground}\tfirstTime: ${getStringDate(usageStat.firstTimeStamp)}\tlastTime: ${getStringDate(usageStat.lastTimeUsed)}"
//                writer.appendln(logMessage)
//            }
//        } catch (e:Exception) {
//            Log.e(TAG, "Error writing log to file",e)
//        }
//    }

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