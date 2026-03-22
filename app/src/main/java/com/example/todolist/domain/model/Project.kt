package com.example.todolist.domain.model

import androidx.compose.ui.graphics.Color

data class Project(
    val id: String,
    val name: String,
    val color: Color,
    val taskCount: Int = 0
)