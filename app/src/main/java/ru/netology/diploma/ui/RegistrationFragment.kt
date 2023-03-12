package ru.netology.diploma.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.databinding.FragmentRegistrationBinding
import ru.netology.diploma.extensions.createToast
import ru.netology.diploma.util.AndroidUtils
import ru.netology.diploma.viewmodel.RegistrationViewModel

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    companion object {
        private const val MAX_IMAGE_SIZE = 500
    }

    private val viewModel: RegistrationViewModel by viewModels()

    private val imageContract = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result->
        when(result.resultCode) {
            ImagePicker.RESULT_ERROR -> {
                view?.createToast(R.string.media_error)
            }

            else -> {
                val data = result.data?.data ?: run {
                    view?.createToast(R.string.media_error)
                    return@registerForActivityResult
                }
                viewModel.changePhoto(data, data.toFile())
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegistrationBinding.inflate(
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
            Toast.makeText(
                context,
                error.message,
                Toast.LENGTH_LONG
            ).show()
        }

        viewModel.media.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.avatar.isGone = true
                return@observe
            }

            binding.avatar.isVisible = true
            binding.avatar.setImageURI(it.uri)
        }

        binding.enter.setOnClickListener {
            val name = binding.name.text.toString()
            val login = binding.login.text.toString()
            val pass = binding.password.text.toString()
            val confirmPass = binding.confirmPassword.text.toString()
            val file = viewModel.media.value?.file

            if (login.isBlank() || pass.isBlank() || name.isBlank()) {
                it.createToast(R.string.error_empty_registration_form)
                return@setOnClickListener
            } else if (pass != confirmPass) {
                it.createToast(R.string.error_confirm_password)
                return@setOnClickListener
            } else {
                if (file == null)
                    viewModel.registerUser(login, pass, name)
                else
                    viewModel.registerWithPhoto(login, pass, name, file)
            }

            AndroidUtils.hideKeyboard(requireView())
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .crop()
                .cameraOnly()
                .maxResultSize(MAX_IMAGE_SIZE, MAX_IMAGE_SIZE)
                .createIntent(imageContract::launch)
        }

        return binding.root
    }

}