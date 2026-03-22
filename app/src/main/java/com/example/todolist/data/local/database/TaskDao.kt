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

    @Query("UPDATE tasks SET isCompleted = :completed, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun updateCompleted(taskId: String, completed: Boolean, updatedAt: Long)

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 AND (isToday = 1 OR dueDate = :todayEpochDay) ORDER BY updatedAt DESC")
    fun getTodayCompletedTasks(todayEpochDay: Long): Flow<List<TaskEntity>>
}