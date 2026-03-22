package com.example.todolist.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.todolist.ui.inbox.InboxScreen
import com.example.todolist.ui.today.TodayScreen
import com.example.todolist.ui.upcoming.UpcomingScreen

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Today : Screen("today", "今日", Icons.Default.DateRange)
    data object Inbox : Screen("inbox", "收件箱", Icons.Default.Inbox)
    data object Upcoming : Screen("upcoming", "计划", Icons.Default.Event)
    data object Projects : Screen("projects", "项目", Icons.Default.Folder)
    data object Tags : Screen("tags", "标签", Icons.Default.Label)
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                listOf(
                    Screen.Today,
                    Screen.Inbox,
                    Screen.Upcoming,
                    Screen.Projects,
                    Screen.Tags
                ).forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Today.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Today.route) {
                TodayScreen()
            }
            composable(Screen.Inbox.route) {
                InboxScreen()
            }
            composable(Screen.Upcoming.route) {
                UpcomingScreen()
            }
            composable(Screen.Projects.route) {
                PlaceholderScreen(title = "项目")
            }
            composable(Screen.Tags.route) {
                PlaceholderScreen(title = "标签")
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$title - 即将推出",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}