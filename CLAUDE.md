# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

Things3 风格的 Android 待办事项应用，使用 Kotlin + Jetpack Compose 开发。

## 常用命令

```bash
# 编译 Debug APK
export JAVA_HOME=$(/usr/libexec/java_home -v 17) && ./gradlew assembleDebug

# 安装到设备/模拟器
~/Library/Android/sdk/platform-tools/adb install -r app/build/outputs/apk/debug/app-debug.apk

# 运行单元测试
./gradlew test

# 运行单个测试类
./gradlew test --tests "com.example.todolist.data.local.database.TaskDaoTest"

# 运行 Android 仪器测试
./gradlew connectedAndroidTest

# 清理构建缓存
./gradlew clean
```

**注意：** 此项目需要 Java 17 编译。如果系统默认 Java 版本不同，需设置 `JAVA_HOME`。

## 架构

采用 MVI + Clean Architecture 分层架构：

```
UI Layer (Compose)          → ViewModel (StateFlow)
    ↓ Intent                    ↓ State
Domain Layer                 ← Use Cases
    ↓
Data Layer (Repository) → Room DAOs → SQLite
```

### 分层职责

| 层 | 目录 | 职责 |
|---|------|------|
| UI | `ui/` | Compose 界面、ViewModel、State/Intent |
| Domain | `domain/` | 业务模型、Use Case |
| Data | `data/` | Room 实体、DAO、Repository、Mapper |
| DI | `di/` | Hilt 模块配置 |

### MVI 模式

每个功能模块遵循 MVI 模式：

- `XxxState.kt` - 不可变状态数据类 + Intent 密封接口
- `XxxViewModel.kt` - 处理 Intent，更新 StateFlow
- `XxxScreen.kt` - Compose UI，收集 State 渲染界面

## 数据模型转换

```
TaskEntity (Room) ←→ Task (Domain)
     ↑                  ↑
  TaskMapper.kt    业务逻辑使用
```

- Entity 使用基本类型（Long 存储日期）
- Domain 模型使用 java.time 类型（LocalDate, LocalDateTime）
- Mapper 提供双向转换扩展函数

## 技术栈版本

| 组件 | 版本 |
|------|------|
| Kotlin | 1.9.22 |
| Compose BOM | 2024.02.02 |
| Room | 2.6.1 |
| Hilt | 2.50 |
| targetSdk | 34 |
| minSdk | 26 |

## UI 设计规范

Things3 风格：圆角卡片、柔和配色、圆形复选框。

关键颜色定义在 `ui/theme/Color.kt`：
- 主色调：#007AFF
- 完成状态：#34C759
- 卡片圆角：12dp