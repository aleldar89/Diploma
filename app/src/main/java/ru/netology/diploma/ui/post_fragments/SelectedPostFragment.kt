package ru.netology.diploma.ui.post_fragments

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
import ru.netology.diploma.adapter.OnInteractionListener
import ru.netology.diploma.adapter.PostViewHolder
import ru.netology.diploma.databinding.CardPostBinding
import ru.netology.diploma.dto.Post
import ru.netology.diploma.mediplayer.MediaLifecycleObserver
import ru.netology.diploma.util.StringArg
import ru.netology.diploma.viewmodel.PostViewModel

@AndroidEntryPoint
class SelectedPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = CardPostBinding.inflate(
            inflater,
            container,
            false
        )

        val gson = Gson()
        val post: Post = arguments?.textArg.let { gson.fromJson(it, Post::class.java) }

        val postViewHolder = PostViewHolder(
            binding,
            object : OnInteractionListener<Post> {
                override fun onLike(post: Post) {
                    if (post.likedByMe)
                        viewModel.dislikeById(post)
                    else
                        viewModel.likeById(post)
                }

                override fun onEdit(post: Post) {
                    findNavController().navigate(
                        R.id.action_selectedPostFragment_to_newPostFragment,
                        Bundle().apply {
                            textArg = post.content
                        }
                    )
                    viewModel.edit(post)
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                    findNavController().navigateUp()
                }

                override fun onShare(post: Post) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }

                    val shareIntent =
                        Intent.createChooser(intent, getString(R.string.chooser_share_post))
                    startActivity(shareIntent)
                }
            },
            MediaLifecycleObserver()
        )

        postViewHolder.bind(post)

        return binding.root
    }
}

//        viewModel.data.map { posts ->
//            lateinit var currentPost: Post
//            posts.map {
//                currentPost = if (it.id == post.id)
//                    it
//                else
//                    post
//            }
//            postViewHolder.bind(currentPost)
//        }