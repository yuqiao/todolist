package com.example.todolist.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.domain.usecase.task.AddTaskUseCase
import com.example.todolist.domain.usecase.task.CompleteTaskUseCase
import com.example.todolist.domain.usecase.task.DeleteTaskUseCase
import com.example.todolist.domain.usecase.task.GetTodayTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val getTodayTasksUseCase: GetTodayTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TodayState())
    val state: StateFlow<TodayState> = _state.asStateFlow()

    init {
        loadTasks()
    }

    fun handleIntent(intent: TodayIntent) {
        when (intent) {
            is TodayIntent.LoadTasks -> loadTasks()
            is TodayIntent.CompleteTask -> completeTask(intent.taskId, intent.completed)
            is TodayIntent.DeleteTask -> deleteTask(intent.task)
            is TodayIntent.AddQuickTask -> addQuickTask(intent.title)
            is TodayIntent.ShowQuickAdd -> showQuickAdd()
            is TodayIntent.HideQuickAdd -> hideQuickAdd()
            is TodayIntent.UpdateQuickAddText -> updateQuickAddText(intent.text)
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getTodayTasksUseCase().collect { tasks ->
                _state.update { it.copy(tasks = tasks, isLoading = false) }
            }
        }
    }

    private fun completeTask(taskId: String, completed: Boolean) {
        viewModelScope.launch {
            completeTaskUseCase(taskId, completed)
        }
    }

    private fun deleteTask(task: com.example.todolist.domain.model.Task) {
        viewModelScope.launch {
            deleteTaskUseCase(task)
        }
    }

    private fun addQuickTask(title: String) {
        viewModelScope.launch {
            addTaskUseCase(title, isToday = true)
            _state.update { it.copy(showQuickAdd = false, quickAddText = "") }
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