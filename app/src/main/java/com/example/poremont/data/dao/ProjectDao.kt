package com.example.poremont.data.dao

import androidx.room.*
import com.example.poremont.data.entity.Project
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects WHERE isActive = 1 LIMIT 1")
    fun getActiveProject(): Flow<Project?>

    @Insert
    suspend fun insert(project: Project): Long

    @Update
    suspend fun update(project: Project)

    @Query("UPDATE projects SET isActive = 0 WHERE id = :id")
    suspend fun deactivateProject(id: Int)
}