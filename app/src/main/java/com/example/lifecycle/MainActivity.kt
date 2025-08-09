package com.example.lifecycle

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lifecycle.ui.theme.LifeCycleTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity_Lifecycle" // underscore for easier Logcat filter
        private const val KEY_MESSAGE = "message"
    }

    private var message = "Welcome"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        message = savedInstanceState?.getString(KEY_MESSAGE) ?: "Welcome"

        enableEdgeToEdge()
        setContent {
            LifeCycleTheme {
                LifecycleDemo(
                    message = message,
                    onMessageChange = { message = it }
                )
            }
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
}

@Composable
fun LifecycleDemo(
    message: String,
    onMessageChange: (String) -> Unit
) {
    var localMessage by remember { mutableStateOf(message) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Activity Lifecycle Demo", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Text(localMessage, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)

        Button(onClick = {
            localMessage = "Updated: ${System.currentTimeMillis()}"
            onMessageChange(localMessage)
        }) {
            Text("Update Message")
        }
    }
}
