package com.example.todolist.data.repository

import com.example.todolist.data.local.database.TaskDao
import com.example.todolist.data.mapper.toDomain
import com.example.todolist.data.mapper.toEntity
import com.example.todolist.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 任务仓库
 * 负责协调数据源和领域模型之间的数据转换
 */
@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    /**
     * 添加新任务
     */
    suspend fun addTask(task: Task) {
        taskDao.insert(task.toEntity())
    }

    /**
     * 更新任务
     */
    suspend fun updateTask(task: Task) {
        taskDao.update(task.toEntity())
    }

    /**
     * 删除任务
     */
    suspend fun deleteTask(task: Task) {
        taskDao.delete(task.toEntity())
    }

    /**
     * 更新任务完成状态
     */
    suspend fun completeTask(taskId: String, completed: Boolean) {
        taskDao.updateCompleted(
            taskId = taskId,
            completed = completed,
            updatedAt = System.currentTimeMillis()
        )
    }

    /**
     * 获取今日任务
     */
    fun getTodayTasks(): Flow<List<Task>> {
        val today = LocalDate.now().toEpochDay()
        return taskDao.getTodayTasks(today).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * 获取收件箱任务
     */
    fun getInboxTasks(): Flow<List<Task>> {
        return taskDao.getInboxTasksByDate().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * 获取所有任务
     */
    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * 获取今日已完成任务
     */
    fun getTodayCompletedTasks(): Flow<List<Task>> {
        val today = LocalDate.now().toEpochDay()
        return taskDao.getTodayCompletedTasks(today).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * 获取未来日期的任务（计划）
     */
    fun getUpcomingTasks(): Flow<List<Task>> {
        val today = LocalDate.now().toEpochDay()
        return taskDao.getUpcomingTasks(today).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * 更新任务日期
     */
    suspend fun updateTaskDate(taskId: String, dueDate: LocalDate?, isToday: Boolean) {
        taskDao.updateTaskDate(
            taskId = taskId,
            dueDate = dueDate?.toEpochDay(),
            isToday = isToday,
            updatedAt = System.currentTimeMillis()
        )
    }
}