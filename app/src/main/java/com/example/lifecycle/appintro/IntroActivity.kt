package com.example.lifecycle.appintro

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || loadNavigationState()) {
            goToMainActivity()
        }
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, com.example.lifecycle.activity.MainActivity::class.java))
        finish()
    }

    fun enableNavigation() = saveNavigationState(true)

    fun disableNavigation() = saveNavigationState(false)

    private fun loadNavigationState() = prefs.getBoolean(KEY_NAVIGATION_ENABLED, false)

    private fun saveNavigationState(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NAVIGATION_ENABLED, enabled).apply()
    }

}