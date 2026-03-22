package com.example.todolist.ui.today

import com.example.todolist.domain.model.Task

data class TodayState(
    val tasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val isCompletedExpanded: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showQuickAdd: Boolean = false,
    val quickAddText: String = ""
)

sealed interface TodayIntent {
    data object LoadTasks : TodayIntent
    data class CompleteTask(val taskId: String, val completed: Boolean) : TodayIntent
    data class DeleteTask(val task: Task) : TodayIntent
    data class AddQuickTask(val title: String) : TodayIntent
    data object ShowQuickAdd : TodayIntent
    data object HideQuickAdd : TodayIntent
    data class UpdateQuickAddText(val text: String) : TodayIntent
    data object ToggleCompletedExpanded : TodayIntent
}