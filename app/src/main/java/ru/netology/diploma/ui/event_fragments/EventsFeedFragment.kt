package ru.netology.diploma.ui.event_fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import ru.netology.diploma.R
import ru.netology.diploma.adapter.EventsAdapter
import ru.netology.diploma.adapter.OnInteractionListener
import ru.netology.diploma.databinding.FragmentEventFeedBinding
import ru.netology.diploma.dto.Event
import ru.netology.diploma.mediplayer.MediaLifecycleObserver
import ru.netology.diploma.ui.post_fragments.PostsFeedFragment.Companion.textArg
import ru.netology.diploma.util.StringArg
import ru.netology.diploma.util.parseException
import ru.netology.diploma.viewmodel.EventViewModel

class EventsFeedFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: EventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEventFeedBinding.inflate(inflater, container, false)

        val gson = Gson()

        val adapter = EventsAdapter(object : OnInteractionListener<Event> {

            override fun onLike(event: Event) {
                if (event.likedByMe)
                    viewModel.dislikeById(event)
                else
                    viewModel.likeById(event)
            }

            override fun onEdit(event: Event) {
                findNavController().navigate(
                    R.id.action_postsFeedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = event.content
                    }
                )
                viewModel.edit(event)
            }

            override fun onRemove(event: Event) {
                viewModel.removeById(event.id)
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

            override fun onUnauthorized(event: Event) {
                findNavController().navigate(
                    R.id.action_global_authFragment
                )
            }

            override fun onSelect(event: Event) {
                findNavController().navigate(
                    R.id.action_eventsFeedFragment_to_selectedEventFragment,
                    Bundle().apply {
                        textArg = gson.toJson(event)
                    }
                )
            }

            override fun onAuthor(event: Event) {
                findNavController().navigate(
                    R.id.action_global_authorWallFragment,
                    Bundle().apply {
                        textArg = gson.toJson(event)
                    }
                )
            }

            override fun onUserIds(event: Event) {
                findNavController().navigate(
                    R.id.action_eventsFeedFragment_to_usersFragment,
                    Bundle().apply {
                        textArg = gson.toJson(event)
                    }
                )
            }

        }, MediaLifecycleObserver())

        binding.list.apply {
            this.adapter = adapter
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.addItemDecoration(
                DividerItemDecoration(
                    context,
                    layoutManager.orientation
                )
            )
        }

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            val errorMessage = parseException(it)
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry_loading) {
                    viewModel.loadEvents()
                }
                .show()
        }

        viewModel.authorization.observe(viewLifecycleOwner) {
            adapter.refresh()
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest {
                binding.swipeRefresh.isRefreshing =
                    it.refresh is LoadState.Loading

                binding.errorGroup.isVisible =
                    it.refresh is LoadState.Error
                            || it.append is LoadState.Error
                            || it.prepend is LoadState.Error
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }

        binding.fab.setOnClickListener {
            if (viewModel.isAuthorized)
                findNavController().navigate(R.id.action_eventsFeedFragment_to_newEventFragment)
            else
                findNavController().navigate(R.id.action_global_authFragment)
        }

        binding.postFeed.setOnClickListener {
            findNavController().navigate(R.id.action_eventsFeedFragment_to_postsFeedFragment)
        }

        return binding.root

    }

}