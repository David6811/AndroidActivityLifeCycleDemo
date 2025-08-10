package com.example.lifecycle.appintro

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro2

class IntroActivity : AppIntro2() {
    
    // 导航控制标志，用于控制用户是否可以进入主界面
    private var isNavigationEnabled = false

    /**
     * Activity创建时的初始化方法
     * 继承自AppIntro2，用于设置引导页面的基本配置
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupIntro()
    }

    /**
     * 用户点击完成按钮时的处理方法
     * 只有在权限授予后才允许用户进入主界面，否则阻止导航
     * @param currentFragment 当前显示的Fragment实例
     */
    override fun onDonePressed(currentFragment: Fragment?) {
        if (shouldAllowNavigation()) {
            goToMainActivity()
        }
        // 如果权限未授予，用户将停留在当前权限请求页面
    }
    
    /**
     * 启用导航功能，允许用户进入主界面
     * 由NotificationPermissionFragment在用户授予权限后调用
     */
    fun enableNavigation() {
        isNavigationEnabled = true
    }
    
    /**
     * 禁用导航功能，阻止用户进入主界面
     * 由NotificationPermissionFragment在用户拒绝权限后调用
     */
    fun disableNavigation() {
        isNavigationEnabled = false
    }
    
    /**
     * 打开系统通知设置页面
     * 当系统阻止多次权限请求时，提供手动设置权限的备选方案
     * 包含异常处理，确保在权限设置页面无法打开时有降级方案
     */
    fun openNotificationSettings() {
        try {
            val intent = createNotificationSettingsIntent()
            startActivity(intent)
        } catch (e: Exception) {
            // 如果无法打开应用专用的通知设置，则打开系统通用设置页面
            Log.e("IntroActivity", "Failed to open notification settings", e)
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }
    
    /**
     * 设置引导页配置，初始化AppIntro2的所有必要设置
     * 
     * 主要配置包括：
     * 1. 添加NotificationPermissionFragment作为唯一的引导页面
     * 2. 隐藏系统状态栏创建沉浸式用户体验
     * 
     * 注意：由于只添加了一个Fragment，AppIntro2会自动显示"Done"按钮而不是"Next"按钮
     * 这符合我们的设计意图，让用户在授予权限后直接完成引导流程
     */
    private fun setupIntro() {
        // 添加通知权限请求Fragment作为引导页面的唯一slide
        // NotificationPermissionFragment包含权限请求UI和相关的MVP逻辑
        addSlide(NotificationPermissionFragment())
        
        // 隐藏Android系统状态栏，创建全屏沉浸式体验
        // 这样用户可以专注于权限授予流程，不被其他系统UI元素干扰
        showStatusBar(false)
        
        // 注意：我们没有设置isSkipButtonEnabled，因为不需要跳过按钮
        // 用户必须处理权限请求才能继续使用应用
    }
    
    /**
     * 判断是否允许用户导航到主界面
     * Android 13+需要用户明确授予通知权限，低版本则自动允许
     * @return true表示允许导航，false表示阻止导航
     */
    private fun shouldAllowNavigation(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+需要检查权限授予状态
            isNavigationEnabled
        } else {
            // Android 12及以下版本无需通知权限，直接允许导航
            true
        }
    }
    
    /**
     * 创建打开通知设置页面的Intent
     * 根据不同Android版本创建相应的Intent以打开通知设置页面
     * @return 用于打开系统设置的Intent对象
     */
    private fun createNotificationSettingsIntent(): Intent {
        return Intent().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Android 8.0+支持直接打开应用的通知设置页面
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            } else {
                // Android 8.0以下版本打开应用详情页面
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.parse("package:$packageName")
            }
        }
    }

    /**
     * 跳转到主界面并结束当前Activity
     * 完成权限授予流程后的最终导航操作
     * 使用finish()确保用户无法通过返回键回到权限请求页面
     */
    private fun goToMainActivity() {
        startActivity(Intent(this, com.example.lifecycle.activity.MainActivity::class.java))
        finish() // 结束当前Activity，防止用户返回权限请求页面
    }
}