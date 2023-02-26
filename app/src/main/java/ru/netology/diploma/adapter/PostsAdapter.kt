package ru.netology.diploma.adapter

import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.diploma.R
import ru.netology.diploma.databinding.CardPostBinding
import ru.netology.diploma.dto.AttachmentType
import ru.netology.diploma.dto.Post
import ru.netology.diploma.extensions.createDate
import ru.netology.diploma.extensions.loadAvatar
import ru.netology.diploma.extensions.loadImage
import ru.netology.diploma.mediplayer.MediaLifecycleObserver
import ru.netology.diploma.util.StringArg

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener<Post>,
    private val observer: MediaLifecycleObserver,
) : PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener, observer)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        post?.let {
            holder.bind(it)
        }
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener<Post>,
    private val observer: MediaLifecycleObserver,
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    fun bind(post: Post) {
        binding.apply {

            post.authorAvatar?.let { authorAvatar.loadAvatar(it) }

            authorAvatar.setOnClickListener {
                onInteractionListener.onAuthor(post)
            }

            author.text = post.author

            author.setOnClickListener {
                onInteractionListener.onAuthor(post)
            }

            authorJob.text = post.authorJob
            published.text = post.published.createDate()

            menu.isVisible = post.ownedByMe

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_item)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            content.text = post.content

            content.setOnClickListener {
                onInteractionListener.onSelect(post)
            }

            if (!post.link.isNullOrEmpty()) {
                link.isVisible = true
                link.text = post.link
            }

            post.attachment?.let {
                when (it.type) {
                    AttachmentType.IMAGE -> imageView.apply {
                        isVisible = true
                        loadImage(it.url)
                    }

                    AttachmentType.VIDEO -> videoView.apply {
                        isVisible = true
                        //TODO возможно проблема с context
                        setMediaController(MediaController(context))
                        setVideoURI(
                            Uri.parse(post.attachment.url)
                        )
                        seekTo(1)
                        setOnPreparedListener { start() }
                        setOnCompletionListener { stopPlayback() }
                    }

                    AttachmentType.AUDIO -> playView.apply {
                        isVisible = true
                        setOnClickListener {
                            observer.apply {
                                mediaPlayer?.stop()
                                mediaPlayer?.reset()
                                mediaPlayer?.setDataSource(post.attachment.url)
                            }.play()
                        }
                    }
                }
            }

            like.isChecked = post.likedByMe
            like.setOnClickListener {
                if (!post.ownedByMe)
                    onInteractionListener.onUnauthorized(post)
                else
                    onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            if (!post.likeOwnerIds.isNullOrEmpty()) {
                likeOwnerIds.isVisible = true
                likeOwnerIds.text = "Likes: ${post.likeOwnerIds.size}"
//                likeOwnerIds.text = Resources
//                    .getSystem()
//                    .getString(R.string.likes, post.likeOwnerIds.size)
            }

            likeOwnerIds.setOnClickListener {
                onInteractionListener.onUserIds(post)
            }

            if (!post.mentionIds.isNullOrEmpty()) {
                mentionIds.isVisible = true
                mentionIds.text = "Mentions: ${post.mentionIds.size}"
            }

            mentionIds.setOnClickListener {
                onInteractionListener.onUserIds(post)
            }

        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }

        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}