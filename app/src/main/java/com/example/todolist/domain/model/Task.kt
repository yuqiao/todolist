package com.example.todolist.domain.model

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

data class Task(
    val id: String,
    val title: String,
    val notes: String? = null,
    val isCompleted: Boolean = false,
    val dueDate: LocalDate? = null,
    val reminderTime: LocalDateTime? = null,
    val isToday: Boolean = false,
    val project: Project? = null,
    val tags: List<Tag> = emptyList(),
    val sortOrder: Int = 0,
    val createdAt: Instant,
    val updatedAt: Instant
)