package com.example.myapplication2

import android.util.Log
import android.Manifest
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.provider.Settings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication2.ui.theme.MyApplication2Theme
import com.google.firebase.firestore.FirebaseFirestore



class MainActivity : ComponentActivity() {
    private lateinit var dbConnection: dbConnection
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbConnection = dbConnection(FirebaseFirestore.getInstance())
        setContent {
            MyApplication2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                    val permissionResult = checkReadStatsPermission(this)
                    Log.d("PermissionCheck", "Result: $permissionResult")
                    val usageStatsClass = UsageStatsClass(this)
                    usageStatsClass.readOneDayUsageStats()
                    val usageStats = usageStatsClass.readOneDayUsageStats()
                    dbConnection.uploadusestate(usageStats)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

fun checkReadStatsPermission(context: Context): Boolean {
    val aom: AppOpsManager? = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
    if (aom != null) {
        val mode: Int = aom.checkOp(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        if (mode == AppOpsManager.MODE_DEFAULT) {
            if (context.checkPermission(
                    "android.permission.PACKAGE_USAGE_STATS",
                    android.os.Process.myPid(),
                    android.os.Process.myUid()
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // 使用状況統計の権限がある場合
                return true
            } else {
                // 使用状況統計の権限がない場合
                // 画面遷移などの処理を行う
                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                return false
            }
        } else {
            // MODE_DEFAULTでない場合は、MODE_ALLOWEDかMODE_IGNOREのいずれか
            return mode == AppOpsManager.MODE_ALLOWED
        }
    } else {
        // aom が null の場合のエラー処理など
        return false
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplication2Theme {
        Greeting("Android")
    }
}