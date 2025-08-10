package com.example.lifecycle.appintro

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
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
    
    private var hasRequestedBefore = false

    fun onViewResumed() {
        when {
            !isAndroid13OrHigher() -> handleOlderAndroidVersions()
            hasNotificationPermission() -> handlePermissionGranted()
            else -> handlePermissionDenied()
        }
    }

    fun onPermissionButtonClicked() {
        when {
            !isAndroid13OrHigher() -> handleOlderAndroidVersions()
            hasNotificationPermission() -> handleAlreadyGranted()
            shouldGoToSettings() -> view?.openNotificationSettings()
            else -> {
                hasRequestedBefore = true
                view?.showSystemPermissionDialog()
            }
        }
    }

    fun onSystemPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            handlePermissionGranted()
        } else {
            handlePermissionDenied()
        }
    }

    fun onDestroy() {
        view = null
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
        view?.showPermissionGrantedUI()
        view?.enableNavigation()
    }

    private fun handlePermissionDenied() {
        view?.showPermissionDeniedUI(shouldGoToSettings())
        view?.disableNavigation()
    }

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
    
    private fun shouldGoToSettings(): Boolean {
        return if (isAndroid13OrHigher() && view is Fragment && !hasNotificationPermission() && hasRequestedBefore) {
            !ActivityCompat.shouldShowRequestPermissionRationale(
                (view as Fragment).requireActivity(),
                NOTIFICATION_PERMISSION
            )
        } else {
            false
        }
    }

}