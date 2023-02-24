package ru.netology.diploma.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.adapter.OnInteractionListener
import ru.netology.diploma.adapter.UsersPreviewAdapter
import ru.netology.diploma.databinding.FragmentUsersBinding
import ru.netology.diploma.dto.Post
import ru.netology.diploma.dto.UserPreview
import ru.netology.diploma.dto.UserResponse
import ru.netology.diploma.ui.AuthorWallFragment.Companion.textArg
import ru.netology.diploma.util.StringArg
import ru.netology.diploma.viewmodel.UsersViewModel

@AndroidEntryPoint
class UsersFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: UsersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUsersBinding.inflate(inflater, container, false)

        val gson = Gson()
        val post: Post = arguments?.textArg.let { gson.fromJson(it, Post::class.java) }
        post.likeOwnerIds?.let { viewModel.getUsersById(it) } //todo должен присылаться сразу List

        val adapter = UsersPreviewAdapter(object : OnInteractionListener<UserResponse> {
            override fun onAuthor(item: UserResponse) {
                //todo можем переправить UserResponse, но стена автора принимает Post, что делать?
            }

        })

        binding.list.adapter = adapter

        viewModel.users.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return binding.root

    }
}