package ru.netology.diploma.ui.job_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.adapter.JobAdapter
import ru.netology.diploma.adapter.OnInteractionListener
import ru.netology.diploma.databinding.FragmentJobFeedBinding
import ru.netology.diploma.dto.Job
import ru.netology.diploma.extensions.loadAvatar
import ru.netology.diploma.util.StringArg
import ru.netology.diploma.util.parseException
import ru.netology.diploma.viewmodel.MyJobViewModel

@AndroidEntryPoint
class MyJobFeedFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: MyJobViewModel by activityViewModels()

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

        viewModel.userResponse.observe(viewLifecycleOwner) {
            binding.author.text = it?.name
            binding.authorAvatar.loadAvatar(it?.avatar.orEmpty())
        }

        binding.list.apply {
            this.adapter = adapter
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.addItemDecoration(
                DividerItemDecoration(
                    context,
                    layoutManager.orientation
                )
            )
        }

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