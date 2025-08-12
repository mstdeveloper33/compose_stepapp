package com.artes.securehup.stepapp.domain.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageManager @Inject constructor(
    private val preferencesManager: PreferencesManager
) {
    
    companion object {
        const val LANGUAGE_TURKISH = "tr"
        const val LANGUAGE_ENGLISH = "en"
        const val DEFAULT_LANGUAGE = LANGUAGE_TURKISH
    }
    
    fun getCurrentLanguage(): String {
        return preferencesManager.getLanguage()
    }
    
    fun setLanguage(language: String) {
        preferencesManager.setLanguage(language)
    }
    
    fun applyLanguageToContext(context: Context, language: String = getCurrentLanguage()): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }
    
    fun getAvailableLanguages(): List<Language> {
        return listOf(
            Language(LANGUAGE_TURKISH, "Türkçe"),
            Language(LANGUAGE_ENGLISH, "English")
        )
    }
    
    fun getLanguageDisplayName(languageCode: String): String {
        return when (languageCode) {
            LANGUAGE_TURKISH -> "Türkçe"
            LANGUAGE_ENGLISH -> "English"
            else -> "Türkçe"
        }
    }
}

data class Language(
    val code: String,
    val displayName: String
)
