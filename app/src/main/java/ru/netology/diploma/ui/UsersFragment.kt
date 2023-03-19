package ru.netology.diploma.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.adapter.OnInteractionListener
import ru.netology.diploma.adapter.UsersPreviewAdapter
import ru.netology.diploma.databinding.FragmentUsersBinding
import ru.netology.diploma.dto.Post
import ru.netology.diploma.dto.UserPreview
import ru.netology.diploma.dto.UserResponse
import ru.netology.diploma.ui.post_fragments.PostsFeedFragment.Companion.textArg
import ru.netology.diploma.util.StringArg
import ru.netology.diploma.viewmodel.UsersViewModel

@AndroidEntryPoint
class UsersFragment : Fragment() {

    private val viewModel: UsersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUsersBinding.inflate(inflater, container, false)

        val adapter = UsersPreviewAdapter(object : OnInteractionListener<UserResponse> {
            override fun onAuthor(item: UserResponse) {
                //todo переход на стену пользователя
//                findNavController().navigate(
//                    R.id.action_global_authorWallFragment
//                )
            }
        })

        binding.list.apply {
            this.adapter = adapter
            val layoutManager = GridLayoutManager(context, 2)
            this.addItemDecoration(
                DividerItemDecoration(
                    context,
                    layoutManager.orientation
                )
            )
        }

        viewModel.users.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return binding.root

    }
}