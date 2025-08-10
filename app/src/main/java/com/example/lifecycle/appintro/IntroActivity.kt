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

    // 设置介绍页面 - 添加权限请求fragment并隐藏状态栏
    private fun setupIntro() {
        addSlide(NotificationPermissionFragment())
        showStatusBar(false)
    }

    // 检查是否允许导航到主界面 - 对于Android 13以下自动允许，对于Android 13+检查权限状态
    override fun onDonePressed(currentFragment: Fragment?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || preferences.isNavigationEnabled()) {
            goToMainActivity()
        }
    }

    // 导航到主界面并结束当前activity
    private fun goToMainActivity() {
        preferences.setIntroCompleted(true)
        startActivity(Intent(this, com.example.lifecycle.activity.MainActivity::class.java))
        finish()
    }

}