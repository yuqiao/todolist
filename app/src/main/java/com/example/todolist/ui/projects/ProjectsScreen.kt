package com.example.todolist.ui.projects

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.todolist.domain.model.Project
import com.example.todolist.domain.model.Task
import com.example.todolist.ui.components.QuickAddButton
import com.example.todolist.ui.components.TaskItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    navController: NavController,
    viewModel: ProjectsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("项目") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            QuickAddButton(
                onClick = { viewModel.handleIntent(ProjectsIntent.ShowAddDialog) }
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
        } else if (state.projects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无项目",
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
                items(state.projects, key = { it.id }) { project ->
                    val isExpanded = state.expandedProjectId == project.id
                    val tasks = state.projectTasks[project.id] ?: emptyList()

                    ProjectItem(
                        project = project,
                        isExpanded = isExpanded,
                        tasks = tasks,
                        onToggleExpand = {
                            viewModel.handleIntent(ProjectsIntent.ToggleExpand(project.id))
                        },
                        onClick = {
                            navController.navigate("projects/${project.id}")
                        },
                        onEdit = {
                            viewModel.handleIntent(ProjectsIntent.EditProject(project))
                        },
                        onDelete = {
                            viewModel.handleIntent(ProjectsIntent.DeleteProject(project.id))
                        },
                        onCompleteTask = { taskId, completed ->
                            // 任务完成操作在项目详情页处理
                        },
                        onTaskClick = {
                            // 任务点击操作在项目详情页处理
                        }
                    )
                }
            }
        }

        // 添加项目对话框
        if (state.showAddDialog) {
            AddEditProjectDialog(
                title = "新建项目",
                name = state.newProjectName,
                color = state.selectedColor,
                onNameChange = { viewModel.handleIntent(ProjectsIntent.UpdateName(it)) },
                onColorChange = { viewModel.handleIntent(ProjectsIntent.UpdateColor(it)) },
                onDismiss = { viewModel.handleIntent(ProjectsIntent.HideAddDialog) },
                onConfirm = { viewModel.handleIntent(ProjectsIntent.SaveProject) }
            )
        }

        // 编辑项目对话框
        if (state.editingProject != null) {
            AddEditProjectDialog(
                title = "编辑项目",
                name = state.newProjectName,
                color = state.selectedColor,
                onNameChange = { viewModel.handleIntent(ProjectsIntent.UpdateName(it)) },
                onColorChange = { viewModel.handleIntent(ProjectsIntent.UpdateColor(it)) },
                onDismiss = { viewModel.handleIntent(ProjectsIntent.HideEditDialog) },
                onConfirm = { viewModel.handleIntent(ProjectsIntent.SaveProject) }
            )
        }
    }
}

@Composable
private fun ProjectItem(
    project: Project,
    isExpanded: Boolean,
    tasks: List<Task>,
    onToggleExpand: () -> Unit,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCompleteTask: (String, Boolean) -> Unit,
    onTaskClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            // 项目标题行
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 展开/收起箭头
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "收起" else "展开",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(8.dp))

                // 颜色圆点
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(project.color)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // 项目名称
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )

                // 任务数量
                if (project.taskCount > 0) {
                    Text(
                        text = "${project.taskCount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // 更多操作
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Text("⋯", style = MaterialTheme.typography.titleLarge)
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("查看详情") },
                            onClick = {
                                showMenu = false
                                onClick()
                            },
                            leadingIcon = { Icon(Icons.Default.ExpandMore, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("编辑") },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("删除") },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.error
                            )
                        )
                    }
                }
            }

            // 展开时显示任务列表
            AnimatedVisibility(visible = isExpanded && tasks.isNotEmpty()) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                ) {
                    tasks.forEach { task ->
                        TaskItem(
                            task = task,
                            onCompleteClick = { completed ->
                                onCompleteTask(task.id, completed)
                            },
                            onTaskClick = onTaskClick,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            // 展开但无任务时显示提示
            AnimatedVisibility(visible = isExpanded && tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 48.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = "暂无任务",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AddEditProjectDialog(
    title: String,
    name: String,
    color: Color,
    onNameChange: (String) -> Unit,
    onColorChange: (Color) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val colors = listOf(
        Color(0xFF007AFF), // 蓝色
        Color(0xFFFF9500), // 橙色
        Color(0xFF34C759), // 绿色
        Color(0xFFFF3B30), // 红色
        Color(0xFFAF52DE), // 紫色
        Color(0xFFFF2D55), // 粉色
        Color(0xFF5856D6), // 靛蓝
        Color(0xFFFFCC00)  // 黄色
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                // 名称输入
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("项目名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 颜色选择
                Text(
                    text = "选择颜色",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colors.forEach { c ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(c)
                                .clickable { onColorChange(c) }
                        ) {
                            if (c == color) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = name.isNotBlank()
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}