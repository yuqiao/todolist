package com.example.todolist.ui.today

import app.cash.turbine.test
import com.example.todolist.domain.usecase.task.AddTaskUseCase
import com.example.todolist.domain.usecase.task.CompleteTaskUseCase
import com.example.todolist.domain.usecase.task.DeleteTaskUseCase
import com.example.todolist.domain.usecase.task.GetTodayTasksUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class TodayViewModelTest {

    private lateinit var getTodayTasksUseCase: GetTodayTasksUseCase
    private lateinit var addTaskUseCase: AddTaskUseCase
    private lateinit var completeTaskUseCase: CompleteTaskUseCase
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase
    private lateinit var viewModel: TodayViewModel

    @Before
    fun setup() {
        getTodayTasksUseCase = mockk()
        addTaskUseCase = mockk()
        completeTaskUseCase = mockk()
        deleteTaskUseCase = mockk()

        every { getTodayTasksUseCase() } returns flowOf(emptyList())
        coEvery { addTaskUseCase(any(), any(), any()) } returns Unit
        coEvery { completeTaskUseCase(any(), any()) } returns Unit

        viewModel = TodayViewModel(
            getTodayTasksUseCase = getTodayTasksUseCase,
            addTaskUseCase = addTaskUseCase,
            completeTaskUseCase = completeTaskUseCase,
            deleteTaskUseCase = deleteTaskUseCase
        )
    }

    @Test
    fun test_showQuickAdd_shouldUpdateState() = runTest {
        viewModel.handleIntent(TodayIntent.ShowQuickAdd)
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.showQuickAdd)
        }
    }
}