package com.example.myapplication2

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
    // アプリケーション全体で共有される初期化処理などを記述することができます
}
