package com.example.poremont.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poremont.data.AppDatabase
import com.example.poremont.data.entity.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val database: AppDatabase) : ViewModel() {
    private val _activeProject = MutableStateFlow<Project?>(null)
    val activeProject: StateFlow<Project?> = _activeProject.asStateFlow()

    init {
        viewModelScope.launch {
            database.projectDao().getActiveProject().collect { project ->
                _activeProject.value = project
            }
        }
    }

    fun createNewProject(name: String = "Новый ремонт") {
        viewModelScope.launch {
            val project = Project(name = name)
            database.projectDao().insert(project)
        }
    }
}