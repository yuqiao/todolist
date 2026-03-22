package com.example.todolist.data.local.database

import androidx.room.*
import com.example.todolist.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

/**
 * 项目数据访问对象
 * 提供项目的 CRUD 操作
 */
@Dao
interface ProjectDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(project: ProjectEntity)

    @Update
    suspend fun update(project: ProjectEntity)

    @Delete
    suspend fun delete(project: ProjectEntity)

    @Query("SELECT * FROM projects ORDER BY sortOrder ASC")
    fun getAll(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :projectId")
    suspend fun getById(projectId: String): ProjectEntity?
}