package com.example.taskmanager

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.taskmanager.databinding.ActivityMainBinding
import com.example.taskmanager.util.TaskReminderWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Request notification permission
        requestNotificationPermission()

        // Schedule daily task reminder check
        scheduleTaskReminders()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }

    private fun scheduleTaskReminders() {
        val taskReminderRequest = PeriodicWorkRequestBuilder<TaskReminderWorker>(
            1, // Repeat interval
            TimeUnit.DAYS // Time unit for repeat interval
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "TaskReminders", // Unique name for the work
            ExistingPeriodicWorkPolicy.KEEP, // Keep existing if already scheduled
            taskReminderRequest
        )
    }
}