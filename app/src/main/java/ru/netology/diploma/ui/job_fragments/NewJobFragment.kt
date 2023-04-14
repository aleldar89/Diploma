package ru.netology.diploma.ui.job_fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.*
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.databinding.FragmentNewJobBinding
import ru.netology.diploma.extensions.createDate
import ru.netology.diploma.extensions.toEditable
import ru.netology.diploma.extensions.toJob
import ru.netology.diploma.ui.FinishDatePickFragment
import ru.netology.diploma.ui.StartDatePickFragment
import ru.netology.diploma.util.AndroidUtils
import ru.netology.diploma.util.StringArg
import ru.netology.diploma.viewmodel.MyJobViewModel
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class NewJobFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
        private const val DATE_PATTERN = "yyyy-MM-dd"
    }

    private lateinit var startDate: Date
    private lateinit var finishDate: Date
    private val gson = Gson()
    private val sdf = SimpleDateFormat(DATE_PATTERN, Locale.US)

    private val viewModel: MyJobViewModel by activityViewModels()

    private var binding: FragmentNewJobBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        setFragmentResultListener("requestStartKey") { _, bundle ->
            startDate = gson.fromJson(bundle.getString("bundleStartKey"), Date::class.java)
            binding?.startDate?.text = sdf.format(startDate)
        }

        setFragmentResultListener("requestFinishKey") { _, bundle ->
            finishDate = gson.fromJson(bundle.getString("bundleFinishKey"), Date::class.java)
            binding?.finishDate?.text = sdf.format(finishDate)
        }

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
                    position = binding?.position?.text.toString().trim(),
                    start = binding?.startDate?.text.toString().trim(),
                    finish = binding?.finishDate?.text.toString().trim(),
                    link = binding?.link?.text.toString().trim()
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

        arguments?.textArg?.toJob().let {
            binding.apply {
                position.text = it?.position?.toEditable()
                link.text = it?.link?.toEditable()
                startDate.text = it?.start?.createDate()
                finishDate.text = it?.finish?.createDate()
            }
        }

        binding.startDateButton.setOnClickListener {
            StartDatePickFragment().show(
                parentFragmentManager, StartDatePickFragment.TAG
            )
        }

        binding.finishDateButton.setOnClickListener {
            FinishDatePickFragment().show(
                parentFragmentManager, FinishDatePickFragment.TAG
            )
        }

        viewModel.jobCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        return binding.root
    }
}