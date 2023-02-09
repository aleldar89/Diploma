package ru.netology.diploma.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.diploma.databinding.UserCardBinding
import ru.netology.diploma.dto.UserPreview
import ru.netology.diploma.extensions.loadAvatar

class UserPreviewAdapter(
    private val onInteractionListener: OnInteractionListener<UserPreview>
) : ListAdapter<UserPreview, UserViewHolder>(UserDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }
}

class UserViewHolder(
    private val binding: UserCardBinding,
    private val onInteractionListener: OnInteractionListener<UserPreview>
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: UserPreview) {
        binding.apply {
            name.text = user.name
            user.avatar?.let { avatar.loadAvatar(it) }

            userGroup.setOnClickListener {
                onInteractionListener.onChoose(user)
            }
        }
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<UserPreview>() {
    override fun areItemsTheSame(oldItem: UserPreview, newItem: UserPreview): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }

        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: UserPreview, newItem: UserPreview): Boolean {
        return oldItem == newItem
    }
}