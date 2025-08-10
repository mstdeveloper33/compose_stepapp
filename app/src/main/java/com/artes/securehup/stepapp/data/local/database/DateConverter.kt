package com.artes.securehup.stepapp.data.local.database

import androidx.room.TypeConverter
import java.util.Date

class DateConverter {
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
} 

/*
Burada kullanılan DateConverter sınıfı, Room veritabanında Date tipini Long'a çevirir ve geri çevirir.
Bu sayede, Room veritabanında Date tipini kullanabiliriz.
*/