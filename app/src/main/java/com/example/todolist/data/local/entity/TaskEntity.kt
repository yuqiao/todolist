package com.example.todolist.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("projectId")]
)
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val notes: String? = null,
    val isCompleted: Boolean = false,
    val dueDate: Long? = null,           // epoch day
    val reminderTime: Long? = null,      // epoch millis
    val isToday: Boolean = false,        // 手动加入今日
    val projectId: String? = null,
    val sortOrder: Int = 0,
    val createdAt: Long,
    val updatedAt: Long
)