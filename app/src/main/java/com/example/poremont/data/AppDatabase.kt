package com.example.poremont.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.poremont.data.dao.ProjectDao
import com.example.poremont.data.entity.Project

@Database(
    entities = [Project::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
}
