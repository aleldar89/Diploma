package ru.netology.diploma.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.adapter.OnInteractionListener
import ru.netology.diploma.adapter.UsersPreviewAdapter
import ru.netology.diploma.databinding.FragmentUsersBinding
import ru.netology.diploma.dto.UserResponse
import ru.netology.diploma.util.StringArg
import ru.netology.diploma.viewmodel.UsersViewModel

@AndroidEntryPoint
class UsersFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
        const val AUTHOR_ID = "AUTHOR_ID"
    }

    private val viewModel: UsersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUsersBinding.inflate(inflater, container, false)

        val adapter = UsersPreviewAdapter(object : OnInteractionListener<UserResponse> {
            override fun onAuthor(user: UserResponse) {
                findNavController().navigate(
                    R.id.action_global_authorWallFragment,
                    Bundle().apply {
                        putInt(AUTHOR_ID, user.id)
                    }
                )
            }
        })

        binding.list.apply {
            this.adapter = adapter
        }

        viewModel.users.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return binding.root

    }
}