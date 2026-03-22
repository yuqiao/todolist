# Things3 风格 Android 待办事项应用 - 设计文档

## 上下文

**背景：** 参考 iOS 应用 Things3 开发一个 Android 版本的待办事项应用。

**目标：** MVP 原型，验证核心功能和设计理念。

**技术约束：** 使用 Kotlin 语言，MVI + Compose 架构，Room 数据库。

---

## 技术栈

| 类别 | 技术选型 |
|------|----------|
| 语言 | Kotlin |
| UI 框架 | Jetpack Compose |
| 架构模式 | MVI (Model-View-Intent) |
| 数据库 | Room |
| 依赖注入 | Hilt |
| 导航 | Compose Navigation |
| 异步处理 | Kotlin Flow / Coroutines |
| 日期处理 | java.time (LocalDate, LocalDateTime) |

---

## 架构设计

### 分层架构

```
┌─────────────────────────────────────────────────────────┐
│                    UI Layer (Compose)                    │
│                                                          │
│   Screen(s) → Intent → ViewModel → State → UI Render    │
│                                                          │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────┴───────────────────────────────┐
│                    Domain Layer                          │
│                                                          │
│   Use Cases ← Entities ← Repository Interface           │
│                                                          │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────┴───────────────────────────────┐
│                    Data Layer                            │
│                                                          │
│   Repository Impl ← Room DAOs ← Entities                │
│                   ← AlarmManager (Reminders)             │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

### 目录结构

```
app/src/main/java/com/example/todolist/
├── di/                          # Hilt 依赖注入模块
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
├── data/
│   ├── local/
│   │   ├── database/
│   │   │   ├── AppDatabase.kt
│   │   │   ├── TaskDao.kt
│   │   │   ├── ProjectDao.kt
│   │   │   └── TagDao.kt
│   │   └── entity/
│   │       ├── TaskEntity.kt
│   │       ├── ProjectEntity.kt
│   │       └── TagEntity.kt
│   ├── repository/
│   │   ├── TaskRepository.kt
│   │   ├── ProjectRepository.kt
│   │   └── TagRepository.kt
│   └── mapper/
│       └── TaskMapper.kt
├── domain/
│   ├── model/
│   │   ├── Task.kt
│   │   ├── Project.kt
│   │   └── Tag.kt
│   ├── usecase/
│   │   ├── task/
│   │   │   ├── AddTaskUseCase.kt
│   │   │   ├── UpdateTaskUseCase.kt
│   │   │   ├── DeleteTaskUseCase.kt
│   │   │   ├── CompleteTaskUseCase.kt
│   │   │   └── GetTodayTasksUseCase.kt
│   │   ├── project/
│   │   │   ├── AddProjectUseCase.kt
│   │   │   └── GetProjectsWithTasksUseCase.kt
│   │   └── tag/
│   │       ├── AddTagUseCase.kt
│   │       └── GetTasksByTagUseCase.kt
│   └── reminder/
│       └── ScheduleReminderUseCase.kt
├── ui/
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   ├── components/
│   │   ├── TaskItem.kt
│   │   ├── QuickAddButton.kt
│   │   ├── SwipeableTaskItem.kt
│   │   └── DatePickerDialog.kt
│   ├── today/
│   │   ├── TodayScreen.kt
│   │   ├── TodayViewModel.kt
│   │   └── TodayState.kt
│   ├── inbox/
│   │   ├── InboxScreen.kt
│   │   ├── InboxViewModel.kt
│   │   └── InboxState.kt
│   ├── projects/
│   │   ├── ProjectsScreen.kt
│   │   ├── ProjectsViewModel.kt
│   │   └── ProjectsState.kt
│   ├── tags/
│   │   ├── TagsScreen.kt
│   │   ├── TagsViewModel.kt
│   │   └── TagsState.kt
│   └── navigation/
│       └── AppNavigation.kt
└── MainActivity.kt
```

---

## 数据模型设计

### Room 实体

```kotlin
// TaskEntity.kt
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val notes: String? = null,
    val isCompleted: Boolean = false,
    val dueDate: Long? = null,           // epoch day
    val reminderTime: Long? = null,      // epoch millis
    val isToday: Boolean = false,        // 手动加入今日
    val projectId: String? = null,
    val sortOrder: Int = 0,
    val createdAt: Long,
    val updatedAt: Long
)

// TaskTagCrossRef.kt (多对多关系)
@Entity(primaryKeys = ["taskId", "tagName"])
data class TaskTagCrossRef(
    val taskId: String,
    val tagName: String
)

// ProjectEntity.kt
@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val colorHex: String = "#007AFF",
    val sortOrder: Int = 0
)

// TagEntity.kt
@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey val name: String,
    val colorHex: String = "#FF9500"
)
```

### 领域模型

```kotlin
// Task.kt
data class Task(
    val id: String,
    val title: String,
    val notes: String?,
    val isCompleted: Boolean,
    val dueDate: LocalDate?,
    val reminderTime: LocalDateTime?,
    val isToday: Boolean,
    val project: Project?,
    val tags: List<Tag>,
    val sortOrder: Int,
    val createdAt: Instant,
    val updatedAt: Instant
)

// Project.kt
data class Project(
    val id: String,
    val name: String,
    val color: Color,
    val taskCount: Int
)

// Tag.kt
data class Tag(
    val name: String,
    val color: Color
)
```

---

## 核心功能模块

### 1. 基础任务管理

**功能：**
- 创建任务：标题 + 可选备注 + 可选截止日期 + 可选项目
- 编辑任务：修改任意字段
- 删除任务：左滑删除，带撤销选项
- 完成任务：点击复选框或右滑完成
- 快速添加：底部悬浮按钮，快速输入标题创建任务

### 2. 今日视图

**显示逻辑：**
- `dueDate == today` 且未完成的任务
- `isToday == true` 且未完成的任务
- 过期未完成的任务（可选显示）

### 3. 项目管理

**功能：**
- 创建项目：名称 + 颜色选择
- 编辑项目：修改名称和颜色
- 删除项目：项目内任务移至收件箱

### 4. 标签系统

**功能：**
- 创建标签：名称 + 颜色
- 任务打标签：支持多标签
- 按标签筛选：显示所有该标签的任务

### 5. 提醒功能

**实现：**
- 使用 `AlarmManager` 设置本地提醒
- 提醒时间到达时发送系统通知

---

## UI 设计规范

### 配色方案

| 元素 | 浅色模式 | 深色模式 |
|------|----------|----------|
| 背景 | #F5F5F5 | #1C1C1E |
| 卡片背景 | #FFFFFF | #2C2C2E |
| 主色调 | #007AFF | #0A84FF |
| 完成状态 | #34C759 | #30D158 |
| 删除状态 | #FF3B30 | #FF453A |
| 主要文字 | #333333 | #FFFFFF |
| 次要文字 | #8E8E93 | #8E8E93 |

### 组件样式

**任务项卡片：**
- 圆角：12dp
- 内边距：16dp
- 复选框：圆形，直径 24dp

**底部导航：**
- 高度：56dp
- 图标 + 文字标签

---

## 验证计划

### 单元测试

- TaskDao 的 CRUD 操作测试
- TaskRepository 的业务逻辑测试
- UseCase 的单元测试
- ViewModel 的 Intent 处理测试

### 手动验证

1. 创建任务流程
2. 今日视图显示
3. 项目管理
4. 标签筛选
5. 提醒功能