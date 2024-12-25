package com.example.taskmanager.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.databinding.ItemTaskBinding
import com.example.taskmanager.model.Task
import androidx.core.content.ContextCompat
import com.example.taskmanager.R
import com.example.taskmanager.model.Priority

class TaskAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onTaskCheckChanged: (Task, Boolean) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var tasks = listOf<Task>()

    fun submitList(newTasks: List<Task>) {
        tasks = newTasks.toList() // Create a new copy of the list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasks[position]
        holder.bind(currentTask)
    }

    override fun getItemCount() = tasks.size

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.apply {
                textViewTitle.text = task.title
                textViewDueDate.text = "Due: ${task.dueDate}"
                textViewPriority.text = "Priority: ${task.priority}"

                // Set checkbox without listener first
                checkBoxCompleted.setOnCheckedChangeListener(null)
                checkBoxCompleted.isChecked = task.isCompleted

                // Set new listener
                checkBoxCompleted.setOnCheckedChangeListener { _, isChecked ->
                    onTaskCheckChanged(task, isChecked)
                }

                root.setOnClickListener {
                    onTaskClick(task)
                }

                // Handle strikethrough
                textViewTitle.paintFlags = if (task.isCompleted) {
                    textViewTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    textViewTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }

                // Set priority color
                val priorityColor = when (task.priority) {
                    Priority.HIGH -> ContextCompat.getColor(root.context, R.color.priority_high)
                    Priority.MEDIUM -> ContextCompat.getColor(root.context, R.color.priority_medium)
                    Priority.LOW -> ContextCompat.getColor(root.context, R.color.priority_low)
                }
                priorityIndicator.setBackgroundColor(priorityColor)
            }
        }
    }

    fun getTaskAt(position: Int): Task = tasks[position]
}