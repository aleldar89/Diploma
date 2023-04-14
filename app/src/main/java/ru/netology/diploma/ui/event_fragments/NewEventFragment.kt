package ru.netology.diploma.ui.event_fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.databinding.FragmentNewEventBinding
import ru.netology.diploma.dto.AudioAttachment
import ru.netology.diploma.dto.Coordinates
import ru.netology.diploma.dto.ImageAttachment
import ru.netology.diploma.dto.VideoAttachment
import ru.netology.diploma.extensions.createToast
import ru.netology.diploma.mediplayer.MediaLifecycleObserver
import ru.netology.diploma.ui.StartDatePickFragment
import ru.netology.diploma.util.AndroidUtils
import ru.netology.diploma.util.StringArg
import ru.netology.diploma.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class NewEventFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
        private const val MAX_IMAGE_SIZE = 2048
        private const val DATE_PATTERN = "yyyy-MM-dd"
        var currentLocation: Coordinates? = null
    }
    private lateinit var dateTime: Date
    private val gson = Gson()
    private val sdf = SimpleDateFormat(DATE_PATTERN, Locale.US)

    private val viewModel: EventViewModel by activityViewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    private val imageContract = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            ImagePicker.RESULT_ERROR -> {
                view?.createToast(R.string.media_error)
            }
            else -> {
                val uri = result.data?.data ?: run {
                    view?.createToast(R.string.media_error)
                    return@registerForActivityResult
                }
                viewModel.attachImage(uri, uri.toFile())
            }
        }
    }

    private val videoContract = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data ?: run {
                view?.createToast(R.string.media_error)
                return@registerForActivityResult
            }
            viewModel.attachVideo(uri)
        } else {
            view?.createToast(R.string.media_error)
        }
    }

    private val audioContract = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data ?: run {
                view?.createToast(R.string.media_error)
                return@registerForActivityResult
            }
            viewModel.attachAudio(uri)
        } else {
            view?.createToast(R.string.media_error)
        }
    }

    private var binding: FragmentNewEventBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getCurrentLocation()

        setFragmentResultListener("requestStartKey") { _, bundle ->
            dateTime = gson.fromJson(bundle.getString("bundleStartKey"), Date::class.java)
            binding?.datetime?.text = sdf.format(dateTime)
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
                    content = binding?.edit?.text?.toString()?.trim() ?: "",
                    datetime = binding?.datetime?.text.toString().trim(),
                    link = binding?.link?.text?.toString()?.trim(),
                    coords = currentLocation
                )
                viewModel.save()
                AndroidUtils.hideKeyboard(requireView())
                true
            }
            R.id.cancel -> {
                viewModel.clearEditedData()
                viewModel.clearMedia()
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

//        val dateSetListener =
//            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
//                cal.set(Calendar.YEAR, year)
//                cal.set(Calendar.MONTH, monthOfYear)
//                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//
//                val sdf = SimpleDateFormat(datePattern, Locale.US)
//                binding.datetime.text = sdf.format(cal.time)
//            }

        arguments?.textArg?.let(binding.edit::setText)

        viewModel.media.observe(viewLifecycleOwner) { media ->
            when (media) {
                is ImageAttachment -> {
                    binding.previewImage.isVisible = true
                    binding.previewImage.setImageURI(media.uri)
                }
                is VideoAttachment -> {
                    binding.previewVideo.isVisible = true
                    binding.previewVideo.apply {
                        setVideoURI(media.uri)
                        seekTo(1)
                    }
                }
                is AudioAttachment -> {
                    binding.previewAudio.isVisible = true
                    binding.previewAudio.apply {
                        setOnClickListener {
                            MediaLifecycleObserver().apply {
                                mediaPlayer?.stop()
                                mediaPlayer?.reset()
                                mediaPlayer?.setDataSource(context, media.uri!!)
                            }.play()
                        }
                    }
                }
                null -> {
                    binding.previewImage.isGone = true
                    binding.previewVideo.isGone = true
                    binding.previewAudio.isGone = true
                    return@observe
                }
            }
        }

        binding.dateButton.setOnClickListener {
            StartDatePickFragment().show(
                parentFragmentManager, StartDatePickFragment.TAG
            )
        }

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
            videoContract.launch(intent)
        }

        binding.galleryAudio.setOnClickListener {
            val intent = Intent()
                .setType("audio/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            audioContract.launch(intent)
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
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) -> {
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