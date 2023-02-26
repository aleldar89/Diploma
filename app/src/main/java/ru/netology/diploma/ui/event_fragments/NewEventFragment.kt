package ru.netology.diploma.ui.event_fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.databinding.FragmentNewEventBinding
import ru.netology.diploma.util.AndroidUtils
import ru.netology.diploma.util.StringArg
import ru.netology.diploma.viewmodel.EventViewModel

@AndroidEntryPoint
class NewEventFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
        private const val MAX_IMAGE_SIZE = 2048
    }

    private val viewModel: EventViewModel by activityViewModels()

    private val imageContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when (result.resultCode) {
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(requireContext(), "Image not captured", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val data = result.data?.data ?: run {
                    Toast.makeText(requireContext(), "Image not captured", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }
                viewModel.changePhoto(data, data.toFile())
            }
        }
    }

    private var binding: FragmentNewEventBinding? = null

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
        if (item.itemId == R.id.save) {
            viewModel.changeContent(binding?.edit?.text?.toString().orEmpty())
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
            true
        } else {
            false
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewEventBinding.inflate(
            inflater,
            container,
            false
        ).also {
            this.binding = it
        }

        arguments?.textArg
            ?.let(binding.edit::setText)

        viewModel.media.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.photoContainer.isGone = true
                return@observe
            }

            binding.photoContainer.isVisible = true
            binding.preview.setImageURI(it.uri)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .crop()
                .cameraOnly()
                .maxResultSize(MAX_IMAGE_SIZE, MAX_IMAGE_SIZE)
                .createIntent(imageContract::launch)
        }

        binding.gallery.setOnClickListener {
            ImagePicker.Builder(this)
                .crop()
                .galleryOnly()
                .maxResultSize(MAX_IMAGE_SIZE, MAX_IMAGE_SIZE)
                .createIntent(imageContract::launch)
        }

        binding.clearPhoto.setOnClickListener {
            viewModel.clearPhoto()
        }

        viewModel.eventCreated.observe(viewLifecycleOwner) {
            viewModel.loadEvents()
            findNavController().navigateUp()
        }

        return binding.root
    }
}