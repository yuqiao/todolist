package com.example.todolist.ui.inbox

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todolist.domain.model.Task
import com.example.todolist.ui.components.QuickAddButton
import com.example.todolist.ui.components.TaskItem
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    viewModel: InboxViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("收件箱") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            QuickAddButton(
                onClick = { viewModel.handleIntent(InboxIntent.ShowQuickAdd) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 快速添加输入框
            AnimatedVisibility(visible = state.showQuickAdd) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = state.quickAddText,
                            onValueChange = {
                                viewModel.handleIntent(InboxIntent.UpdateQuickAddText(it))
                            },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("添加新任务") },
                            singleLine = true
                        )
                        IconButton(onClick = { viewModel.handleIntent(InboxIntent.HideQuickAdd) }) {
                            Icon(Icons.Default.Close, contentDescription = "取消")
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                if (state.quickAddText.isNotBlank()) {
                                    viewModel.handleIntent(InboxIntent.AddQuickTask(state.quickAddText))
                                }
                            }
                        ) {
                            Text("添加")
                        }
                    }
                }
            }

            // 任务列表
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "收件箱为空",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.tasks, key = { it.id }) { task ->
                        InboxTaskItem(
                            task = task,
                            onCompleteClick = { completed ->
                                viewModel.handleIntent(InboxIntent.CompleteTask(task.id, completed))
                            },
                            onDateClick = {
                                viewModel.handleIntent(InboxIntent.ShowDatePicker(task.id))
                            }
                        )
                    }
                }
            }
        }

        // 日期选择器
        state.selectedTaskId?.let { taskId ->
            if (state.showDatePicker) {
                DatePickerSheet(
                    onDateSelected = { date, isToday ->
                        viewModel.handleIntent(InboxIntent.UpdateTaskDate(taskId, date, isToday))
                    },
                    onDismiss = {
                        viewModel.handleIntent(InboxIntent.HideDatePicker)
                    }
                )
            }
        }
    }
}

@Composable
private fun InboxTaskItem(
    task: Task,
    onCompleteClick: (Boolean) -> Unit,
    onDateClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 任务内容
            Column(modifier = Modifier.weight(1f)) {
                TaskItem(
                    task = task,
                    onCompleteClick = onCompleteClick,
                    onTaskClick = { }
                )
            }

            // 日期按钮
            IconButton(onClick = onDateClick) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = "设置日期",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerSheet(
    onDateSelected: (LocalDate?, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val today = LocalDate.now()
    val tomorrow = today.plusDays(1)
    val nextWeek = today.plusWeeks(1)

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = "选择日期",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // 快捷选项
            listOf(
                "今天" to Pair(today, true),
                "明天" to Pair(tomorrow, false),
                "下周" to Pair(nextWeek, false)
            ).forEach { (label, pair) ->
                TextButton(
                    onClick = { onDateSelected(pair.first, pair.second) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(label, modifier = Modifier.fillMaxWidth())
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 清除日期
            TextButton(
                onClick = { onDateSelected(null, false) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("清除日期", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}