package com.example.taskmanager.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.taskmanager.data.TaskDatabase
import java.text.SimpleDateFormat
import java.util.*

class TaskReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    private val notificationHelper = NotificationHelper(context)

    override suspend fun doWork(): Result {
        val taskDao = TaskDatabase.getDatabase(applicationContext).taskDao()
        val today = dateFormat.format(Calendar.getInstance().time)

        // Get all tasks due today
        val tasks = taskDao.getTasksDueOn(today)

        tasks.forEach { task ->
            if (!task.isCompleted) {
                notificationHelper.showTaskDueNotification(task)
            }
        }

        return Result.success()
    }
}