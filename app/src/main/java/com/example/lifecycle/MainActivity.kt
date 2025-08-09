package com.example.lifecycle

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat

class MainActivity : Activity() {

    companion object {
        private const val TAG = "MainActivity_Lifecycle" // underscore for easier Logcat filter
        private const val KEY_MESSAGE = "message"
        private const val CHANNEL_ID = "lifecycle_notifications"
        private const val NOTIFICATION_ID = 1
    }

    private var message = "Welcome"

    private lateinit var messageTextView: TextView
    private lateinit var updateButton: Button
    private lateinit var notificationButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        setContentView(R.layout.activity_main)

        message = savedInstanceState?.getString(KEY_MESSAGE) ?: "Welcome"

        messageTextView = findViewById(R.id.messageTextView)
        updateButton = findViewById(R.id.updateButton)
        notificationButton = findViewById(R.id.notificationButton)

        messageTextView.text = message
        
        createNotificationChannel()

        updateButton.setOnClickListener {
            message = "Updated: ${System.currentTimeMillis()}"
            messageTextView.text = message
        }
        
        notificationButton.setOnClickListener {
            showNotification()
        }
    }

    override fun onStart() { super.onStart(); Log.d(TAG, "onStart") }
    override fun onResume() { super.onResume(); Log.d(TAG, "onResume") }
    override fun onPause() { super.onPause(); Log.d(TAG, "onPause") }
    override fun onStop() { super.onStop(); Log.d(TAG, "onStop") }
    override fun onRestart() { super.onRestart(); Log.d(TAG, "onRestart") }
    override fun onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy") }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_MESSAGE, message)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Lifecycle Notifications"
            val descriptionText = "Notifications from the Lifecycle Demo app"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun showNotification() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Lifecycle Demo")
            .setContentText("Notification from MainActivity!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_dialog_email,
                "Open App",
                pendingIntent
            )
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}

