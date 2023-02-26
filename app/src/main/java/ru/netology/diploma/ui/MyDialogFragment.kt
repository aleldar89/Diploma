package ru.netology.diploma.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.auth.AppAuth
import javax.inject.Inject

@AndroidEntryPoint
class MyDialogFragment : DialogFragment() {

    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.are_you_sure)
                .setPositiveButton(R.string.logout) { _, _ ->
                    appAuth.clearAuth()
                    findNavController().navigate(R.id.postsFeedFragment)
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    findNavController().navigateUp()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}