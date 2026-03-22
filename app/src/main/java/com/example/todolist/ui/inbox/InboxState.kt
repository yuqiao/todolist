package com.example.todolist.ui.inbox

import com.example.todolist.domain.model.Task
import java.time.LocalDate

data class InboxState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val showQuickAdd: Boolean = false,
    val quickAddText: String = "",
    val showDatePicker: Boolean = false,
    val selectedTaskId: String? = null
)

sealed interface InboxIntent {
    data object LoadTasks : InboxIntent
    data class CompleteTask(val taskId: String, val completed: Boolean) : InboxIntent
    data class AddQuickTask(val title: String) : InboxIntent
    data class ShowDatePicker(val taskId: String) : InboxIntent
    data object HideDatePicker : InboxIntent
    data class UpdateTaskDate(val taskId: String, val date: LocalDate?, val isToday: Boolean) : InboxIntent
    data object ShowQuickAdd : InboxIntent
    data object HideQuickAdd : InboxIntent
    data class UpdateQuickAddText(val text: String) : InboxIntent
}