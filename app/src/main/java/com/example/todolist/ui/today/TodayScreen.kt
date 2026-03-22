package com.example.todolist.ui.today

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todolist.ui.components.QuickAddButton
import com.example.todolist.ui.components.TaskItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    viewModel: TodayViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("今日") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            QuickAddButton(
                onClick = { viewModel.handleIntent(TodayIntent.ShowQuickAdd) }
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
                                viewModel.handleIntent(TodayIntent.UpdateQuickAddText(it))
                            },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("添加新任务") },
                            singleLine = true
                        )
                        IconButton(onClick = { viewModel.handleIntent(TodayIntent.HideQuickAdd) }) {
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
                                    viewModel.handleIntent(TodayIntent.AddQuickTask(state.quickAddText))
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
            } else if (state.tasks.isEmpty() && state.completedTasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "今日暂无任务",
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
                    // 未完成任务
                    items(state.tasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onCompleteClick = { completed ->
                                viewModel.handleIntent(TodayIntent.CompleteTask(task.id, completed))
                            },
                            onTaskClick = { /* TODO: 导航到任务详情 */ }
                        )
                    }

                    // 已完成任务区域
                    if (state.completedTasks.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.handleIntent(TodayIntent.ToggleCompletedExpanded)
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (state.isCompletedExpanded) {
                                        Icons.Default.KeyboardArrowUp
                                    } else {
                                        Icons.Default.KeyboardArrowDown
                                    },
                                    contentDescription = if (state.isCompletedExpanded) "收起" else "展开",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "已完成 ${state.completedTasks.size} 项",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // 已完成任务列表
                        if (state.isCompletedExpanded) {
                            items(state.completedTasks, key = { it.id }) { task ->
                                TaskItem(
                                    task = task,
                                    onCompleteClick = { completed ->
                                        viewModel.handleIntent(TodayIntent.CompleteTask(task.id, completed))
                                    },
                                    onTaskClick = { /* TODO: 导航到任务详情 */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}