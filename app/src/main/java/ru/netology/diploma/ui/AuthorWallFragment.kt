package ru.netology.diploma.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.diploma.R
import ru.netology.diploma.adapter.OnInteractionListener
import ru.netology.diploma.adapter.PostsAdapter
import ru.netology.diploma.databinding.FragmentWallBinding
import ru.netology.diploma.dto.Post
import ru.netology.diploma.extensions.loadAvatar
import ru.netology.diploma.mediplayer.MediaLifecycleObserver
import ru.netology.diploma.ui.SelectedPostFragment.Companion.textArg
import ru.netology.diploma.util.StringArg
import ru.netology.diploma.util.parseException
import ru.netology.diploma.viewmodel.AuthorWallViewModel

@AndroidEntryPoint
class AuthorWallFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: AuthorWallViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentWallBinding.inflate(inflater, container, false)
        val adapter = PostsAdapter(object : OnInteractionListener<Post> {}, MediaLifecycleObserver())

        val gson = Gson()
        val post: Post = arguments?.textArg.let { gson.fromJson(it, Post::class.java) }

        post.authorAvatar?.let { binding.authorAvatar.loadAvatar(it) }
        binding.author.text = post.author

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            val errorMessage = parseException(it)
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry_loading) {
                    viewModel.loadPosts(post.authorId)
                }
                .show()
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

        return binding.root

    }
}