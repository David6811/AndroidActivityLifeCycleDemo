package com.example.lifecycle.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.lifecycle.R
import com.example.lifecycle.notification.AppNotificationManager

class MainActivity : Activity() {

    companion object {
        private const val TAG = "MainActivity_Lifecycle"
        private const val KEY_MESSAGE = "message"
    }

    private var message = "Welcome"

    private lateinit var messageTextView: TextView
    private lateinit var updateButton: Button
    private lateinit var notificationButton: Button
    private lateinit var notificationManager: AppNotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        setContentView(R.layout.activity_main)

        message = savedInstanceState?.getString(KEY_MESSAGE) ?: "Welcome"

        messageTextView = findViewById(R.id.messageTextView)
        updateButton = findViewById(R.id.updateButton)
        notificationButton = findViewById(R.id.notificationButton)

        messageTextView.text = message
        
        // Initialize notification manager
        notificationManager = AppNotificationManager(this)

        updateButton.setOnClickListener {
            message = "Updated: ${System.currentTimeMillis()}"
            messageTextView.text = message
        }
        
        notificationButton.setOnClickListener {
            notificationManager.showNotification()
        }
    }

    override fun onStart() { super.onStart(); Log.d(TAG, "onStart") }
    
    override fun onResume() { 
        super.onResume()
        Log.d(TAG, "onResume")
        if (!notificationManager.areNotificationsEnabled()) {
            notificationManager.openNotificationSettings()
        }
    }
    
    override fun onPause() { super.onPause(); Log.d(TAG, "onPause") }
    override fun onStop() { super.onStop(); Log.d(TAG, "onStop") }
    override fun onRestart() { super.onRestart(); Log.d(TAG, "onRestart") }
    override fun onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy") }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_MESSAGE, message)
    }
}

