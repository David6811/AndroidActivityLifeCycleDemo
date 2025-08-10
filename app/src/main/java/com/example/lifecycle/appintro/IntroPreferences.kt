package com.example.lifecycle.appintro

import android.content.Context
import android.content.SharedPreferences

class IntroPreferences(context: Context) {
    
    companion object {
        private const val PREFS_NAME = "intro_prefs"
        private const val KEY_NAVIGATION_ENABLED = "navigation_enabled"
        private const val KEY_INTRO_COMPLETED = "intro_completed"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    fun isNavigationEnabled(): Boolean = prefs.getBoolean(KEY_NAVIGATION_ENABLED, false)
    
    fun setNavigationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NAVIGATION_ENABLED, enabled).apply()
    }
    
    fun isIntroCompleted(): Boolean = prefs.getBoolean(KEY_INTRO_COMPLETED, false)
    
    fun setIntroCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_INTRO_COMPLETED, completed).apply()
    }
}