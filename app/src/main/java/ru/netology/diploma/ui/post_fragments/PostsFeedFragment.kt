package ru.netology.diploma.ui.post_fragments

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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.diploma.R
import ru.netology.diploma.adapter.OnInteractionListener
import ru.netology.diploma.adapter.OnUserIdsListener
import ru.netology.diploma.adapter.PostsAdapter
import ru.netology.diploma.databinding.FragmentPostFeedBinding
import ru.netology.diploma.dto.Post
import ru.netology.diploma.mediplayer.MediaLifecycleObserver
import ru.netology.diploma.util.StringArg
import ru.netology.diploma.util.parseException
import ru.netology.diploma.viewmodel.PostViewModel

@AndroidEntryPoint
class PostsFeedFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
        const val AUTHOR_ID = "AUTHOR_ID"
        const val ID_ARRAY = "ID_ARRAY"
    }

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostFeedBinding.inflate(inflater, container, false)
        val gson = Gson()

        val adapter = PostsAdapter(
            object : OnInteractionListener<Post> {

                override fun onLike(post: Post) {
                    if (viewModel.isAuthorized) {
                        if (post.likedByMe)
                            viewModel.dislikeById(post)
                        else
                            viewModel.likeById(post)
                    } else {
                        findNavController().navigate(R.id.action_global_authFragment)
                    }
                }

                override fun onEdit(post: Post) {
                    findNavController().navigate(
                        R.id.action_global_newPostFragment,
                        Bundle().apply {
                            textArg = post.content
                        }
                    )
                    viewModel.edit(post)
                }


                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onShare(post: Post) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }

                    val shareIntent =
                        Intent.createChooser(intent, getString(R.string.share_post))
                    startActivity(shareIntent)
                }

                override fun onUnauthorized(post: Post) {
                    findNavController().navigate(R.id.action_global_authFragment)
                }

                override fun onSelect(post: Post) {
                    findNavController().navigate(
                        R.id.action_global_selectedPostFragment,
                        Bundle().apply {
                            textArg = gson.toJson(post)
                        }
                    )
                }

                override fun onAuthor(post: Post) {
                    findNavController().navigate(
                        R.id.action_global_authorWallFragment,
                        Bundle().apply {
                            putInt(AUTHOR_ID, post.authorId)
                        }
                    )
                }
            },
            object : OnUserIdsListener {
                override fun onUserIds(list: List<Int>) {
                    findNavController().navigate(
                        R.id.action_global_usersFragment,
                        Bundle().apply {
                            putIntArray(ID_ARRAY, list.toIntArray())
                        }
                    )
                }
            },
            MediaLifecycleObserver(),
        )

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
                    viewModel.loadPosts()
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
                findNavController().navigate(R.id.action_global_newPostFragment)
            else
                findNavController().navigate(R.id.action_global_authFragment)
        }

        return binding.root

    }

}