package com.example.taskmanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Priority {
    LOW, MEDIUM, HIGH
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var title: String,
    var description: String = "", // Added for task details
    var dueDate: String,
    var priority: Priority,
    var isCompleted: Boolean = false
)
