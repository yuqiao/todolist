package com.example.todolist.ui.upcoming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.repository.TaskRepository
import com.example.todolist.domain.usecase.task.CompleteTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpcomingViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val completeTaskUseCase: CompleteTaskUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(UpcomingState())
    val state: StateFlow<UpcomingState> = _state.asStateFlow()

    init {
        loadTasks()
    }

    fun handleIntent(intent: UpcomingIntent) {
        when (intent) {
            is UpcomingIntent.LoadTasks -> loadTasks()
            is UpcomingIntent.CompleteTask -> completeTask(intent.taskId, intent.completed)
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            taskRepository.getUpcomingTasks().collect { tasks ->
                _state.update { it.copy(tasks = tasks, isLoading = false) }
            }
        }
    }

    private fun completeTask(taskId: String, completed: Boolean) {
        viewModelScope.launch {
            completeTaskUseCase(taskId, completed)
        }
    }
}