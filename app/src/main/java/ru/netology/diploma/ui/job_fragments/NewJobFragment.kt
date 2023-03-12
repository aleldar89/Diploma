package ru.netology.diploma.ui.job_fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
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
import ru.netology.diploma.extensions.createDate
import ru.netology.diploma.extensions.dateFormatter
import ru.netology.diploma.extensions.toEditable
import ru.netology.diploma.extensions.toJob
import ru.netology.diploma.ui.post_fragments.NewPostFragment.Companion.textArg
import ru.netology.diploma.util.AndroidUtils
import ru.netology.diploma.util.StringArg
import ru.netology.diploma.viewmodel.MyJobViewModel
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class NewJobFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
        private const val datePattern = "yyyy-MM-dd"
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
                    name = viewModel.userResponse.value?.name ?: "",
                    binding?.position?.text.toString().trim(),
                    binding?.start?.text.toString().trim().dateFormatter(),
                    binding?.finish?.text.toString().trim().dateFormatter(),
                    binding?.link?.text.toString().trim(),
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

        val cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val sdf = SimpleDateFormat(datePattern, Locale.US)
            binding.start.text = sdf.format(cal.time)
            binding.finish.text = sdf.format(cal.time)
        }

        val job = arguments?.textArg?.toJob()

        binding.apply {
            position.text = job?.position?.toEditable()
            start.text = job?.start
            finish.text = job?.finish
            link.text = job?.link?.toEditable()
        }

        binding.start.text = SimpleDateFormat(datePattern).format(System.currentTimeMillis())
        binding.finish.text = SimpleDateFormat(datePattern).format(System.currentTimeMillis())

        binding.start.setOnClickListener {
            DatePickerDialog(requireContext(), dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.finish.setOnClickListener {
            DatePickerDialog(requireContext(), dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        viewModel.jobCreated.observe(viewLifecycleOwner) {
            viewModel.loadJobs()
            findNavController().navigateUp()
        }

        return binding.root
    }
}