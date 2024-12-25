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

class TaskDetailsFragment : Fragment() {
    private var _binding: FragmentTaskDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by activityViewModels()
    private val args: TaskDetailsFragmentArgs by navArgs()

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

        viewModel.getTaskById(args.taskId).observe(viewLifecycleOwner) { task ->
            task?.let {
                binding.apply {
                    textViewTitle.text = it.title
                    textViewDescription.text = it.description
                    textViewDueDate.text = "Due: ${it.dueDate}"
                    textViewPriority.text = "Priority: ${it.priority}"
                    checkBoxCompleted.isChecked = it.isCompleted

                    checkBoxCompleted.setOnCheckedChangeListener { _, isChecked ->
                        viewModel.updateTaskStatus(it.id, isChecked)
                    }

                    buttonEdit.setOnClickListener {
                        // Navigate to edit task (implement later)
                    }

                    buttonDelete.setOnClickListener {
                        viewModel.deleteTask(task)
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}