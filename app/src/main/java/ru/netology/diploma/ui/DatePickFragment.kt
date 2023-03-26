package ru.netology.diploma.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.adapter.PostViewHolder.Companion.textArg
import ru.netology.diploma.util.StringArg
import java.util.*

@AndroidEntryPoint
class DatePickFragment : DialogFragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
        private const val ARG_DATE = "ARG_DATE"
    }

    private val calendar: Calendar = Calendar.getInstance()
    private val gson = Gson()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val resultDay = GregorianCalendar(year, month, day).time

            parentFragmentManager.setFragmentResult(ARG_DATE, Bundle().apply {
                textArg = gson.toJson(resultDay)
            })
        }

        val date = arguments?.getSerializable(ARG_DATE, Date::class.java)
        if (date != null) {
            calendar.time = date
        }
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