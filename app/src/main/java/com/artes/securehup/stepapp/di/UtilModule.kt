package com.artes.securehup.stepapp.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UtilModule {
    // PreferencesManager ve LanguageManager @Inject constructor kullandığı için 
    // otomatik olarak provide edilecek
}
