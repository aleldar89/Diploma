package ru.netology.diploma.ui.event_fragments

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.databinding.FragmentNewEventBinding
import ru.netology.diploma.dto.Coordinates
import ru.netology.diploma.extensions.createToast
import ru.netology.diploma.extensions.dateFormatter
import ru.netology.diploma.ui.job_fragments.NewJobFragment
import ru.netology.diploma.util.AndroidUtils
import ru.netology.diploma.util.StringArg
import ru.netology.diploma.viewmodel.EventViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class NewEventFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
        private const val MAX_IMAGE_SIZE = 2048
        private const val datePattern = "yyyy-MM-dd"
        var currentLocation: Coordinates? = null
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    private val viewModel: EventViewModel by activityViewModels()

    private val imageContract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                ImagePicker.RESULT_ERROR -> {
                    view?.createToast(R.string.media_error)
                }
                else -> {
                    val data = result.data?.data ?: run {
                        view?.createToast(R.string.media_error)
                        return@registerForActivityResult
                    }
                    viewModel.changeMedia(data, data.toFile())
                }
            }
        }

    private val mvContract = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.data ?: run {
                view?.createToast(R.string.media_error)
                return@registerForActivityResult
            }
//            viewModel.changeVideo(data, data.toFile()) //todo Exception: Uri lacks 'file' scheme
            viewModel.changeMedia(data, File(data.path))
        } else {
            view?.createToast(R.string.media_error)
        }
    }

    private var binding: FragmentNewEventBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getCurrentLocation()
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
                    content = binding?.edit?.text?.toString() ?: "",
                    datetime = binding?.dateTime?.text.toString().trim().dateFormatter(),
                    link = binding?.link?.text?.toString()?.trim(),
                    coords = currentLocation
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
        val binding = FragmentNewEventBinding.inflate(
            inflater,
            container,
            false
        ).also {
            this.binding = it
        }

        val cal = Calendar.getInstance()

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val sdf = SimpleDateFormat(datePattern, Locale.US)
                binding.dateTime.text = sdf.format(cal.time)
            }

        arguments?.textArg?.let(binding.edit::setText)

        binding.dateTime.text = SimpleDateFormat(datePattern).format(System.currentTimeMillis())
        binding.dateTime.setOnClickListener {
            DatePickerDialog(
                requireContext(), dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

//        viewModel.image.observe(viewLifecycleOwner) {
//            if (it == null) {
//                binding.photoContainer.isGone = true
//                return@observe
//            }
//
//            binding.photoContainer.isVisible = true
//            binding.previewPhoto.setImageURI(it.uri)
//        }
//
//        viewModel.media.observe(viewLifecycleOwner) {
//            if (it == null) {
//                binding.videoContainer.isGone = true
//                return@observe
//            }
//
//            binding.videoContainer.isVisible = true
//            binding.previewVideo.apply {
//                setVideoURI(it.uri)
//                seekTo(1)
//            }
//        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .crop()
                .cameraOnly()
                .maxResultSize(MAX_IMAGE_SIZE, MAX_IMAGE_SIZE)
                .createIntent(imageContract::launch)
        }

        binding.galleryImage.setOnClickListener {
            ImagePicker.Builder(this)
                .crop()
                .galleryOnly()
                .maxResultSize(MAX_IMAGE_SIZE, MAX_IMAGE_SIZE)
                .createIntent(imageContract::launch)
        }

        binding.galleryVideo.setOnClickListener {
            val intent = Intent()
                .setType("video/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            mvContract.launch(intent)
        }

        binding.galleryMusic.setOnClickListener {
            val intent = Intent()
                .setType("audio/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            mvContract.launch(intent)
        }

        binding.clearMedia.setOnClickListener {
            viewModel.clearMedia()
        }

        viewModel.eventCreated.observe(viewLifecycleOwner) {
            viewModel.loadEvents()
            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun getCurrentLocation() {
        lifecycle.coroutineScope.launchWhenCreated {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            currentLocation = Coordinates(
                                lat = location.latitude.toString().dropLast(1),
                                longitude = location.longitude.toString().dropLast(1)
                            )
                        } else {
                            return@addOnSuccessListener
                        }
                    }
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }
    }

}