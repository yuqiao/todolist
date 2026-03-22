package com.example.todolist.ui.projectdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.repository.ProjectRepository
import com.example.todolist.data.repository.TaskRepository
import com.example.todolist.domain.model.Task
import com.example.todolist.domain.usecase.task.AddTaskUseCase
import com.example.todolist.domain.usecase.task.CompleteTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository,
    private val addTaskUseCase: AddTaskUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectId: String = checkNotNull(savedStateHandle["projectId"])

    private val _state = MutableStateFlow(ProjectDetailState())
    val state: StateFlow<ProjectDetailState> = _state.asStateFlow()

    init {
        loadProject()
    }

    fun handleIntent(intent: ProjectDetailIntent) {
        when (intent) {
            is ProjectDetailIntent.LoadProject -> loadProject()
            is ProjectDetailIntent.CompleteTask -> completeTask(intent.taskId, intent.completed)
            is ProjectDetailIntent.AddQuickTask -> addQuickTask(intent.title)
            is ProjectDetailIntent.ShowQuickAdd -> showQuickAdd()
            is ProjectDetailIntent.HideQuickAdd -> hideQuickAdd()
            is ProjectDetailIntent.UpdateQuickAddText -> updateQuickAddText(intent.text)
        }
    }

    private fun loadProject() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // 获取项目信息
            val project = projectRepository.getProjectById(projectId)
            _state.update { it.copy(project = project) }

            // 获取项目任务
            projectRepository.getProjectTasks(projectId).collect { tasks ->
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
            val project = _state.value.project ?: return@launch
            val task = Task(
                id = UUID.randomUUID().toString(),
                title = title,
                notes = null,
                isCompleted = false,
                dueDate = null,
                reminderTime = null,
                isToday = false,
                project = project,
                tags = emptyList(),
                sortOrder = 0,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
            taskRepository.addTask(task)
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