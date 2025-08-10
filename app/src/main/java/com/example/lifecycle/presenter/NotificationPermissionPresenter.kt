package com.example.lifecycle.presenter

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.lifecycle.contract.NotificationPermissionContract

class NotificationPermissionPresenter(
    private val context: Context,
    private var view: NotificationPermissionContract.View?
) : NotificationPermissionContract.Presenter {
    
    companion object {
        private const val MAX_SYSTEM_REQUESTS = 2
        private const val NOTIFICATION_PERMISSION = "android.permission.POST_NOTIFICATIONS"
    }
    
    private var denialCount = 0
    
    override fun onViewCreated() {
        // Initial setup completed
    }
    
    override fun onViewResumed() {
        checkAndUpdatePermissionState()
    }
    
    override fun onPermissionButtonClicked() {
        when {
            !isAndroid13OrHigher() -> handleOlderAndroidVersions()
            hasNotificationPermission() -> handleAlreadyGranted()
            denialCount >= MAX_SYSTEM_REQUESTS -> view?.showCustomPermissionDialog()
            else -> view?.showSystemPermissionDialog()
        }
    }
    
    override fun onSystemPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            handlePermissionGranted()
        } else {
            handlePermissionDenied()
        }
    }
    
    override fun onCustomDialogAllowClicked() {
        view?.openNotificationSettings()
    }
    
    override fun onCustomDialogDenyClicked() {
        handlePermissionDenied()
    }
    
    override fun onDestroy() {
        view = null
    }
    
    private fun checkAndUpdatePermissionState() {
        when {
            !isAndroid13OrHigher() -> handleOlderAndroidVersions()
            hasNotificationPermission() -> handlePermissionGranted()
            else -> handlePermissionDenied(shouldIncrementCount = false)
        }
    }
    
    private fun handleOlderAndroidVersions() {
        view?.showPermissionGrantedUI()
        view?.enableNavigation()
    }
    
    private fun handleAlreadyGranted() {
        view?.showPermissionGrantedUI()
        view?.enableNavigation()
    }
    
    private fun handlePermissionGranted() {
        denialCount = 0
        view?.showPermissionGrantedUI()
        view?.enableNavigation()
    }
    
    private fun handlePermissionDenied(shouldIncrementCount: Boolean = true) {
        if (shouldIncrementCount) {
            denialCount++
        }
        view?.showPermissionDeniedUI(denialCount >= MAX_SYSTEM_REQUESTS)
        view?.disableNavigation()
    }
    
    private fun isAndroid13OrHigher(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    
    private fun hasNotificationPermission(): Boolean {
        return if (isAndroid13OrHigher()) {
            ContextCompat.checkSelfPermission(context, NOTIFICATION_PERMISSION) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Always granted on older versions
        }
    }
}