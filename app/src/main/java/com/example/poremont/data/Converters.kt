package com.example.poremont.data

class Converters {
    @androidx.room.TypeConverter
    fun fromStringList(value: String?): List<String>? = value?.split(",")?.map { it.trim() }

    @androidx.room.TypeConverter
    fun toStringList(list: List<String>?): String? = list?.joinToString(",")
}