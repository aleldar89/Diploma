package ru.netology.diploma.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.diploma.R
import ru.netology.diploma.adapter.OnInteractionListener
import ru.netology.diploma.adapter.OnUserIdsListener
import ru.netology.diploma.adapter.PostsAdapter
import ru.netology.diploma.databinding.FragmentWallBinding
import ru.netology.diploma.dto.Post
import ru.netology.diploma.extensions.createToast
import ru.netology.diploma.extensions.loadAvatar
import ru.netology.diploma.mediplayer.MediaLifecycleObserver
import ru.netology.diploma.ui.post_fragments.PostsFeedFragment
import ru.netology.diploma.ui.post_fragments.PostsFeedFragment.Companion.textArg
import ru.netology.diploma.ui.post_fragments.SelectedPostFragment.Companion.textArg
import ru.netology.diploma.util.StringArg
import ru.netology.diploma.util.parseException
import ru.netology.diploma.viewmodel.AuthorWallViewModel

@AndroidEntryPoint
class AuthorWallFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
        const val AUTHOR_ID = "AUTHOR_ID"
    }

    private val viewModel: AuthorWallViewModel by viewModels()

//    override fun onCreate(savedInstanceState: Bundle?) {
//        viewModel.clearPosts()
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentWallBinding.inflate(inflater, container, false)
        val adapter = PostsAdapter(
            object : OnInteractionListener<Post> {},
            object : OnUserIdsListener {},
            MediaLifecycleObserver()
        )

        val gson = Gson()
        val post: Post = arguments?.textArg.let { gson.fromJson(it, Post::class.java) }

        post.authorAvatar?.let { binding.authorAvatar.loadAvatar(it) }
        binding.author.text = post.author

//        binding.author.text = viewModel.userResponse.value?.name
//        viewModel.userResponse.value?.avatar?.let { binding.authorAvatar.loadAvatar(it) }

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

        binding.authorAvatar.setOnClickListener {
            findNavController().navigate(
                R.id.action_authorWallFragment_to_userJobFeedFragment,
                Bundle().apply {
                    putInt(AUTHOR_ID, checkNotNull(viewModel.userResponse.value?.id))
                })
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
                    viewModel.loadPosts()
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