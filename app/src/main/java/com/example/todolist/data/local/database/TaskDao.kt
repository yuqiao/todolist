package com.example.todolist.data.local.database

import androidx.room.*
import com.example.todolist.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * 任务数据访问对象
 * 提供任务的 CRUD 操作
 */
@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getById(taskId: String): TaskEntity?

    @Query("SELECT * FROM tasks ORDER BY sortOrder ASC, createdAt DESC")
    fun getAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND (isToday = 1 OR dueDate = :todayEpochDay) ORDER BY sortOrder ASC")
    fun getTodayTasks(todayEpochDay: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE projectId IS NULL AND isCompleted = 0 ORDER BY sortOrder ASC, createdAt DESC")
    fun getInboxTasks(): Flow<List<TaskEntity>>

    /**
     * 获取收件箱任务（未指定日期）
     */
    @Query("SELECT * FROM tasks WHERE dueDate IS NULL AND isToday = 0 AND isCompleted = 0 ORDER BY sortOrder ASC, createdAt DESC")
    fun getInboxTasksByDate(): Flow<List<TaskEntity>>

    /**
     * 获取未来日期的任务（计划）
     */
    @Query("SELECT * FROM tasks WHERE dueDate > :todayEpochDay AND isCompleted = 0 ORDER BY dueDate ASC, sortOrder ASC")
    fun getUpcomingTasks(todayEpochDay: Long): Flow<List<TaskEntity>>

    @Query("UPDATE tasks SET isCompleted = :completed, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun updateCompleted(taskId: String, completed: Boolean, updatedAt: Long)

    /**
     * 更新任务日期
     */
    @Query("UPDATE tasks SET dueDate = :dueDate, isToday = :isToday, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun updateTaskDate(taskId: String, dueDate: Long?, isToday: Boolean, updatedAt: Long)

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 AND (isToday = 1 OR dueDate = :todayEpochDay) ORDER BY updatedAt DESC")
    fun getTodayCompletedTasks(todayEpochDay: Long): Flow<List<TaskEntity>>

    /**
     * 获取项目下的任务
     */
    @Query("SELECT * FROM tasks WHERE projectId = :projectId AND isCompleted = 0 ORDER BY sortOrder ASC")
    fun getTasksByProject(projectId: String): Flow<List<TaskEntity>>

    /**
     * 清除任务的 projectId（用于删除项目时）
     */
    @Query("UPDATE tasks SET projectId = NULL, updatedAt = :updatedAt WHERE projectId = :projectId")
    suspend fun clearProjectId(projectId: String, updatedAt: Long)
}