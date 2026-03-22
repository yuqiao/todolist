package com.example.todolist.ui.projects

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.repository.ProjectRepository
import com.example.todolist.domain.model.Project
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProjectsState())
    val state: StateFlow<ProjectsState> = _state.asStateFlow()

    init {
        loadProjects()
    }

    fun handleIntent(intent: ProjectsIntent) {
        when (intent) {
            is ProjectsIntent.LoadProjects -> loadProjects()
            is ProjectsIntent.ShowAddDialog -> showAddDialog()
            is ProjectsIntent.HideAddDialog -> hideAddDialog()
            is ProjectsIntent.EditProject -> editProject(intent.project)
            is ProjectsIntent.HideEditDialog -> hideEditDialog()
            is ProjectsIntent.DeleteProject -> deleteProject(intent.projectId)
            is ProjectsIntent.UpdateName -> updateName(intent.name)
            is ProjectsIntent.UpdateColor -> updateColor(intent.color)
            is ProjectsIntent.SaveProject -> saveProject()
        }
    }

    private fun loadProjects() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            projectRepository.getAllProjects().collect { projects ->
                _state.update { it.copy(projects = projects, isLoading = false) }
            }
        }
    }

    private fun showAddDialog() {
        _state.update {
            it.copy(
                showAddDialog = true,
                newProjectName = "",
                selectedColor = Color(0xFF007AFF)
            )
        }
    }

    private fun hideAddDialog() {
        _state.update { it.copy(showAddDialog = false) }
    }

    private fun editProject(project: Project) {
        _state.update {
            it.copy(
                editingProject = project,
                newProjectName = project.name,
                selectedColor = project.color
            )
        }
    }

    private fun hideEditDialog() {
        _state.update { it.copy(editingProject = null) }
    }

    private fun deleteProject(projectId: String) {
        viewModelScope.launch {
            projectRepository.deleteProject(projectId)
        }
    }

    private fun updateName(name: String) {
        _state.update { it.copy(newProjectName = name) }
    }

    private fun updateColor(color: Color) {
        _state.update { it.copy(selectedColor = color) }
    }

    private fun saveProject() {
        viewModelScope.launch {
            val name = _state.value.newProjectName.trim()
            if (name.isBlank()) return@launch

            val editingProject = _state.value.editingProject

            if (editingProject != null) {
                // 更新现有项目
                projectRepository.updateProject(
                    editingProject.copy(
                        name = name,
                        color = _state.value.selectedColor
                    )
                )
                hideEditDialog()
            } else {
                // 创建新项目
                projectRepository.addProject(
                    Project(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        color = _state.value.selectedColor
                    )
                )
                hideAddDialog()
            }
        }
    }
}