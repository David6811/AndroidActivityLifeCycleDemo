package com.example.lifecycle.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.lifecycle.fragment.NotificationPermissionFragment
import com.github.appintro.AppIntro2

class IntroActivity : AppIntro2() {
    
    private var isNavigationEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupIntro()
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        openNotificationSettings()
    }

    override fun onNextPressed(currentFragment: Fragment?) {
        if (shouldAllowNavigation()) {
            goToMainActivity()
        }
    }
    
    override fun onDonePressed(currentFragment: Fragment?) {
        if (shouldAllowNavigation()) {
            goToMainActivity()
        }
    }
    
    fun enableNavigation() {
        isNavigationEnabled = true
    }
    
    fun disableNavigation() {
        isNavigationEnabled = false
    }
    
    fun openNotificationSettings() {
        try {
            val intent = createNotificationSettingsIntent()
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("IntroActivity", "Failed to open notification settings", e)
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }
    
    private fun setupIntro() {
        addSlide(NotificationPermissionFragment())
        showStatusBar(false)
        isSkipButtonEnabled = true
    }
    
    private fun shouldAllowNavigation(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isNavigationEnabled
        } else {
            true
        }
    }
    
    private fun createNotificationSettingsIntent(): Intent {
        return Intent().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            } else {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.parse("package:$packageName")
            }
        }
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, com.example.lifecycle.activity.MainActivity::class.java))
        finish()
    }
}