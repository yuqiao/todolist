# Things3 风格 Android 待办事项应用 - 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建一个 Things3 风格的 Android 待办事项应用 MVP 原型。

**Architecture:** MVI 架构，分层设计（UI -> Domain -> Data），使用 Jetpack Compose 构建界面，Room 持久化数据，Hilt 依赖注入。

**Tech Stack:** Kotlin, Jetpack Compose, Room, Hilt, Coroutines/Flow

---

## 文件结构

```
app/src/main/java/com/example/todolist/
├── di/
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
│   │       ├── TagEntity.kt
│   │       └── TaskTagCrossRef.kt
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
│   └── usecase/
│       └── task/
│           ├── AddTaskUseCase.kt
│           ├── CompleteTaskUseCase.kt
│           ├── GetTodayTasksUseCase.kt
│           └── DeleteTaskUseCase.kt
├── ui/
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   ├── components/
│   │   ├── TaskItem.kt
│   │   └── QuickAddButton.kt
│   ├── today/
│   │   ├── TodayScreen.kt
│   │   ├── TodayViewModel.kt
│   │   └── TodayState.kt
│   └── navigation/
│       └── AppNavigation.kt
├── TodolistApplication.kt
└── MainActivity.kt
```

---

## Phase 1: 项目初始化

### Task 1.1: 创建 Android 项目

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts` (project level)
- Create: `app/build.gradle.kts` (app level)

- [ ] **Step 1: 使用 Android Studio 创建新项目**
  - 选择 "Empty Activity" (Compose)
  - Name: "Todolist", Package: "com.example.todolist"
  - Language: Kotlin, Minimum SDK: API 26

- [ ] **Step 2: 配置项目级 build.gradle.kts**
```kotlin
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
}
```

- [ ] **Step 3: 配置 app/build.gradle.kts**
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.todolist"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.example.todolist"
        minSdk = 26
        targetSdk = 34
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.8" }
}

dependencies {
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("app.cash.turbine:turbine:1.0.0")
}
```

- [ ] **Step 4: 验证构建** - Run: `./gradlew assembleDebug`

- [ ] **Step 5: 提交** - `git commit -m "feat: 初始化 Android 项目"`

---

### Task 1.2: 配置 Hilt 和 Application 类

**Files:**
- Create: `app/src/main/java/com/example/todolist/TodolistApplication.kt`
- Modify: `app/src/main/java/com/example/todolist/MainActivity.kt`

- [ ] **Step 1: 创建 Application 类**
```kotlin
// TodolistApplication.kt
package com.example.todolist

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TodolistApplication : Application()
```

- [ ] **Step 2: 更新 AndroidManifest.xml** 添加 `android:name=".TodolistApplication"`

- [ ] **Step 3: 更新 MainActivity 添加 @AndroidEntryPoint**

- [ ] **Step 4: 提交** - `git commit -m "feat: 配置 Hilt 依赖注入"`

---

## Phase 2: 数据层

### Task 2.1: 创建数据实体

**Files:**
- Create: `app/src/main/java/com/example/todolist/data/local/entity/TaskEntity.kt`
- Create: `app/src/main/java/com/example/todolist/data/local/entity/ProjectEntity.kt`
- Create: `app/src/main/java/com/example/todolist/data/local/entity/TagEntity.kt`
- Create: `app/src/main/java/com/example/todolist/data/local/entity/TaskTagCrossRef.kt`

- [ ] **Step 1: 创建 TaskEntity**
```kotlin
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val notes: String? = null,
    val isCompleted: Boolean = false,
    val dueDate: Long? = null,
    val reminderTime: Long? = null,
    val isToday: Boolean = false,
    val projectId: String? = null,
    val sortOrder: Int = 0,
    val createdAt: Long,
    val updatedAt: Long
)
```

- [ ] **Step 2: 创建 ProjectEntity**
```kotlin
@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val colorHex: String = "#007AFF",
    val sortOrder: Int = 0
)
```

- [ ] **Step 3: 创建 TagEntity**
```kotlin
@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey val name: String,
    val colorHex: String = "#FF9500"
)
```

- [ ] **Step 4: 创建 TaskTagCrossRef**
```kotlin
@Entity(tableName = "task_tag_cross_ref", primaryKeys = ["taskId", "tagName"])
data class TaskTagCrossRef(val taskId: String, val tagName: String)
```

- [ ] **Step 5: 提交** - `git commit -m "feat: 添加 Room 数据实体"`

---

### Task 2.2: 创建 DAO 接口

**Files:**
- Create: `app/src/main/java/com/example/todolist/data/local/database/AppDatabase.kt`
- Create: `app/src/main/java/com/example/todolist/data/local/database/TaskDao.kt`
- Create: `app/src/main/java/com/example/todolist/data/local/database/ProjectDao.kt`
- Create: `app/src/main/java/com/example/todolist/data/local/database/TagDao.kt`
- Create: `app/src/test/java/com/example/todolist/data/local/TaskDaoTest.kt`

- [ ] **Step 1: 编写 TaskDao 测试 - test_insert_whenValidTask_shouldPersist**
```kotlin
// TaskDaoTest.kt
@RunWith(AndroidJUnit4::class)
class TaskDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        taskDao = database.taskDao()
    }

    @Test
    fun test_insert_whenValidTask_shouldPersist() = runTest {
        val task = TaskEntity(
            id = "test-1", title = "Test Task",
            createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()
        )
        taskDao.insert(task)
        val result = taskDao.getById("test-1")
        assertEquals("Test Task", result?.title)
    }

    @After
    fun teardown() { database.close() }
}
```

- [ ] **Step 2: 运行测试确认失败** (TaskDao 未定义)

- [ ] **Step 3: 创建 TaskDao**
```kotlin
@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getById(taskId: String): TaskEntity?

    @Query("SELECT * FROM tasks ORDER BY sortOrder ASC")
    fun getAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND (isToday = 1 OR dueDate = :todayEpochDay)")
    fun getTodayTasks(todayEpochDay: Long): Flow<List<TaskEntity>>

    @Query("UPDATE tasks SET isCompleted = :completed, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun updateCompleted(taskId: String, completed: Boolean, updatedAt: Long)
}
```

- [ ] **Step 4: 创建 ProjectDao, TagDao, AppDatabase**

- [ ] **Step 5: 运行测试确认通过**

- [ ] **Step 6: 提交** - `git commit -m "feat: 添加 Room DAO 接口"`

---

### Task 2.3: 配置 Hilt 数据库模块

**Files:**
- Create: `app/src/main/java/com/example/todolist/di/DatabaseModule.kt`

- [ ] **Step 1: 创建 DatabaseModule**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "todolist_database").build()
    }
}
```

- [ ] **Step 2: 提交** - `git commit -m "feat: 配置 Hilt 数据库模块"`

---

## Phase 3: 领域层

### Task 3.1: 创建领域模型

**Files:**
- Create: `app/src/main/java/com/example/todolist/domain/model/Task.kt`
- Create: `app/src/main/java/com/example/todolist/domain/model/Project.kt`
- Create: `app/src/main/java/com/example/todolist/domain/model/Tag.kt`

- [ ] **Step 1: 创建领域模型**
```kotlin
// Task.kt
data class Task(
    val id: String,
    val title: String,
    val notes: String? = null,
    val isCompleted: Boolean = false,
    val dueDate: LocalDate? = null,
    val reminderTime: LocalDateTime? = null,
    val isToday: Boolean = false,
    val project: Project? = null,
    val tags: List<Tag> = emptyList(),
    val sortOrder: Int = 0,
    val createdAt: Instant,
    val updatedAt: Instant
)
```

- [ ] **Step 2: 提交** - `git commit -m "feat: 添加领域模型"`

---

### Task 3.2: 创建实体映射器

**Files:**
- Create: `app/src/main/java/com/example/todolist/data/mapper/TaskMapper.kt`

- [ ] **Step 1: 创建 TaskMapper**
```kotlin
fun TaskEntity.toDomain(): Task { ... }
fun Task.toEntity(): TaskEntity { ... }
```

- [ ] **Step 2: 提交** - `git commit -m "feat: 添加实体映射器"`

---

### Task 3.3: 创建 Repository

**Files:**
- Create: `app/src/main/java/com/example/todolist/data/repository/TaskRepository.kt`
- Create: `app/src/main/java/com/example/todolist/di/RepositoryModule.kt`
- Create: `app/src/test/java/com/example/todolist/data/repository/TaskRepositoryTest.kt`

- [ ] **Step 1: 编写 TaskRepository 测试 - test_addTask_shouldCallDaoInsert**
```kotlin
// TaskRepositoryTest.kt
class TaskRepositoryTest {
    private lateinit var taskDao: TaskDao
    private lateinit var repository: TaskRepository

    @Before
    fun setup() {
        taskDao = mockk()
        repository = TaskRepository(taskDao)
    }

    @Test
    fun test_addTask_shouldCallDaoInsert() = runTest {
        val task = Task(id = "1", title = "Test", createdAt = Instant.now(), updatedAt = Instant.now())
        coEvery { taskDao.insert(any()) } returns Unit
        repository.addTask(task)
        coVerify { taskDao.insert(any()) }
    }
}
```

- [ ] **Step 2: 运行测试确认失败** (TaskRepository 未定义)

- [ ] **Step 3: 创建 TaskRepository**
```kotlin
@Singleton
class TaskRepository @Inject constructor(private val taskDao: TaskDao) {
    suspend fun addTask(task: Task) { taskDao.insert(task.toEntity()) }
    suspend fun completeTask(taskId: String, completed: Boolean) { ... }
    fun getTodayTasks(): Flow<List<Task>> { ... }
}
```

- [ ] **Step 4: 运行测试确认通过**

- [ ] **Step 5: 提交** - `git commit -m "feat: 添加 TaskRepository"`

---

### Task 3.4: 创建 Use Cases

**Files:**
- Create: `app/src/main/java/com/example/todolist/domain/usecase/task/AddTaskUseCase.kt`
- Create: `app/src/main/java/com/example/todolist/domain/usecase/task/CompleteTaskUseCase.kt`
- Create: `app/src/main/java/com/example/todolist/domain/usecase/task/GetTodayTasksUseCase.kt`
- Create: `app/src/main/java/com/example/todolist/domain/usecase/task/DeleteTaskUseCase.kt`
- Create: `app/src/test/java/com/example/todolist/domain/usecase/task/AddTaskUseCaseTest.kt`

- [ ] **Step 1: 编写 AddTaskUseCase 测试**
```kotlin
// AddTaskUseCaseTest.kt
class AddTaskUseCaseTest {
    private lateinit var repository: TaskRepository
    private lateinit var useCase: AddTaskUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = AddTaskUseCase(repository)
    }

    @Test
    fun test_invoke_shouldAddTask() = runTest {
        coEvery { repository.addTask(any()) } returns Unit
        useCase("Test Task")
        coVerify { repository.addTask(match { it.title == "Test Task" }) }
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

- [ ] **Step 3: 创建 Use Cases**
```kotlin
class AddTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(title: String, notes: String? = null) { ... }
}
```

- [ ] **Step 4: 运行测试确认通过**

- [ ] **Step 5: 提交** - `git commit -m "feat: 添加任务相关 Use Cases"`

---

## Phase 4: UI 层

### Task 4.1: 创建主题配置

**Files:**
- Create: `app/src/main/java/com/example/todolist/ui/theme/Color.kt`
- Create: `app/src/main/java/com/example/todolist/ui/theme/Theme.kt`
- Create: `app/src/main/java/com/example/todolist/ui/theme/Type.kt`

- [ ] **Step 1: 创建 Things3 风格配色**
```kotlin
val BackgroundLight = Color(0xFFF5F5F5)
val PrimaryLight = Color(0xFF007AFF)
val SuccessLight = Color(0xFF34C759)
```

- [ ] **Step 2: 提交** - `git commit -m "feat: 添加 Material3 主题配置"`

---

### Task 4.2: 创建通用组件

**Files:**
- Create: `app/src/main/java/com/example/todolist/ui/components/TaskItem.kt`
- Create: `app/src/main/java/com/example/todolist/ui/components/QuickAddButton.kt`

- [ ] **Step 1: 创建 TaskItem 组件**
```kotlin
// TaskItem.kt
@Composable
fun TaskItem(
    task: Task,
    onCompleteClick: (Boolean) -> Unit,
    onTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable { onTaskClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(24.dp).clip(CircleShape).clickable { onCompleteClick(!task.isCompleted) },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Surface(modifier = Modifier.size(24.dp), shape = CircleShape, color = SuccessLight) {
                        Icon(Icons.Default.Check, contentDescription = "已完成", tint = Color.White, modifier = Modifier.padding(4.dp))
                    }
                } else {
                    Surface(modifier = Modifier.size(24.dp), shape = CircleShape, color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline)) {}
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = task.title, style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None)
        }
    }
}
```

- [ ] **Step 2: 创建 QuickAddButton 组件**
```kotlin
// QuickAddButton.kt
@Composable
fun QuickAddButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onClick, modifier = modifier, shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(Icons.Default.Add, contentDescription = "添加任务")
    }
}
```

- [ ] **Step 3: 提交** - `git commit -m "feat: 添加通用 UI 组件"`

---

### Task 4.3: 创建今日视图

**Files:**
- Create: `app/src/main/java/com/example/todolist/ui/today/TodayState.kt`
- Create: `app/src/main/java/com/example/todolist/ui/today/TodayViewModel.kt`
- Create: `app/src/main/java/com/example/todolist/ui/today/TodayScreen.kt`
- Create: `app/src/test/java/com/example/todolist/ui/today/TodayViewModelTest.kt`

- [ ] **Step 1: 创建 TodayState**
```kotlin
// TodayState.kt
data class TodayState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val showQuickAdd: Boolean = false,
    val quickAddText: String = ""
)

sealed interface TodayIntent {
    data object LoadTasks : TodayIntent
    data class CompleteTask(val taskId: String, val completed: Boolean) : TodayIntent
    data class AddQuickTask(val title: String) : TodayIntent
    data object ShowQuickAdd : TodayIntent
    data object HideQuickAdd : TodayIntent
}
```

- [ ] **Step 2: 编写 TodayViewModel 测试**
```kotlin
// TodayViewModelTest.kt
class TodayViewModelTest {
    private lateinit var getTodayTasksUseCase: GetTodayTasksUseCase
    private lateinit var addTaskUseCase: AddTaskUseCase
    private lateinit var viewModel: TodayViewModel

    @Before
    fun setup() {
        getTodayTasksUseCase = mockk()
        addTaskUseCase = mockk()
        every { getTodayTasksUseCase() } returns flowOf(emptyList())
        viewModel = TodayViewModel(getTodayTasksUseCase, addTaskUseCase, mockk(), mockk())
    }

    @Test
    fun test_showQuickAdd_shouldUpdateState() = runTest {
        viewModel.handleIntent(TodayIntent.ShowQuickAdd)
        viewModel.state.test { assertTrue(awaitItem().showQuickAdd) }
    }
}
```

- [ ] **Step 3: 创建 TodayViewModel** (MVI 模式)

- [ ] **Step 4: 创建 TodayScreen** (Compose UI)

- [ ] **Step 5: 提交** - `git commit -m "feat: 添加今日视图"`

---

### Task 4.4: 创建导航配置

**Files:**
- Create: `app/src/main/java/com/example/todolist/ui/navigation/AppNavigation.kt`
- Modify: `app/src/main/java/com/example/todolist/MainActivity.kt`

- [ ] **Step 1: 创建底部导航** (今日/收件箱/项目/标签)

- [ ] **Step 2: 更新 MainActivity**

- [ ] **Step 3: 提交** - `git commit -m "feat: 添加底部导航"`

---

## Phase 5: 验证

### Task 5.1: 验证项目完整性

- [ ] **Step 1: 运行所有测试** - `./gradlew test`

- [ ] **Step 2: 构建 Release 版本** - `./gradlew assembleRelease`

- [ ] **Step 3: 手动验证核心功能**
  - 应用启动正常
  - 快速添加任务
  - 任务完成切换
  - 底部导航切换

- [ ] **Step 4: 最终提交** - `git commit -m "feat: MVP 完成"`

---

## 验证检查清单

### 单元测试
- [ ] TaskDao CRUD 测试通过
- [ ] TaskRepository 测试通过
- [ ] TodayViewModel 测试通过

### 集成验证
- [ ] 应用构建成功
- [ ] 今日任务显示正确
- [ ] 任务创建流程正常
- [ ] 任务完成状态切换正常