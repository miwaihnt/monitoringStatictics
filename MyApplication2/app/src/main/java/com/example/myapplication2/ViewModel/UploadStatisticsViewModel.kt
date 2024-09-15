package com.example.myapplication2.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import android.util.Log
import android.app.usage.UsageStats
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import java.util.HashMap
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class UploadStatisticsViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth
) : ViewModel() {

    val TAG = "dbConnection"
    val tag = "dailyStatus"
    private val currentDateTime = LocalDateTime.now()
    private val FormatDate = getFormattedDateTime()

    private fun getFormattedDateTime(): String {
        return currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    private fun getStringDate(milliseconds: Long): String {
        val df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPANESE)
        val date = Date(milliseconds)
        return df.format(date)
    }

    // パッケージ名からアプリ名を取得するメソッド
    private fun getAppName(packageName: String): String? {
        return try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            null // アプリ名が取得できなかった場合はnullを返す
        }
    }

    private fun getAppIconUri(packageName: String): Uri? {
        return try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val iconDrawable = packageManager.getApplicationIcon(appInfo)

            // アイコンがAdaptiveIconDrawableかBitmapDrawableかをチェック
            val bitmap: Bitmap = when (iconDrawable) {
                is BitmapDrawable -> iconDrawable.bitmap
                is AdaptiveIconDrawable -> {
                    // AdaptiveIconDrawableの場合、Bitmapに変換
                    val bitmap = Bitmap.createBitmap(
                        iconDrawable.intrinsicWidth,
                        iconDrawable.intrinsicHeight,
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = Canvas(bitmap)
                    iconDrawable.setBounds(0, 0, canvas.width, canvas.height)
                    iconDrawable.draw(canvas)
                    bitmap
                }
                else -> throw IllegalArgumentException("Unsupported drawable type")
            }

            // アイコン画像を一時ファイルに保存してURIを取得
            val file = File(context.cacheDir, "$packageName.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            Uri.fromFile(file)
        } catch (e: Exception) {
            Log.e("getAppIconUri", "Error retrieving app icon", e)
            null
        }
    }


    // アプリ使用統計をFirebaseにアップロード
    fun uploadUsageState(
        usageStats: List<UsageStats>,
        uploadImage: (Uri, (String) -> Unit) -> Unit // コールバックとして画像アップロード関数を渡す
    ) {
        val currentUser = auth.currentUser
        Log.d(TAG, "currentUser: $currentUser")

        if (currentUser != null) {
            val uid = currentUser.uid
            Log.d("UploadStatictics", "uid: $uid")

            val data = HashMap<String, Any>()
            val pendingUploads = mutableListOf<Deferred<Unit>>()

            viewModelScope.launch {
                for (usageStat in usageStats) {
                    val appName = getAppName(usageStat.packageName) ?: continue
                    val iconUri = getAppIconUri(usageStat.packageName) ?: continue

                    val uploadDeferred = async {
                        val imageUploadDeferred = CompletableDeferred<String>()
                        uploadImage(iconUri) { downloadUrl ->
                            imageUploadDeferred.complete(downloadUrl)
                        }
                        val imageUrl = imageUploadDeferred.await()
                        val usageData = hashMapOf(
                            "appName" to appName,
                            "packageName" to usageStat.packageName,
                            "totalTimeInForeground" to usageStat.totalTimeInForeground,
                            "firstTime" to getStringDate(usageStat.firstTimeStamp),
                            "lastTime" to getStringDate(usageStat.lastTimeUsed),
                            "iconUrl" to imageUrl // 画像のURLを追加
                        )
                        data[appName] = usageData
                    }
                    pendingUploads.add(uploadDeferred)
                }

                // すべての画像アップロードが完了するまで待つ
                pendingUploads.awaitAll()

                try {
                    val userDocRef = db.collection("statistics").document(uid)
                    userDocRef.set(mapOf("createdAt" to FieldValue.serverTimestamp()))
                    userDocRef.collection("dailyStatistics")
                        .document(FormatDate)
                        .set(data)
                    Log.d("uploadStatisctics", "successful data: $data")
                } catch (e: Exception) {
                    Log.e(tag, "Failure upload", e)
                }
            }
        } else {
            Log.e(TAG, "currentUserはnullです")
        }
    }
}