package com.example.todolist.data.repository

import com.example.todolist.data.local.database.ProjectDao
import com.example.todolist.data.local.database.TaskDao
import com.example.todolist.data.mapper.toDomain
import com.example.todolist.data.mapper.toEntity
import com.example.todolist.domain.model.Project
import com.example.todolist.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val projectDao: ProjectDao,
    private val taskDao: TaskDao
) {
    /**
     * 获取所有项目（带任务数量）
     */
    fun getAllProjects(): Flow<List<Project>> {
        return projectDao.getAll().map { entities ->
            entities.map { entity ->
                val taskCount = try {
                    0 // 简化处理，后续可添加实时计数
                } catch (e: Exception) {
                    0
                }
                entity.toDomain(taskCount)
            }
        }
    }

    /**
     * 添加项目
     */
    suspend fun addProject(project: Project) {
        projectDao.insert(project.toEntity())
    }

    /**
     * 更新项目
     */
    suspend fun updateProject(project: Project) {
        projectDao.update(project.toEntity())
    }

    /**
     * 删除项目（任务移至收件箱）
     */
    suspend fun deleteProject(projectId: String) {
        // 清除该项目的任务关联
        taskDao.clearProjectId(projectId, System.currentTimeMillis())
        // 删除项目
        val project = projectDao.getById(projectId)
        project?.let { projectDao.delete(it) }
    }

    /**
     * 获取项目下的任务
     */
    fun getProjectTasks(projectId: String): Flow<List<Task>> {
        return taskDao.getTasksByProject(projectId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * 根据ID获取项目
     */
    suspend fun getProjectById(projectId: String): Project? {
        return projectDao.getById(projectId)?.toDomain()
    }
}