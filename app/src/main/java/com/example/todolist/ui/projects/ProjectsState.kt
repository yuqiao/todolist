package com.example.todolist.ui.projects

import androidx.compose.ui.graphics.Color
import com.example.todolist.domain.model.Project
import com.example.todolist.domain.model.Task

data class ProjectsState(
    val projects: List<Project> = emptyList(),
    val projectTasks: Map<String, List<Task>> = emptyMap(),
    val expandedProjectId: String? = null,
    val isLoading: Boolean = false,
    val showAddDialog: Boolean = false,
    val editingProject: Project? = null,
    val newProjectName: String = "",
    val selectedColor: Color = Color(0xFF007AFF)
)

sealed interface ProjectsIntent {
    data object LoadProjects : ProjectsIntent
    data class ToggleExpand(val projectId: String) : ProjectsIntent
    data object ShowAddDialog : ProjectsIntent
    data object HideAddDialog : ProjectsIntent
    data class EditProject(val project: Project) : ProjectsIntent
    data object HideEditDialog : ProjectsIntent
    data class DeleteProject(val projectId: String) : ProjectsIntent
    data class UpdateName(val name: String) : ProjectsIntent
    data class UpdateColor(val color: Color) : ProjectsIntent
    data object SaveProject : ProjectsIntent
}