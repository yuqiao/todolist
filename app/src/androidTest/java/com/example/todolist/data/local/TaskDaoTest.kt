package com.example.todolist.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todolist.data.local.database.AppDatabase
import com.example.todolist.data.local.database.TaskDao
import com.example.todolist.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * TaskDao 的单元测试
 * 遵循 TDD 流程：测试先行
 */
@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        taskDao = database.taskDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun test_insert_whenValidTask_shouldPersist() = runTest {
        val task = TaskEntity(
            id = "test-1",
            title = "Test Task",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        taskDao.insert(task)
        val result = taskDao.getById("test-1")
        assertEquals("Test Task", result?.title)
    }

    @Test
    fun test_getAll_whenMultipleTasks_shouldReturnAll() = runTest {
        val task1 = TaskEntity(
            id = "task-1", title = "Task 1",
            createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()
        )
        val task2 = TaskEntity(
            id = "task-2", title = "Task 2",
            createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()
        )
        taskDao.insert(task1)
        taskDao.insert(task2)
        val result = taskDao.getAll().first()
        assertEquals(2, result.size)
    }

    @Test
    fun test_delete_whenTaskExists_shouldRemove() = runTest {
        val task = TaskEntity(
            id = "test-1", title = "Test Task",
            createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()
        )
        taskDao.insert(task)
        taskDao.delete(task)
        val result = taskDao.getById("test-1")
        assertNull(result)
    }

    @Test
    fun test_getTodayTasks_whenTaskIsToday_shouldReturn() = runTest {
        val today = java.time.LocalDate.now().toEpochDay()
        val task = TaskEntity(
            id = "today-1", title = "Today Task", isToday = true,
            createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()
        )
        taskDao.insert(task)
        val result = taskDao.getTodayTasks(today).first()
        assertEquals(1, result.size)
        assertEquals("Today Task", result[0].title)
    }
}