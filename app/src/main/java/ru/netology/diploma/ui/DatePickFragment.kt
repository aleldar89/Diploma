package ru.netology.diploma.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DatePickFragment : DialogFragment() {

    companion object {
        const val TAG = "DatePickFragment"
    }
    private val gson = Gson()
    private val calendar = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dateListener = DatePickerDialog.OnDateSetListener {
                _: DatePicker, year, month, day ->
            val resultDate = GregorianCalendar(year, month, day).time

            parentFragmentManager.setFragmentResult(
                "requestKey",
                bundleOf("bundleKey" to gson.toJson(resultDate))
            )
        }

        return DatePickerDialog(
            requireContext(),
            dateListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
}