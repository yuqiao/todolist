package com.example.todolist.data.mapper

import androidx.compose.ui.graphics.Color
import com.example.todolist.data.local.entity.TaskEntity
import com.example.todolist.data.local.entity.ProjectEntity
import com.example.todolist.data.local.entity.TagEntity
import com.example.todolist.domain.model.Task
import com.example.todolist.domain.model.Project
import com.example.todolist.domain.model.Tag
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * TaskEntity 转 Task 领域模型
 */
fun TaskEntity.toDomain(project: Project? = null, tags: List<Tag> = emptyList()): Task {
    return Task(
        id = id,
        title = title,
        notes = notes,
        isCompleted = isCompleted,
        dueDate = dueDate?.let { LocalDate.ofEpochDay(it) },
        reminderTime = reminderTime?.let {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
        },
        isToday = isToday,
        project = project,
        tags = tags,
        sortOrder = sortOrder,
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt)
    )
}

/**
 * Task 领域模型转 TaskEntity
 */
fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        notes = notes,
        isCompleted = isCompleted,
        dueDate = dueDate?.toEpochDay(),
        reminderTime = reminderTime?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
        isToday = isToday,
        projectId = project?.id,
        sortOrder = sortOrder,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli()
    )
}

/**
 * ProjectEntity 转 Project 领域模型
 */
fun ProjectEntity.toDomain(taskCount: Int = 0): Project {
    return Project(
        id = id,
        name = name,
        color = colorHex.hexToColor(),
        taskCount = taskCount
    )
}

/**
 * Project 领域模型转 ProjectEntity
 */
fun Project.toEntity(): ProjectEntity {
    return ProjectEntity(
        id = id,
        name = name,
        colorHex = color.toHex(),
        sortOrder = 0
    )
}

/**
 * TagEntity 转 Tag 领域模型
 */
fun TagEntity.toDomain(): Tag {
    return Tag(
        name = name,
        color = colorHex.hexToColor()
    )
}

/**
 * Tag 领域模型转 TagEntity
 */
fun Tag.toEntity(): TagEntity {
    return TagEntity(
        name = name,
        colorHex = color.toHex()
    )
}

/**
 * 十六进制颜色字符串转 Color
 */
fun String.hexToColor(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: Exception) {
        Color(0xFF007AFF)
    }
}

/**
 * Color 转十六进制字符串
 */
fun Color.toHex(): String {
    val red = (red * 255).toInt()
    val green = (green * 255).toInt()
    val blue = (blue * 255).toInt()
    return String.format("#%02X%02X%02X", red, green, blue)
}