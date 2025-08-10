package com.example.lifecycle.appintro

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro2

class IntroActivity : AppIntro2() {
    
    companion object {
        private const val PREFS_NAME = "intro_prefs"
        private const val KEY_NAVIGATION_ENABLED = "navigation_enabled"
    }
    
    private lateinit var prefs: SharedPreferences
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        setupIntro()
    }

    private fun setupIntro() {
        addSlide(NotificationPermissionFragment())
        showStatusBar(false)
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        if (shouldAllowNavigation()) {
            goToMainActivity()
        }
    }
    
    fun enableNavigation() {
        saveNavigationState(true)
    }
    
    fun disableNavigation() {
        saveNavigationState(false)
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
    

    
    private fun shouldAllowNavigation(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            loadNavigationState()
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

    private fun loadNavigationState(): Boolean {
        return prefs.getBoolean(KEY_NAVIGATION_ENABLED, false)
    }

    private fun saveNavigationState(enabled: Boolean) {
        prefs.edit()
            .putBoolean(KEY_NAVIGATION_ENABLED, enabled)
            .apply()
    }
}