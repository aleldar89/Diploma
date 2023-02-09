package ru.netology.diploma.adapter

import android.net.Uri
import android.os.Bundle
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
        //TODO почему null-проверка
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
            author.text = post.author
            authorJob.text = post.authorJob
            published.text = post.published

            menu.isVisible = post.ownedByMe
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
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

            post.attachment?.let {
                when (it.type) {
                    //TODO возможно не apply
                    AttachmentType.IMAGE -> imageView.apply {
                        isVisible = true
                        loadImage(it.url)
                    }

                    AttachmentType.AUDIO -> playView.apply {
                        isVisible = true
                        //TODO возможно проблема с нажатием view, а не playView
                        setOnClickListener {
                            observer.apply {
                                mediaPlayer?.stop()
                                mediaPlayer?.release()
                                mediaPlayer?.setDataSource(post.attachment.url)
                            }.play()
                        }
                    }

                    AttachmentType.VIDEO -> videoView.apply {
                        isVisible = true
                        //TODO возможно проблема с context
                        setMediaController(MediaController(context))
                        setVideoURI(
                            Uri.parse(post.attachment.url)
                        )
                        setOnPreparedListener { start() }
                        setOnCompletionListener { stopPlayback() }
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

            if (post.likeOwnerIds != null) {
                likeOwnerIds.isVisible = true
                //TODO превращение likeOwnerIds в аватары с именами аналогично ВК
                likeOwnerIds.text = post.likeOwnerIds.toString()
            }

            likeOwnerIds.setOnClickListener {
                onInteractionListener.onUserIds(post)
            }

            if (post.mentionIds != null) {
                mentionIds.isVisible = true
                //TODO превращение mentionIds в аватары с именами аналогично ВК
                mentionIds.text = post.mentionIds.toString()
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