package com.example.lifecycle.appintro

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro2

class IntroActivity : AppIntro2() {
    
    private lateinit var preferences: IntroPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = IntroPreferences(this)
        setupIntro()
    }

    private fun setupIntro() {
        addSlide(NotificationPermissionFragment())
        showStatusBar(false)
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || preferences.isNavigationEnabled()) {
            goToMainActivity()
        }
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, com.example.lifecycle.activity.MainActivity::class.java))
        finish()
    }

    fun enableNavigation() = preferences.setNavigationEnabled(true)

    fun disableNavigation() = preferences.setNavigationEnabled(false)

}