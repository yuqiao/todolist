package com.example.todolist.domain.usecase.task

import com.example.todolist.data.repository.TaskRepository
import com.example.todolist.domain.model.Task
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        repository.deleteTask(task)
    }
}