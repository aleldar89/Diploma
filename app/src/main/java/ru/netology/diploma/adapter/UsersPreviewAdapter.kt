package ru.netology.diploma.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.diploma.databinding.CardUserBinding
import ru.netology.diploma.dto.UserResponse
import ru.netology.diploma.extensions.loadAvatar

class UsersPreviewAdapter(
    private val onInteractionListener: OnInteractionListener<UserResponse>
) : ListAdapter<UserResponse, UserViewHolder>(UserDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = CardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }
}

class UserViewHolder(
    private val binding: CardUserBinding,
    private val onInteractionListener: OnInteractionListener<UserResponse>
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: UserResponse) {
        binding.apply {
            name.text = user.name
            user.avatar?.let { avatar.loadAvatar(it) }

            userGroup.setOnClickListener {
                onInteractionListener.onAuthor(user)
            }
        }
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<UserResponse>() {
    override fun areItemsTheSame(oldItem: UserResponse, newItem: UserResponse): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }

        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: UserResponse, newItem: UserResponse): Boolean {
        return oldItem == newItem
    }
}