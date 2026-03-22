package com.example.todolist.ui.upcoming

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todolist.domain.model.Task
import com.example.todolist.ui.components.TaskItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpcomingScreen(
    viewModel: UpcomingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("计划") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无计划任务",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 按日期分组
                val groupedTasks = state.tasks.groupBy { task ->
                    task.dueDate ?: LocalDate.now()
                }.toSortedMap()

                groupedTasks.forEach { (date, tasks) ->
                    // 日期标题
                    item {
                        DateHeader(date = date)
                    }

                    // 该日期的任务
                    items(tasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onCompleteClick = { completed ->
                                viewModel.handleIntent(UpcomingIntent.CompleteTask(task.id, completed))
                            },
                            onTaskClick = { }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DateHeader(date: LocalDate) {
    val today = LocalDate.now()
    val daysUntil = ChronoUnit.DAYS.between(today, date).toInt()

    val displayText = when {
        daysUntil == 0 -> "今天"
        daysUntil == 1 -> "明天"
        daysUntil in 2..6 -> {
            val dayOfWeek = date.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.CHINESE)
            dayOfWeek
        }
        else -> date.format(DateTimeFormatter.ofPattern("M月d日"))
    }

    Text(
        text = displayText,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}