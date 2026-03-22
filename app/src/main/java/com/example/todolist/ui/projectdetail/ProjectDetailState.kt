package com.example.todolist.ui.projectdetail

import com.example.todolist.domain.model.Project
import com.example.todolist.domain.model.Task

data class ProjectDetailState(
    val project: Project? = null,
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val showQuickAdd: Boolean = false,
    val quickAddText: String = ""
)

sealed interface ProjectDetailIntent {
    data class LoadProject(val projectId: String) : ProjectDetailIntent
    data class CompleteTask(val taskId: String, val completed: Boolean) : ProjectDetailIntent
    data class AddQuickTask(val title: String) : ProjectDetailIntent
    data object ShowQuickAdd : ProjectDetailIntent
    data object HideQuickAdd : ProjectDetailIntent
    data class UpdateQuickAddText(val text: String) : ProjectDetailIntent
}