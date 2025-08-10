package com.example.lifecycle.appintro

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.lifecycle.R

class NotificationPermissionFragment : Fragment() {
    
    companion object {
        private const val NOTIFICATION_PERMISSION = "android.permission.POST_NOTIFICATIONS"
    }
    
    private lateinit var permissionButton: Button
    private lateinit var preferences: IntroPreferences
    
    // =========================== Fragment Lifecycle Methods ===========================
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_notification_permission, container, false)
        setupViews(view)
        setupPreferences()
        return view
    }
    
    override fun onResume() {
        super.onResume()
        checkAndUpdatePermissionState()
    }
    
    // =========================== Initialization Methods ===========================
    
    private fun setupViews(view: View) {
        permissionButton = view.findViewById(R.id.btn_allow_notifications)
        permissionButton.setOnClickListener { onPermissionButtonClicked() }
    }
    
    private fun setupPreferences() {
        preferences = IntroPreferences(requireContext())
    }
    
    // =========================== Permission Event Handlers ===========================
    
    // Check and update permission state when page is displayed
    private fun checkAndUpdatePermissionState() {
        if (hasNotificationPermission()) {
            handlePermissionGranted()
        } else {
            handlePermissionDenied()
        }
    }
    
    // User clicks permission button - directly navigate to settings page
    private fun onPermissionButtonClicked() {
        if (hasNotificationPermission()) {
            handlePermissionGranted()
        } else {
            openNotificationSettings()
        }
    }
    
    // =========================== Permission State Handlers ===========================
    
    // Permission granted - show success state and enable navigation
    private fun handlePermissionGranted() {
        showPermissionGrantedUI()
        preferences.setNavigationEnabled(true)
    }
    
    // Permission denied - show settings hint and disable navigation
    private fun handlePermissionDenied() {
        showPermissionDeniedUI()
        preferences.setNavigationEnabled(false)
    }
    
    // =========================== UI State Management Methods ===========================
    
    // Show UI state for granted permission
    private fun showPermissionGrantedUI() {
        permissionButton.apply {
            text = getString(R.string.notifications_enabled)
            isEnabled = false
            alpha = 0.6f
        }
    }
    
    // Show UI state for denied permission
    private fun showPermissionDeniedUI() {
        permissionButton.apply {
            text = getString(R.string.allow_notifications_required)
            isEnabled = true
            alpha = 1.0f
        }
    }
    
    // =========================== Permission Check Methods ===========================
    
    // Check if notification permission is granted (only needed for Android 13+)
    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                NOTIFICATION_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Auto-granted for Android versions below 13
        }
    }
    
    // =========================== Settings Navigation Methods ===========================
    
    // Open system notification settings page
    private fun openNotificationSettings() {
        val intent = createNotificationSettingsIntent()
        startActivity(intent)
    }
    
    // Create settings page intent (compatible with different Android versions)
    private fun createNotificationSettingsIntent() = Intent().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
        } else {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.parse("package:${requireContext().packageName}")
        }
    }
}