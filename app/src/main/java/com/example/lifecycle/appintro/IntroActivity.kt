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
    
    // SharedPreferences相关常量
    companion object {
        private const val PREFS_NAME = "intro_prefs"
        private const val KEY_NAVIGATION_ENABLED = "navigation_enabled"
    }
    
    // SharedPreferences实例，用于持久化存储导航状态
    private lateinit var prefs: SharedPreferences
    

    /**
     * Activity创建时的初始化方法
     * 继承自AppIntro2，用于设置引导页面的基本配置
     * 初始化SharedPreferences并恢复之前保存的导航状态
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        
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
     * 同时将状态持久化保存
     */
    fun enableNavigation() {
        saveNavigationState(true)
    }
    
    /**
     * 禁用导航功能，阻止用户进入主界面
     * 同时将状态持久化保存
     */
    fun disableNavigation() {
        saveNavigationState(false)
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
            Log.e("IntroActivity", "Failed to open notification settings", e)
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }
    
    /**
     * 设置引导页配置，初始化AppIntro2的所有必要设置
     * 添加通知权限Fragment并配置沉浸式体验
     */
    private fun setupIntro() {
        addSlide(NotificationPermissionFragment())
        showStatusBar(false)
    }
    
    /**
     * 判断是否允许用户导航到主界面
     * Android 13+需要用户明确授予通知权限，低版本则自动允许
     * @return true表示允许导航，false表示阻止导航
     */
    private fun shouldAllowNavigation(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            loadNavigationState()
        } else {
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
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            } else {
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
        finish()
    }

    /**
     * 从SharedPreferences加载导航状态
     * @return 如果之前用户已授予权限返回true，否则返回false
     */
    private fun loadNavigationState(): Boolean {
        return prefs.getBoolean(KEY_NAVIGATION_ENABLED, false)
    }

    /**
     * 将导航状态保存到SharedPreferences
     * @param enabled 是否启用导航功能
     */
    private fun saveNavigationState(enabled: Boolean) {
        prefs.edit()
            .putBoolean(KEY_NAVIGATION_ENABLED, enabled)
            .apply()
    }
}