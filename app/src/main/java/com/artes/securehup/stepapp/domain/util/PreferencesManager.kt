package com.artes.securehup.stepapp.domain.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "step_app_prefs"
        private const val KEY_LANGUAGE = "selected_language"
        private const val KEY_IS_ONBOARDING_COMPLETED = "is_onboarding_completed"
    }
    
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun getLanguage(): String {
        return sharedPreferences.getString(KEY_LANGUAGE, LanguageManager.DEFAULT_LANGUAGE) 
            ?: LanguageManager.DEFAULT_LANGUAGE
    }
    
    fun setLanguage(language: String) {
        sharedPreferences.edit()
            .putString(KEY_LANGUAGE, language)
            .apply()
    }
    
    fun isOnboardingCompleted(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_ONBOARDING_COMPLETED, false)
    }
    
    fun setOnboardingCompleted(completed: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_ONBOARDING_COMPLETED, completed)
            .apply()
    }
}
