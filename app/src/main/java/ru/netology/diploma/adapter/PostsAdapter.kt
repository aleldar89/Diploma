package ru.netology.diploma.adapter

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
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
import ru.netology.diploma.mediplayer.ExoPlayerLifecycleObserver
import ru.netology.diploma.mediplayer.MediaLifecycleObserver
import ru.netology.diploma.util.StringArg

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener<Post>,
    private val onUserIdsListener: OnUserIdsListener,
    private val mediaObserver: MediaLifecycleObserver,
    private val exoObserver: ExoPlayerLifecycleObserver,
) : PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val context = binding.root.context
        return PostViewHolder(
            binding,
            onInteractionListener,
            onUserIdsListener,
            mediaObserver,
            exoObserver,
            context
        )
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
    private val onUserIdsListener: OnUserIdsListener,
    private val mediaObserver: MediaLifecycleObserver,
    private val exoObserver: ExoPlayerLifecycleObserver,
    private val context: Context,
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
            authorJob.text = post.authorJob
            published.text = post.published.createDate()
            
            menu.isVisible = post.ownedByMe
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_item)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
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
            } else {
                link.isVisible = false
            }

            if (post.attachment == null) {
                imageView.isVisible = false
                videoView.isVisible = false
                audioView.isVisible = false
            } else {
                when (post.attachment.type) {
                    AttachmentType.IMAGE -> imageView.apply {
                        isVisible = true
                        loadImage(post.attachment.url)
                    }

                    AttachmentType.VIDEO -> {
                        videoView.isVisible = true
                        exoObserver.apply {
                            val mediaItem = MediaItem.fromUri(Uri.parse(post.attachment.url))
                            play(videoView, mediaItem)
                        }
                    }

                    AttachmentType.AUDIO -> {
                        audioView.isVisible = true
                        mediaObserver.apply {
                            playView.setOnClickListener {
                                if (mediaPlayer?.isPlaying == true) {
                                    playView.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
                                    mediaPlayer?.pause()
                                } else {
                                    playView.setImageResource(R.drawable.ic_baseline_stop_circle_24)
                                    mediaPlayer?.stop()
                                    mediaPlayer?.reset()
                                    mediaPlayer?.setDataSource(post.attachment.url)
                                    this.play()

//                                    seekBar.max = 100

//                                    seekBar.setOnSeekBarChangeListener(seekBarListener)

//                                    seekBar.post {
//                                        while (mediaPlayer?.isPlaying == true) {
//                                            val duration = mediaPlayer?.duration ?: 0
//                                            val currentPosition = mediaPlayer?.currentPosition ?: 0
//                                            seekBar.progress =
//                                                (currentPosition.toFloat() / duration.toFloat() * 100).toInt()
//                                        }
//                                    }

//                                    seekBar.post {
//                                        if (mediaPlayer?.isPlaying == true) {
//                                            val duration = mediaPlayer?.duration ?: 0
//                                            val currentPosition = mediaPlayer?.currentPosition ?: 0
//                                            seekBar.progress =
//                                                (currentPosition.toFloat() / duration.toFloat() * 100).toInt()
//                                        }
//                                    }

                                }
                            }
                        }
                    }
                }
            }

            like.isChecked = post.likedByMe
            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            if (!post.likeOwnerIds.isNullOrEmpty()) {
                likeOwnerIds.isVisible = true
                likeOwnerIds.text = context.getString(
                    R.string.likes, post.likeOwnerIds.size
                )
                likeOwnerIds.setOnClickListener {
                    onUserIdsListener.onUserIds(post.likeOwnerIds)
                }
            } else {
                likeOwnerIds.isVisible = false
            }

            if (!post.mentionIds.isNullOrEmpty()) {
                mentionIds.isVisible = true
                mentionIds.text = context.getString(
                    R.string.mentions, post.mentionIds.size
                )
                mentionIds.setOnClickListener {
                    onUserIdsListener.onUserIds(post.mentionIds)
                }
            } else {
                mentionIds.isVisible = false
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