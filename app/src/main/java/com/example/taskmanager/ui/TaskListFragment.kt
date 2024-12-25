package com.example.taskmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentTaskListBinding

class TaskListFragment : Fragment() {
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by activityViewModels()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeToDelete()
        setupFab()
        observeTasks()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskClick = { task ->
                val action = TaskListFragmentDirections.actionTaskListToTaskDetails(task.id)
                findNavController().navigate(action)
            },
            onTaskCheckChanged = { task, isChecked ->
                viewModel.updateTaskStatus(task.id, isChecked)
            }
        )

        binding.recyclerView.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupFab() {
        binding.fabAddTask.setOnClickListener {
            findNavController().navigate(R.id.action_taskList_to_addTask)
        }
    }

    private fun observeTasks() {
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
        }
    }

    private fun setupSwipeToDelete() {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(
            0, // Drag directions (we don't need drag & drop)
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT // Swipe directions
        ) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = taskAdapter.getTaskAt(position)
                viewModel.deleteTask(task)
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.recyclerView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}