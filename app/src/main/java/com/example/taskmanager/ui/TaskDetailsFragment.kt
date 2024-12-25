package com.example.taskmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.taskmanager.databinding.FragmentTaskDetailsBinding
import com.example.taskmanager.model.Task

class TaskDetailsFragment : Fragment() {
    private var _binding: FragmentTaskDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by activityViewModels()
    private val args: TaskDetailsFragmentArgs by navArgs()
    private var currentTask: Task? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupButtons()
    }

    private fun setupObservers() {
        viewModel.getTaskById(args.taskId).observe(viewLifecycleOwner) { task ->
            task?.let {
                currentTask = it
                updateUI(it)
            }
        }
    }

    private fun updateUI(task: Task) {
        binding.apply {
            textViewTitle.text = task.title
            textViewDescription.text = task.description
            textViewDueDate.text = "Due: ${task.dueDate}"
            textViewPriority.text = "Priority: ${task.priority}"
            checkBoxCompleted.isChecked = task.isCompleted
        }
    }

    private fun setupButtons() {
        binding.apply {
            checkBoxCompleted.setOnCheckedChangeListener { _, isChecked ->
                currentTask?.let {
                    viewModel.updateTaskStatus(it.id, isChecked)
                }
            }

            buttonDelete.setOnClickListener {
                currentTask?.let {
                    viewModel.deleteTask(it)
                    findNavController().navigateUp()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}