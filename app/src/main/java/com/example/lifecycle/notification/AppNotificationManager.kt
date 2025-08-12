package com.example.lifecycle.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.lifecycle.activity.MainActivity

class AppNotificationManager(private val context: Context) {
    
    companion object {
        private const val CHANNEL_ID = "lifecycle_notifications"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_NAME = "Lifecycle Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications from the Lifecycle Demo app"
        private const val NOTIFICATION_PERMISSION = "android.permission.POST_NOTIFICATIONS"
    }
    
    private val notificationManager: NotificationManager = 
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    private val activity: AppCompatActivity? = context as? AppCompatActivity
    private var permissionLauncher: ActivityResultLauncher<String>? = null
    
    init {
        createNotificationChannel()
        setupPermissionLauncher()
    }
    
    // =========================== Setup ===========================
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun setupPermissionLauncher() {
        activity?.let { act ->
            permissionLauncher = act.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                if (granted) {
                    sendNotification()
                    Toast.makeText(context, "通知已发送！", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "需要通知权限才能发送提醒", Toast.LENGTH_SHORT).show()
                    openNotificationSettings()
                }
            }
        }
    }
    
    // =========================== Public Methods ===========================
    
    fun showNotification() {
        if (hasNotificationPermission()) {
            sendNotification()
            Toast.makeText(context, "通知已发送！", Toast.LENGTH_SHORT).show()
        } else {
            requestNotificationPermission()
        }
    }
    
    fun openNotificationSettings() {
        val intent = Intent().apply {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                else -> {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.parse("package:${context.packageName}")
                }
            }
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
    
    // =========================== Private Methods ===========================
    
    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, NOTIFICATION_PERMISSION) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }
    
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher?.launch(NOTIFICATION_PERMISSION)
        } else {
            Toast.makeText(context, "请在设置中开启通知权限", Toast.LENGTH_LONG).show()
            openNotificationSettings()
        }
    }
    
    private fun sendNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Lifecycle Demo")
            .setContentText("来自MainActivity的通知!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}