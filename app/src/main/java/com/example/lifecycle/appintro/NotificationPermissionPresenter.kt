package com.example.lifecycle.appintro

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class NotificationPermissionPresenter(
    private val context: Context,
    private var view: NotificationPermissionFragment?
) {

    companion object {
        private const val NOTIFICATION_PERMISSION = "android.permission.POST_NOTIFICATIONS"
    }
    
    private val preferences = IntroPreferences(context)

    // =========================== Public Event Handlers ===========================

    fun onViewResumed() {
        when {
            !isAndroid13OrHigher() -> handleOlderAndroidVersions()
            hasNotificationPermission() -> handlePermissionGranted()
            else -> handlePermissionDenied()
        }
    }

    // 用户点击权限按钮 - 简化逻辑：总是先尝试系统对话框
    fun onPermissionButtonClicked() {
        when {
            !isAndroid13OrHigher() -> handleOlderAndroidVersions()
            hasNotificationPermission() -> handleAlreadyGranted()
            else -> view?.showSystemPermissionDialog() // 直接弹出系统dialog
        }
    }

    fun onSystemPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            handlePermissionGranted()
        } else {
            // 权限被拒绝后，直接跳转到设置页面（避免二次弹框）
            handlePermissionDeniedWithSettings()
        }
    }

    fun onDestroy() {
        view = null
    }

    // =========================== Permission State Handlers ===========================

    private fun handleOlderAndroidVersions() {
        view?.showPermissionGrantedUI()
        preferences.setNavigationEnabled(true)
    }

    private fun handleAlreadyGranted() {
        view?.showPermissionGrantedUI()
        preferences.setNavigationEnabled(true)
    }

    private fun handlePermissionGranted() {
        view?.showPermissionGrantedUI()
        preferences.setNavigationEnabled(true)
    }

    private fun handlePermissionDenied() {
        view?.showPermissionDeniedUI(false) // 初始状态不显示设置提示
        preferences.setNavigationEnabled(false)
    }

    // 权限被拒绝后直接跳转到设置页面
    private fun handlePermissionDeniedWithSettings() {
        view?.showPermissionDeniedUI(true)
        preferences.setNavigationEnabled(false)
        // 直接打开设置页面，不需要用户再次点击
        openNotificationSettings()
    }

    // =========================== Permission Check Methods ===========================

    private fun isAndroid13OrHigher(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    private fun hasNotificationPermission(): Boolean {
        return if (isAndroid13OrHigher()) {
            ContextCompat.checkSelfPermission(
                context,
                NOTIFICATION_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    
    // 不再需要shouldGoToSettings方法，简化为直接跳转

    // =========================== Settings Navigation Methods ===========================

    private fun openNotificationSettings() {
        val intent = createNotificationSettingsIntent()
        context.startActivity(intent)
    }

    private fun createNotificationSettingsIntent() = Intent().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        } else {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.parse("package:${context.packageName}")
        }
    }

}