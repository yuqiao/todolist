package com.example.todolist.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "task_tag_cross_ref",
    primaryKeys = ["taskId", "tagName"]
)
data class TaskTagCrossRef(
    val taskId: String,
    val tagName: String
)