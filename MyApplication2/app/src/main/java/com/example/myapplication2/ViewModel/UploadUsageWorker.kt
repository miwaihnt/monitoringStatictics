package com.example.myapplication2.ViewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

fun scheduleDailyUpload(context: Context) {

    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED) // ネットワーク接続時にのみ実行
        .setRequiresBatteryNotLow(true) // バッテリーが十分にある場合に実行
        .build()

    val uploadRequest = PeriodicWorkRequestBuilder<UploadUsageWorker>(1, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "UploadUsageWorker",
        ExistingPeriodicWorkPolicy.REPLACE,
        uploadRequest
    )
}


class UploadUsageWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val usageStatsViewModel: UsageStatsViewModel,
    private val uploadStatisticsViewModel: UploadStatisticsViewModel,
    private val fileUploadViewModel: FileUploadViewModel
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 使用統計情報の取得
            val usageStats = usageStatsViewModel.readOneDayUsageStats()

            // 画像アップロードのコールバックを定義
            val uploadAppIcon: (Uri, (String) -> Unit) -> Unit = { uri, onSuccess ->
                fileUploadViewModel.uploadAppIconImage(uri, onSuccess)
            }

            // Firebase Firestoreにデータをアップロード
            uploadStatisticsViewModel.uploadUsageState(usageStats, uploadAppIcon)

            Result.success()
        } catch (e: Exception) {
            Log.e("DailyUsageUploadWorker", "Error uploading usage data", e)
            Result.failure()
        }
    }
}