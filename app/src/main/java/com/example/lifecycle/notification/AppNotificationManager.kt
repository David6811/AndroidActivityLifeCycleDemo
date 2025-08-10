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
    
    init {
        createNotificationChannel()
    }
    
    // =========================== Channel Management ===========================
    
    /**
     * Creates notification channel for Android 8.0+ (API level 26)
     * Required for displaying notifications on newer Android versions
     */
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
    
    // =========================== Notification Methods ===========================
    
    /**
     * Shows a basic lifecycle demo notification
     * @param title The notification title
     * @param content The notification content text
     * @param targetActivity The activity to open when notification is tapped
     */
    fun showNotification(
        title: String = "Lifecycle Demo",
        content: String = "Notification from MainActivity!",
        targetActivity: Class<*> = MainActivity::class.java
    ) {
        val intent = createNotificationIntent(targetActivity)
        val pendingIntent = createPendingIntent(intent)
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_dialog_email,
                "Open App",
                pendingIntent
            )
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    /**
     * Shows a simple notification with just title and content
     */
    fun showSimpleNotification(title: String, content: String) {
        showNotification(title, content)
    }
    
    /**
     * Cancels the current notification
     */
    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }
    
    // =========================== Permission Check Methods ===========================
    
    /**
     * Checks if notification permissions are enabled
     * @return true if notifications are enabled, false otherwise
     */
    fun areNotificationsEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires explicit permission
            ContextCompat.checkSelfPermission(context, NOTIFICATION_PERMISSION) == PackageManager.PERMISSION_GRANTED &&
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0+ check notification settings
            NotificationManagerCompat.from(context).areNotificationsEnabled() &&
            isChannelEnabled()
        } else {
            // Pre-Android 8.0 check general notification setting
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }
    
    /**
     * Checks if the notification channel is enabled (Android 8.0+)
     */
    private fun isChannelEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = notificationManager.getNotificationChannel(CHANNEL_ID)
            channel?.importance != NotificationManager.IMPORTANCE_NONE
        } else {
            true
        }
    }
    
    /**
     * Opens the app's notification settings page
     */
    fun openNotificationSettings() {
        val intent = createNotificationSettingsIntent()
        context.startActivity(intent)
    }
    
    /**
     * Creates intent to open notification settings for this app
     */
    private fun createNotificationSettingsIntent(): Intent {
        return Intent().apply {
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
    }
    
    // =========================== Helper Methods ===========================
    
    private fun createNotificationIntent(targetActivity: Class<*>): Intent {
        return Intent(context, targetActivity).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }
    
    private fun createPendingIntent(intent: Intent): PendingIntent {
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}