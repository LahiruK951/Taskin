package com.example.taskmanager.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentAddEditTaskBinding
import com.example.taskmanager.model.Priority
import java.util.Calendar

class AddEditTaskFragment : Fragment() {
    private var _binding: FragmentAddEditTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by activityViewModels()
    private val args: AddEditTaskFragmentArgs by navArgs()
    private var editingTask = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupDatePicker()
        loadTaskIfEditing()
        setupSaveButton()
    }

    private fun setupUI() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.priority_levels,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerPriority.adapter = adapter
        }
    }

    private fun loadTaskIfEditing() {
        if (args.taskId != -1L) {
            editingTask = true
            viewModel.getTaskById(args.taskId).observe(viewLifecycleOwner) { task ->
                task?.let {
                    binding.apply {
                        editTextTitle.setText(it.title)
                        editTextDescription.setText(it.description)
                        editTextDueDate.setText(it.dueDate)
                        spinnerPriority.setSelection(
                            when (it.priority) {
                                Priority.LOW -> 0
                                Priority.MEDIUM -> 1
                                Priority.HIGH -> 2
                            }
                        )
                        buttonSave.text = "Update Task"
                    }
                }
            }
        }
    }

    private fun setupDatePicker() {
        binding.editTextDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val date = String.format("%02d/%02d/%d", month + 1, day, year)
                    binding.editTextDueDate.setText(date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            val title = binding.editTextTitle.text.toString()
            val description = binding.editTextDescription.text.toString()
            val dueDate = binding.editTextDueDate.text.toString()

            val priority = when(binding.spinnerPriority.selectedItemPosition) {
                0 -> Priority.LOW
                1 -> Priority.MEDIUM
                2 -> Priority.HIGH
                else -> Priority.LOW
            }

            if (title.isNotEmpty() && dueDate.isNotEmpty()) {
                if (editingTask) {
                    viewModel.getTaskById(args.taskId).observe(viewLifecycleOwner) { task ->
                        task?.let {
                            viewModel.updateTask(it.copy(
                                title = title,
                                description = description,
                                dueDate = dueDate,
                                priority = priority
                            ))
                            findNavController().navigateUp()
                        }
                    }
                } else {
                    viewModel.addTask(title, description, dueDate, priority)
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