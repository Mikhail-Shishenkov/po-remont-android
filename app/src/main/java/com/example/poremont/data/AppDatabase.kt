package com.example.poremont.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.poremont.data.dao.*
import com.example.poremont.data.entity.*

@Database(
    entities = [Project::class, RoomEntity::class, Stage::class, ChecklistItem::class, Defect::class, Photo::class, ReferenceSection::class, ReferenceMaterial::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun roomDao(): RoomDao
    abstract fun stageDao(): StageDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun defectDao(): DefectDao
    abstract fun referenceDao(): ReferenceDao
}