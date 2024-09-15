package com.example.myapplication2.ViewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.myapplication2.UploadStatictics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor (
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage // FirebaseStorageの依存を追加
) :ViewModel() {

    fun logIn(
        email: String,
        password: String,
        navController: NavController,
        usageStatsViewModel: UsageStatsViewModel,
        statisticsViewModel: UploadStatisticsViewModel,
        fileUploadViewModel: FileUploadViewModel
    )  {

        if (email.isNotBlank() && password.isNotBlank()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        navController.navigate("Home")
                        Log.d("performLogin", "Succcess")

                        viewModelScope.launch {
                            uploadStatisticsIfNeeded(usageStatsViewModel, statisticsViewModel, fileUploadViewModel)
                        }

                    } else {
                        Log.d("performLogin", "Failed")
                        val errorCode = task.exception?.message
                        Log.e("performLogin", "ErrorCode: $errorCode")
                    }
                }
        } else {
            Log.d("performLogin","Failed")
        }
    }

    // 権限チェックと統計情報のアップロードを管理
    suspend fun uploadStatisticsIfNeeded(
        usageStatsViewModel: UsageStatsViewModel,
        statisticsViewModel: UploadStatisticsViewModel,
        fileUploadViewModel: FileUploadViewModel // ここに追加
    ) {
        if (!usageStatsViewModel.checkUsageStatsPermission()) {
            usageStatsViewModel.requestUsageStatsPermission()
        } else {
            val usageStats = usageStatsViewModel.readOneDayUsageStats()

            // 画像アップロードのコールバックを定義
            val uploadAppIcon: (Uri, (String) -> Unit) -> Unit = { uri, onSuccess ->
                fileUploadViewModel.uploadAppIconImage(uri, onSuccess)
            }

            statisticsViewModel.uploadUsageState(usageStats, uploadAppIcon)
        }
    }

}

