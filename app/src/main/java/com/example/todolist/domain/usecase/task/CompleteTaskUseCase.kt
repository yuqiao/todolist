package com.example.todolist.domain.usecase.task

import com.example.todolist.data.repository.TaskRepository
import javax.inject.Inject

class CompleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: String, completed: Boolean = true) {
        repository.completeTask(taskId, completed)
    }
}