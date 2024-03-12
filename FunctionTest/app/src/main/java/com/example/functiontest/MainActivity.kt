package com.websarva.wings.android.preusm2

import android.Manifest
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private val REQUEST_USAGE_STATS = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestUsageStatsPermission()
    }

    private fun requestUsageStatsPermission() {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(), packageName
        )
        val granted = mode == AppOpsManager.MODE_ALLOWED

        if (!granted) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivityForResult(intent, REQUEST_USAGE_STATS)
        } else {
            writeUsageStatsToFile()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_USAGE_STATS) {
            if (hasUsageStatsPermission()) {
                writeUsageStatsToFile()
            } else {
                Log.d(TAG, "Usage stats permission denied")
            }
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(), packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun writeUsageStatsToFile() {
        val usageStats = getUsageStats()
        val file = createUsageStatsFile()

        try {
            val writer = BufferedWriter(FileWriter(file))
            for (stat in usageStats) {
                writer.write("Package Name: ${stat.packageName}, Total Time: ${stat.totalTimeInForeground}ms\n")
            }
            writer.close()
            Log.d(TAG, "Usage stats written to file: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Error writing usage stats to file", e)
        }
    }

    private fun getUsageStats(): List<UsageStats> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -1) // Get stats for the last day

        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        return usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            calendar.timeInMillis,
            System.currentTimeMillis()
        )
    }

    private fun createUsageStatsFile(): File {
        val directory = getExternalFilesDir(null)
        val fileName = "usage_stats.txt"
        return File(directory, fileName)
    }
}
