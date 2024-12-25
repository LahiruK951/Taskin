package com.example.taskmanager

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.taskmanager.databinding.ActivityMainBinding
import com.example.taskmanager.util.TaskReminderWorker
import com.example.taskmanager.util.ThemeManager
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply theme before setting content view
        ThemeManager.applyTheme(ThemeManager.isDarkMode(this))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Set up the ActionBar
        setSupportActionBar(binding.toolbar) // Make sure you have a toolbar in activity_main.xml
        setupActionBarWithNavController(navController)

        // Request notification permission and schedule reminders
        requestNotificationPermission()
        scheduleTaskReminders()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settingsFragment -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.settingsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
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