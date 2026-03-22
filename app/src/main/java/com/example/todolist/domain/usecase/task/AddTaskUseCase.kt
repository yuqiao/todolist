package com.example.todolist.domain.usecase.task

import com.example.todolist.data.repository.TaskRepository
import com.example.todolist.domain.model.Task
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(
        title: String,
        notes: String? = null,
        isToday: Boolean = false
    ) {
        val now = Instant.now()
        val task = Task(
            id = UUID.randomUUID().toString(),
            title = title,
            notes = notes,
            isToday = isToday,
            createdAt = now,
            updatedAt = now
        )
        repository.addTask(task)
    }
}