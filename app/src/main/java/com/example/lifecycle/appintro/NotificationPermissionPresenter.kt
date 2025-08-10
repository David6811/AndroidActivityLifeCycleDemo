package com.example.lifecycle.appintro

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * 通知权限业务逻辑处理类
 * 负责处理权限申请的核心逻辑，包括权限检查、重试机制等
 * MVP模式中的Presenter层，分离UI和业务逻辑
 */
class NotificationPermissionPresenter(
    private val context: Context,
    private var view: NotificationPermissionFragment?
) {
    
    companion object {
        // Android 13+的通知权限常量
        private const val NOTIFICATION_PERMISSION = "android.permission.POST_NOTIFICATIONS"
    }

    /**
     * Fragment视图创建完成时调用
     * 预留用于初始化相关逻辑
     */
    fun onViewCreated() {
        // 初始化设置已完成
    }
    
    /**
     * Fragment恢复时调用，检查并更新权限状态
     * 用户从设置页面返回时会重新检查权限
     */
    fun onViewResumed() {
        checkAndUpdatePermissionState()
    }
    
    /**
     * 权限按钮点击事件处理
     * 根据不同情况决定采取何种权限请求策略
     */
    fun onPermissionButtonClicked() {
        when {
            !isAndroid13OrHigher() -> handleOlderAndroidVersions()
            hasNotificationPermission() -> handleAlreadyGranted()
            shouldGoToSettings() -> view?.openNotificationSettings()
            else -> view?.showSystemPermissionDialog()
        }
    }
    
    /**
     * 处理系统权限对话框的结果
     * @param isGranted 用户是否授予了权限
     */
    fun onSystemPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            handlePermissionGranted()
        } else {
            handlePermissionDenied()
        }
    }
    
    /**
     * Fragment销毁时清理引用，防止内存泄漏
     */
    fun onDestroy() {
        view = null
    }
    
    /**
     * 检查当前权限状态并更新UI
     * 在Fragment恢复时调用，确保UI状态与实际权限状态同步
     */
    private fun checkAndUpdatePermissionState() {
        when {
            !isAndroid13OrHigher() -> handleOlderAndroidVersions()
            hasNotificationPermission() -> handlePermissionGranted()
            else -> handlePermissionDenied()
        }
    }
    
    /**
     * 处理Android 13以下版本的情况
     * 这些版本不需要显式的通知权限，直接允许导航
     */
    private fun handleOlderAndroidVersions() {
        view?.showPermissionGrantedUI()
        view?.enableNavigation()
    }
    
    /**
     * 处理权限已授予的情况
     * 显示已授予UI并允许用户继续
     */
    private fun handleAlreadyGranted() {
        view?.showPermissionGrantedUI()
        view?.enableNavigation()
    }
    
    /**
     * 处理权限授予成功的情况
     * 更新UI并启用导航
     */
    private fun handlePermissionGranted() {
        view?.showPermissionGrantedUI()
        view?.enableNavigation()
    }
    
    /**
     * 处理权限被拒绝的情况
     * 更新UI状态并禁用导航
     */
    private fun handlePermissionDenied() {
        view?.showPermissionDeniedUI(shouldGoToSettings())
        view?.disableNavigation()
    }
    
    /**
     * 判断当前系统版本是否为Android 13或更高版本
     * @return true表示需要显式申请通知权限
     */
    private fun isAndroid13OrHigher(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    
    /**
     * 检查是否已获得通知权限
     * @return true表示已授予权限或不需要权限（低版本系统）
     */
    private fun hasNotificationPermission(): Boolean {
        return if (isAndroid13OrHigher()) {
            ContextCompat.checkSelfPermission(context, NOTIFICATION_PERMISSION) == PackageManager.PERMISSION_GRANTED
        } else {
            true // 低版本系统始终视为已授予
        }
    }
    
    /**
     * 判断是否应该直接跳转到设置页面
     * 使用shouldShowRequestPermissionRationale来判断用户是否已经拒绝过权限
     * @return true表示应该跳转设置页面，false表示可以继续请求权限
     */
    private fun shouldGoToSettings(): Boolean {
        return if (isAndroid13OrHigher() && view is Fragment) {
            !ActivityCompat.shouldShowRequestPermissionRationale(
                (view as Fragment).requireActivity(),
                NOTIFICATION_PERMISSION
            ) && !hasNotificationPermission()
        } else {
            false
        }
    }
}