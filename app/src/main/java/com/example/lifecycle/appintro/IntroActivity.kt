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

    // Activity创建时的初始化方法
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupIntro()
    }

    // 用户点击完成按钮时的处理方法
    override fun onDonePressed(currentFragment: Fragment?) {
        if (shouldAllowNavigation()) {
            goToMainActivity()
        }
    }
    
    // 启用导航功能，允许用户进入主界面
    fun enableNavigation() {
        isNavigationEnabled = true
    }
    
    // 禁用导航功能，阻止用户进入主界面
    fun disableNavigation() {
        isNavigationEnabled = false
    }
    
    // 打开系统通知设置页面
    fun openNotificationSettings() {
        try {
            val intent = createNotificationSettingsIntent()
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("IntroActivity", "Failed to open notification settings", e)
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }
    
    // 设置引导页配置，添加权限请求页面
    private fun setupIntro() {
        addSlide(NotificationPermissionFragment())
        showStatusBar(false)
    }
    
    // 判断是否允许用户导航到下一页
    private fun shouldAllowNavigation(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isNavigationEnabled
        } else {
            true
        }
    }
    
    // 创建打开通知设置页面的Intent
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

    // 跳转到主界面并结束当前Activity
    private fun goToMainActivity() {
        startActivity(Intent(this, com.example.lifecycle.activity.MainActivity::class.java))
        finish()
    }
}