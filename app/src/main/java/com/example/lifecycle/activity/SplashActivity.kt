package com.example.lifecycle.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.lifecycle.appintro.IntroActivity
import com.example.lifecycle.appintro.IntroPreferences

@SuppressLint("CustomSplashScreen")
class SplashActivity : Activity() {

    companion object {
        private const val TAG = "SplashActivity_Lifecycle"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { false }
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        
        // Navigate based on intro completion status
        val isIntroCompleted = IntroPreferences(this).isIntroCompleted()
        val targetActivity = if (isIntroCompleted) MainActivity::class.java else IntroActivity::class.java
        startActivity(Intent(this, targetActivity))
        finish()
    }

    override fun onStart() { super.onStart(); Log.d(TAG, "onStart") }
    override fun onResume() { super.onResume(); Log.d(TAG, "onResume") }
    override fun onPause() { super.onPause(); Log.d(TAG, "onPause") }
    override fun onStop() { super.onStop(); Log.d(TAG, "onStop") }
    override fun onRestart() { super.onRestart(); Log.d(TAG, "onRestart") }
    override fun onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy") }
}