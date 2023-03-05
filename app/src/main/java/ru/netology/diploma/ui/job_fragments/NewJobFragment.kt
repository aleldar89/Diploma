package ru.netology.diploma.ui.job_fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.databinding.FragmentNewJobBinding
import ru.netology.diploma.dto.Job
import ru.netology.diploma.dto.UserPreview
import ru.netology.diploma.extensions.toEditable
import ru.netology.diploma.extensions.toJob
import ru.netology.diploma.ui.post_fragments.NewPostFragment.Companion.textArg
import ru.netology.diploma.util.AndroidUtils
import ru.netology.diploma.util.StringArg
import ru.netology.diploma.viewmodel.MyJobViewModel

@AndroidEntryPoint
class NewJobFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: MyJobViewModel by activityViewModels()

    private var binding: FragmentNewJobBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_new_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.save -> {
                viewModel.changeContent(
                    binding?.position?.text.toString(),
                    binding?.start?.text.toString(),
                    binding?.finish?.text.toString(),
                    binding?.link?.text.toString(),
                )
                viewModel.save()
                AndroidUtils.hideKeyboard(requireView())
                true
            }
            R.id.cancel -> {
                viewModel.clearEditedData()
                AndroidUtils.hideKeyboard(requireView())
                findNavController().navigateUp()
                true
            }
            else -> {
                false
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewJobBinding.inflate(
            inflater,
            container,
            false
        ).also {
            this.binding = it
        }

        val job = arguments?.textArg?.toJob()

        binding.apply {
            position.text = job?.position?.toEditable()
            start.text = job?.start?.toEditable()
            finish.text = job?.finish?.toEditable()
            link.text = job?.link?.toEditable()
        }

        viewModel.jobCreated.observe(viewLifecycleOwner) {
            viewModel.loadJobs()
            findNavController().navigateUp()
        }

        return binding.root
    }
}