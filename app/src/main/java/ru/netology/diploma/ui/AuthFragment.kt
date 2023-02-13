package ru.netology.diploma.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.databinding.FragmentAuthentificationBinding
import ru.netology.diploma.extensions.createToast
import ru.netology.diploma.util.AndroidUtils
import ru.netology.diploma.util.parseException
import ru.netology.diploma.viewmodel.AuthentificationViewModel

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private val viewModel: AuthentificationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAuthentificationBinding.inflate(
            inflater,
            container,
            false
        )

        viewModel.responseAuthState.observe(viewLifecycleOwner) { token ->
            if (token != null) {
                viewModel.saveToken(token)
                findNavController().navigateUp()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            val errorMessage = "${parseException(error)} Authentification failed, try later"
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
            findNavController().navigateUp()
        }

        binding.enter.setOnClickListener {
            val login = binding.login.text.toString()
            val pass = binding.password.text.toString()

            if (login.isBlank() || pass.isBlank()) {
                it.createToast(R.string.error_empty_registration_form)
                return@setOnClickListener
            } else {
                viewModel.updateUser(login, pass)
            }
            AndroidUtils.hideKeyboard(requireView())
        }

        return binding.root

    }

}