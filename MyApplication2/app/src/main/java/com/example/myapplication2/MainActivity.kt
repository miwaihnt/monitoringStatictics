package com.example.myapplication2

import android.util.Log
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.myapplication2.View2.FileUploadScreen
import com.example.myapplication2.View2.ListFriendsUI
import com.example.myapplication2.View2.LogInView
import com.example.myapplication2.View2.NavGraph
import com.example.myapplication2.View2.Profile
import com.example.myapplication2.ViewModel.FollowData
import com.example.myapplication2.ViewModel.scheduleDailyUpload
import com.example.myapplication2.ui.theme.MyApplication2Theme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity222"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var UploadStatictics: UploadStatictics
    private lateinit var userRegistration: UserRegistration
    private lateinit var dbInfoGet: dbInfoGet
    private lateinit var dbAddFollowData: dbAddFollowData
    private lateinit var dbgetDocumentId: dbgetDocumentId
    private lateinit var getStatistics:getStatistics
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 定期タスクのスケジュールを設定
        scheduleDailyUpload(this)

        val FirebaseFirestore = FirebaseFirestore.getInstance()
        UploadStatictics = UploadStatictics(FirebaseFirestore)
        userRegistration = UserRegistration(FirebaseFirestore)
        dbInfoGet = dbInfoGet(FirebaseFirestore)
        dbAddFollowData = dbAddFollowData(FirebaseFirestore)
        dbgetDocumentId = dbgetDocumentId(FirebaseFirestore)
        getStatistics = getStatistics(FirebaseFirestore)
        auth = FirebaseAuth.getInstance()
        setContent {
            MyApplication2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {

                        //新：Hiltテスト用
//                      ListFriendsUI()
//                      FileUploadScreen()
                      NavGraph()
//                      profile()

//                    旧：統計情報取得アプリ用
//                   val usageStatsClass = UsageStatsClass(this)
//                   val usageStats = usageStatsClass.readOneDayUsageStats()
//                   DisplayNav(userRegistration,auth,dbInfoGet,dbAddFollowData,FirebaseFirestore,usageStats,getStatistics,)
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG,"onStart Called")
        val currentUser = auth.currentUser
        if(currentUser != null){
            currentUser.reload().addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    Log.d(TAG,"User reloaded")
                } else {
                    Log.e(TAG,"Failed to reload user")
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause Called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop Called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy Called")
    }
}

//統計情報の状態確認
//fun checkReadStatsPermission(context: Context): Boolean {
//    val aom: AppOpsManager? = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
//    if (aom != null) {
//        val mode: Int = aom.checkOp(
//            AppOpsManager.OPSTR_GET_USAGE_STATS,
//            android.os.Process.myUid(),
//            context.packageName
//        )
//        if (mode == AppOpsManager.MODE_DEFAULT) {
//            if (context.checkPermission(
//                    "android.permission.PACKAGE_USAGE_STATS",
//                    android.os.Process.myPid(),
//                    android.os.Process.myUid()
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                // 使用状況統計の権限がある場合
//                return true
//            } else {
//                // 使用状況統計の権限がない場合
//                // 画面遷移などの処理を行う
//                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
//                return false
//            }
//        } else {
//            // MODE_DEFAULTでない場合は、MODE_ALLOWEDかMODE_IGNOREのいずれか
//            return mode == AppOpsManager.MODE_ALLOWED
//        }
//    } else {
//        // aom が null の場合のエラー処理など
//        return false
//    }
//}


