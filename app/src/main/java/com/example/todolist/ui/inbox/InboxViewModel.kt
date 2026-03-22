package com.example.todolist.ui.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.repository.TaskRepository
import com.example.todolist.domain.usecase.task.AddTaskUseCase
import com.example.todolist.domain.usecase.task.CompleteTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val addTaskUseCase: AddTaskUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(InboxState())
    val state: StateFlow<InboxState> = _state.asStateFlow()

    init {
        loadTasks()
    }

    fun handleIntent(intent: InboxIntent) {
        when (intent) {
            is InboxIntent.LoadTasks -> loadTasks()
            is InboxIntent.CompleteTask -> completeTask(intent.taskId, intent.completed)
            is InboxIntent.AddQuickTask -> addQuickTask(intent.title)
            is InboxIntent.ShowDatePicker -> showDatePicker(intent.taskId)
            is InboxIntent.HideDatePicker -> hideDatePicker()
            is InboxIntent.UpdateTaskDate -> updateTaskDate(intent.taskId, intent.date, intent.isToday)
            is InboxIntent.ShowQuickAdd -> showQuickAdd()
            is InboxIntent.HideQuickAdd -> hideQuickAdd()
            is InboxIntent.UpdateQuickAddText -> updateQuickAddText(intent.text)
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            taskRepository.getInboxTasks().collect { tasks ->
                _state.update { it.copy(tasks = tasks, isLoading = false) }
            }
        }
    }

    private fun completeTask(taskId: String, completed: Boolean) {
        viewModelScope.launch {
            completeTaskUseCase(taskId, completed)
        }
    }

    private fun addQuickTask(title: String) {
        viewModelScope.launch {
            addTaskUseCase(title, isToday = false)
            _state.update { it.copy(showQuickAdd = false, quickAddText = "") }
        }
    }

    private fun showDatePicker(taskId: String) {
        _state.update { it.copy(showDatePicker = true, selectedTaskId = taskId) }
    }

    private fun hideDatePicker() {
        _state.update { it.copy(showDatePicker = false, selectedTaskId = null) }
    }

    private fun updateTaskDate(taskId: String, date: java.time.LocalDate?, isToday: Boolean) {
        viewModelScope.launch {
            taskRepository.updateTaskDate(taskId, date, isToday)
            hideDatePicker()
        }
    }

    private fun showQuickAdd() {
        _state.update { it.copy(showQuickAdd = true) }
    }

    private fun hideQuickAdd() {
        _state.update { it.copy(showQuickAdd = false, quickAddText = "") }
    }

    private fun updateQuickAddText(text: String) {
        _state.update { it.copy(quickAddText = text) }
    }
}