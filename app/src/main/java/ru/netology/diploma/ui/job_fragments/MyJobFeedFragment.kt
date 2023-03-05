package ru.netology.diploma.ui.job_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.adapter.JobAdapter
import ru.netology.diploma.adapter.OnInteractionListener
import ru.netology.diploma.databinding.FragmentJobFeedBinding
import ru.netology.diploma.dto.Job
import ru.netology.diploma.dto.Post
import ru.netology.diploma.extensions.loadAvatar
import ru.netology.diploma.ui.post_fragments.PostsFeedFragment.Companion.textArg
import ru.netology.diploma.util.StringArg
import ru.netology.diploma.util.parseException
import ru.netology.diploma.viewmodel.MyJobViewModel

@AndroidEntryPoint
class MyJobFeedFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: MyJobViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentJobFeedBinding.inflate(inflater, container, false)

        val gson = Gson()

        val adapter = JobAdapter(object : OnInteractionListener<Job>{
            override fun onRemove(item: Job) {
                viewModel.removeById(item.id)
            }

            override fun onEdit(item: Job) {
                findNavController().navigate(
                    R.id.action_myJobFeedFragment_to_newJobFragment,
                    Bundle().apply {
                        textArg = gson.toJson(item)
                    }
                )
                viewModel.edit(item)
            }

        })

        binding.author.text = viewModel.userResponse.value?.name
        viewModel.userResponse.value?.avatar?.let { binding.authorAvatar.loadAvatar(it) }

        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.emptyText.isVisible = it.isEmpty()
        }

        viewModel.error.observe(viewLifecycleOwner) {
            val errorMessage = parseException(it)
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry_loading) {
                    viewModel.loadJobs()
                }
                .show()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_myJobFeedFragment_to_newJobFragment)
        }

        return binding.root

    }

}