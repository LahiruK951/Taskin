package com.example.taskmanager.data

import androidx.room.TypeConverter
import com.example.taskmanager.model.Priority

class Converters {
    @TypeConverter
    fun toPriority(value: String) = enumValueOf<Priority>(value)

    @TypeConverter
    fun fromPriority(value: Priority) = value.name
}