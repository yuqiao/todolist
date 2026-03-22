package com.example.todolist

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * 应用程序入口类，用于配置 Hilt 依赖注入框架。
 * 所有的依赖注入容器会在应用启动时初始化。
 */
@HiltAndroidApp
class TodolistApplication : Application()