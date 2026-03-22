package com.example.todolist.domain.usecase.task

import com.example.todolist.data.repository.TaskRepository
import com.example.todolist.domain.model.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodayTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<List<Task>> {
        return repository.getTodayTasks()
    }
}