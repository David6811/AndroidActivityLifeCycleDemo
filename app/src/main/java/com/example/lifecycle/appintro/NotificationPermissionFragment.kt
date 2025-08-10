package com.example.lifecycle.appintro

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.lifecycle.R

/**
 * 通知权限请求Fragment
 * 负责处理Android 13+的通知权限申请流程和UI交互
 * 使用MVP模式，通过Presenter处理业务逻辑
 */
class NotificationPermissionFragment : Fragment() {
    
    companion object {
        private const val TAG = "PermissionFragment"
    }
    
    // UI组件：权限请求按钮
    private lateinit var permissionButton: Button
    
    // 系统权限请求启动器，用于调用Android系统权限对话框
    private lateinit var permissionLauncher: androidx.activity.result.ActivityResultLauncher<String>
    
    // MVP模式的Presenter，处理权限相关的业务逻辑
    private lateinit var presenter: NotificationPermissionPresenter
    
    /**
     * Fragment视图创建时的初始化方法
     * 加载布局并初始化所有必要组件
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_notification_permission, container, false)
        setupViews(view)
        setupPresenter()
        setupPermissionLauncher()
        presenter.onViewCreated()
        return view
    }
    
    /**
     * Fragment恢复时检查权限状态
     * 用户从设置页面返回时可能已授予权限
     */
    override fun onResume() {
        super.onResume()
        presenter.onViewResumed()
    }
    
    /**
     * Fragment销毁时清理Presenter引用，防止内存泄漏
     */
    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
    
    /**
     * 初始化UI组件并设置点击监听
     * 将按钮点击事件委托给Presenter处理
     */
    private fun setupViews(view: View) {
        permissionButton = view.findViewById(R.id.btn_allow_notifications)
        permissionButton.setOnClickListener { presenter.onPermissionButtonClicked() }
    }
    
    /**
     * 初始化MVP模式的Presenter
     * 传入Context用于权限检查，传入this作为View接口
     */
    private fun setupPresenter() {
        presenter = NotificationPermissionPresenter(requireContext(), this)
    }
    
    /**
     * 设置系统权限请求结果监听器
     * 当用户在系统对话框中选择允许/拒绝后会回调此方法
     */
    private fun setupPermissionLauncher() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            Log.d(TAG, "Permission result: $isGranted")
            presenter.onSystemPermissionResult(isGranted)
        }
    }
    
    /**
     * 显示权限已授予的UI状态
     * 按钮变为不可点击的灰色状态，显示已启用文本
     */
    fun showPermissionGrantedUI() {
        permissionButton.apply {
            text = getString(R.string.notifications_enabled)
            isEnabled = false
            alpha = 0.6f
        }
    }
    
    /**
     * 显示权限被拒绝的UI状态
     * 根据拒绝次数显示不同的提示文本
     * @param isMultipleDenials 是否多次拒绝，决定显示的文本内容
     */
    fun showPermissionDeniedUI(isMultipleDenials: Boolean) {
        permissionButton.apply {
            text = if (isMultipleDenials) {
                getString(R.string.allow_notifications_required)
            } else {
                getString(R.string.tap_to_allow_notifications)
            }
            isEnabled = true
            alpha = 1.0f
        }
    }
    
    /**
     * 显示Android系统原生权限对话框
     * 仅在Android 13+系统上调用，因为只有这些版本需要通知权限
     */
    fun showSystemPermissionDialog() {
        Log.d(TAG, "Launching system permission request")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    
    
    /**
     * 打开系统通知设置页面
     * 委托给IntroActivity处理，因为Activity更适合处理Intent跳转
     */
    fun openNotificationSettings() {
        (activity as? IntroActivity)?.openNotificationSettings()
    }
    
    /**
     * 启用导航功能，允许用户完成引导流程
     * 委托给IntroActivity更新导航状态
     */
    fun enableNavigation() {
        (activity as? IntroActivity)?.enableNavigation()
    }
    
    /**
     * 禁用导航功能，阻止用户完成引导流程
     * 委托给IntroActivity更新导航状态
     */
    fun disableNavigation() {
        (activity as? IntroActivity)?.disableNavigation()
    }
}