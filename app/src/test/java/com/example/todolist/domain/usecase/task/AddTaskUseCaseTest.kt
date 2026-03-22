package com.example.todolist.domain.usecase.task

import com.example.todolist.data.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AddTaskUseCaseTest {

    private lateinit var repository: TaskRepository
    private lateinit var useCase: AddTaskUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = AddTaskUseCase(repository)
    }

    @Test
    fun test_invoke_shouldAddTask() = runTest {
        // Given
        coEvery { repository.addTask(any()) } returns Unit

        // When
        useCase("Test Task")

        // Then
        coVerify { repository.addTask(match { it.title == "Test Task" }) }
    }
}