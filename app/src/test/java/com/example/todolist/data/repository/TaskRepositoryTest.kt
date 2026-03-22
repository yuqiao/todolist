package com.example.todolist.data.repository

import com.example.todolist.data.local.database.TaskDao
import com.example.todolist.data.local.entity.TaskEntity
import com.example.todolist.domain.model.Task
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals

class TaskRepositoryTest {

    private lateinit var taskDao: TaskDao
    private lateinit var repository: TaskRepository

    @Before
    fun setup() {
        taskDao = mockk()
        repository = TaskRepository(taskDao)
    }

    @Test
    fun test_addTask_whenValidInput_shouldCallDaoInsert() = runTest {
        // Given
        val task = Task(
            id = "task-1",
            title = "测试任务",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        coEvery { taskDao.insert(any()) } returns Unit

        // When
        repository.addTask(task)

        // Then
        coVerify { taskDao.insert(match { it.title == "测试任务" }) }
    }

    @Test
    fun test_getTodayTasks_shouldReturnFlow() = runTest {
        // Given
        val today = java.time.LocalDate.now().toEpochDay()
        every { taskDao.getTodayTasks(today) } returns flowOf(emptyList())

        // When
        val result = repository.getTodayTasks()

        // Then
        result.collect { tasks ->
            assertEquals(emptyList(), tasks)
        }
    }
}