package ru.netology.diploma.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.util.StringArg
import ru.netology.diploma.viewmodel.MyJobViewModel
import java.util.*

@AndroidEntryPoint
class DatePickFragment : DialogFragment() {

    companion object {
        private const val ARG_DATE = "ARG_DATE"
        private const val RESULT_DATE = "RESULT_DATE"
        private const val REQUEST_DATE = 0

        fun newInstance(date: Date): DatePickFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }
            return DatePickFragment().apply {
                arguments = args
            }
        }
    }

    //todo переделать с вьюмоделью или отказаться от отдельного фрагмента
    private val viewModel: MyJobViewModel by activityViewModels()

    interface Callbacks {
        fun onDateSelected(date: Date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val resultDate = GregorianCalendar(year, month, day).time

//            parentFragmentManager.setFragmentResult(RESULT_DATE, Bundle().apply {
//                putSerializable(RESULT_DATE, resultDate)
//            })

            targetFragment?.let { fragment ->
                (fragment as Callbacks).onDateSelected(resultDate)
            }
        }

//        val date = arguments?.getSerializable(ARG_DATE, Date::class.java)
        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(
            requireContext(),
            dateListener,
            year,
            month,
            day
        )
    }

}