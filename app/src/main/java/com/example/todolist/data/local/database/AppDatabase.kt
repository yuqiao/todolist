package com.example.todolist.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todolist.data.local.entity.*

/**
 * 应用数据库
 * Room 数据库的主入口
 */
@Database(
    entities = [
        TaskEntity::class,
        ProjectEntity::class,
        TagEntity::class,
        TaskTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun projectDao(): ProjectDao
    abstract fun tagDao(): TagDao
}