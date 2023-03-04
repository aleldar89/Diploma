package ru.netology.diploma.ui.event_fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.map
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map
import ru.netology.diploma.R
import ru.netology.diploma.adapter.EventViewHolder
import ru.netology.diploma.adapter.OnInteractionListener
import ru.netology.diploma.databinding.CardEventBinding
import ru.netology.diploma.dto.Event
import ru.netology.diploma.mediplayer.MediaLifecycleObserver
import ru.netology.diploma.util.StringArg
import ru.netology.diploma.viewmodel.EventViewModel

@AndroidEntryPoint
class SelectedEventFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: EventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = CardEventBinding.inflate(
            inflater,
            container,
            false
        )

        val gson = Gson()
        val event: Event = arguments?.textArg.let { gson.fromJson(it, Event::class.java) }

        val eventViewHolder = EventViewHolder(
            binding,
            object : OnInteractionListener<Event> {
                override fun onLike(event: Event) {
                    if (event.likedByMe)
                        viewModel.dislikeById(event)
                    else
                        viewModel.likeById(event)
                }

                override fun onEdit(event: Event) {
                    findNavController().navigate(
                        R.id.action_selectedPostFragment_to_newPostFragment,
                        Bundle().apply {
                            textArg = event.content
                        }
                    )
                    viewModel.edit(event)
                }

                override fun onRemove(event: Event) {
                    viewModel.removeById(event.id)
                    findNavController().navigateUp()
                }

                override fun onShare(event: Event) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, event.content)
                        type = "text/plain"
                    }

                    val shareIntent =
                        Intent.createChooser(intent, getString(R.string.chooser_share_post))
                    startActivity(shareIntent)
                }
            },
            MediaLifecycleObserver(), binding.root.context
        )

        eventViewHolder.bind(event)

        return binding.root
    }
}