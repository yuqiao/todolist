package com.example.todolist.ui.upcoming

import com.example.todolist.domain.model.Task

data class UpcomingState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false
)

sealed interface UpcomingIntent {
    data object LoadTasks : UpcomingIntent
    data class CompleteTask(val taskId: String, val completed: Boolean) : UpcomingIntent
}