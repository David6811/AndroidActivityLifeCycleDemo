package com.example.lifecycle

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.github.appintro.model.SliderPage

class IntroActivity : AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Notification permission slide
        addSlide(AppIntroFragment.createInstance(
            SliderPage(
                title = "Enable Notifications",
                description = "Allow notifications to see app updates and demonstration features. Tap 'Skip' to open notification settings manually.",
                imageDrawable = android.R.drawable.ic_popup_reminder,
                titleColorRes = android.R.color.black,
                descriptionColorRes = android.R.color.darker_gray,
                backgroundColorRes = android.R.color.white
            )
        ))

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            askForPermissions(
                permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                slideNumber = 1,
                required = false
            )
        }

        // Hide status bar
        showStatusBar(false)
        
        // Enable skip button
        isSkipButtonEnabled = true
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        openNotificationSettings()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        goToMainActivity()
    }

    private fun openNotificationSettings() {
        try {
            val intent = Intent()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            } else {
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.parse("package:$packageName")
            }
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback to main settings if specific settings fail
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
        goToMainActivity()
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}