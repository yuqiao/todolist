package com.example.todolist.data.local.database

import androidx.room.*
import com.example.todolist.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow

/**
 * 标签数据访问对象
 * 提供标签的 CRUD 操作
 */
@Dao
interface TagDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tag: TagEntity)

    @Delete
    suspend fun delete(tag: TagEntity)

    @Update
    suspend fun update(tag: TagEntity)

    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAll(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE name = :tagName")
    suspend fun getByName(tagName: String): TagEntity?
}