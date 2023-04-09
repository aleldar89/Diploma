package ru.netology.diploma.ui.job_fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.*
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.databinding.FragmentNewJobBinding
import ru.netology.diploma.extensions.createDate
import ru.netology.diploma.extensions.createToast
import ru.netology.diploma.extensions.toEditable
import ru.netology.diploma.extensions.toJob
import ru.netology.diploma.ui.DatePickFragment
import ru.netology.diploma.util.AndroidUtils
import ru.netology.diploma.util.StringArg
import ru.netology.diploma.viewmodel.MyJobViewModel
import java.text.SimpleDateFormat
import java.util.*

//@AndroidEntryPoint
//class NewJobFragment : Fragment(), FragmentResultListener {
//
//    companion object {
//        var Bundle.textArg: String? by StringArg
//        private const val DATE_PATTERN = "yyyy-MM-dd"
//    }
//
//    private val gson = Gson()
//    private var resultDate: Date? = null
//    private val sdf = SimpleDateFormat(DATE_PATTERN, Locale.US)
//
//    private val viewModel: MyJobViewModel by activityViewModels()
//
//    private var binding: FragmentNewJobBinding? = null
//
//    override fun onFragmentResult(requestKey: String, result: Bundle) {
//        when (requestKey) {
//            "requestKey" -> {
//                resultDate = gson.fromJson(result.getString("bundleKey"), Date::class.java)
//                binding?.startDate?.text = sdf.format(resultDate)
//                binding?.finishDate?.text = sdf.format(resultDate)
//            }
//        }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        childFragmentManager.setFragmentResultListener(
//            DatePickFragment.TAG,
//            viewLifecycleOwner,
//            this
//        )
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
//
////        setFragmentResultListener("requestKey") { _, bundle ->
////            resultDate = gson.fromJson(bundle.getString("bundleKey"), Date::class.java)
////        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        binding = null
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.options_new_item, menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean =
//        when (item.itemId) {
//            R.id.save -> {
//                viewModel.changeContent(
//                    name = viewModel.userResponse.value?.name ?: "",
//                    position = binding?.position?.text.toString().trim(),
//                    start = binding?.startDate?.text.toString().trim(),
//                    finish = binding?.finishDate?.text.toString().trim(),
//                    link = binding?.link?.text.toString().trim()
//                )
//                viewModel.save()
//                AndroidUtils.hideKeyboard(requireView())
//                true
//            }
//            R.id.cancel -> {
//                viewModel.clearEditedData()
//                AndroidUtils.hideKeyboard(requireView())
//                findNavController().navigateUp()
//                true
//            }
//            else -> {
//                false
//            }
//        }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val binding = FragmentNewJobBinding.inflate(
//            inflater,
//            container,
//            false
//        ).also {
//            this.binding = it
//        }
//
//        arguments?.textArg?.toJob().let {
//            binding.apply {
//                position.text = it?.position?.toEditable()
//                link.text = it?.link?.toEditable()
//                startDate.text = it?.start?.createDate()
//                finishDate.text = it?.finish?.createDate()
//            }
//        }
//
//        binding.startDateButton.setOnClickListener {
//            DatePickFragment().show(
//                childFragmentManager, DatePickFragment.TAG
//            )
//        }
//
//        binding.finishDateButton.setOnClickListener {
//            DatePickFragment().show(
//                childFragmentManager, DatePickFragment.TAG
//            )
//        }
//
//        viewModel.jobCreated.observe(viewLifecycleOwner) {
//            findNavController().navigateUp()
//        }
//
//        return binding.root
//    }
//}

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

        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat(datePattern, Locale.US)

        val startDateListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.startDate.text = sdf.format(cal.time)
            }

        val finishDateListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.finishDate.text = sdf.format(cal.time)
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
            DatePickerDialog(
                requireContext(), startDateListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.finishDateButton.setOnClickListener {
            DatePickerDialog(
                requireContext(), finishDateListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        viewModel.jobCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        return binding.root
    }
}