package com.example.taskmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.taskmanager.databinding.FragmentCalendarViewBinding
import java.text.SimpleDateFormat
import java.util.*

class CalendarViewFragment : Fragment() {
    private var _binding: FragmentCalendarViewBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by activityViewModels()
    private val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCalendarView()
        observeTasks()
    }

    private fun setupCalendarView() {
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val selectedDate = dateFormat.format(calendar.time)

            viewModel.getTasksForDate(selectedDate).observe(viewLifecycleOwner) { tasks ->
                // Update the tasks list for the selected date
                binding.tasksList.adapter = TaskAdapter(
                    onTaskClick = { task ->
                        val action = CalendarViewFragmentDirections
                            .actionCalendarViewToTaskDetails(task.id)
                        findNavController().navigate(action)
                    },
                    onTaskCheckChanged = { task, isChecked ->
                        viewModel.updateTaskStatus(task.id, isChecked)
                    }
                ).apply {
                    submitList(tasks)
                }
            }
        }
    }

    private fun observeTasks() {
        // Initially show today's tasks
        val today = dateFormat.format(Calendar.getInstance().time)
        viewModel.getTasksForDate(today).observe(viewLifecycleOwner) { tasks ->
            binding.tasksList.adapter = TaskAdapter(
                onTaskClick = { task ->
                    val action = CalendarViewFragmentDirections
                        .actionCalendarViewToTaskDetails(task.id)
                    findNavController().navigate(action)
                },
                onTaskCheckChanged = { task, isChecked ->
                    viewModel.updateTaskStatus(task.id, isChecked)
                }
            ).apply {
                submitList(tasks)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}