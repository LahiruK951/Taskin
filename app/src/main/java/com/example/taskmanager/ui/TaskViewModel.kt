package com.example.taskmanager.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.TaskDatabase
import com.example.taskmanager.data.TaskRepository
import com.example.taskmanager.model.Priority
import com.example.taskmanager.model.Task
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    val tasks: LiveData<List<Task>>

    init {
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        tasks = repository.allTasks.asLiveData()
    }

    fun addTask(title: String, description: String, dueDate: String, priority: Priority) {
        viewModelScope.launch {
            val task = Task(
                title = title,
                description = description,
                dueDate = dueDate,
                priority = priority
            )
            repository.insertTask(task)
        }
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
    }

    fun updateTaskStatus(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.getTask(taskId)?.let { task ->
                repository.updateTask(task.copy(isCompleted = isCompleted))
            }
        }
    }

    fun getTaskById(taskId: Long): LiveData<Task?> {
        val taskLiveData = MutableLiveData<Task?>()
        viewModelScope.launch {
            taskLiveData.value = repository.getTask(taskId)
        }
        return taskLiveData
    }

    fun getTasksForDate(date: String): LiveData<List<Task>> {
        val tasksForDate = MutableLiveData<List<Task>>()
        viewModelScope.launch {
            tasks.value?.let { allTasks ->
                tasksForDate.value = allTasks.filter { task ->
                    task.dueDate == date
                }
            }
        }
        return tasksForDate
    }
}