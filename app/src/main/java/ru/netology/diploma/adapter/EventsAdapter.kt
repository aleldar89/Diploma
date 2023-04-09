package ru.netology.diploma.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.diploma.R
import ru.netology.diploma.databinding.CardEventBinding
import ru.netology.diploma.dto.AttachmentType
import ru.netology.diploma.dto.Event
import ru.netology.diploma.extensions.createDate
import ru.netology.diploma.extensions.loadAvatar
import ru.netology.diploma.extensions.loadImage
import ru.netology.diploma.mediplayer.ExoPlayerLifecycleObserver
import ru.netology.diploma.mediplayer.MediaLifecycleObserver

class EventsAdapter(
    private val onInteractionListener: OnInteractionListener<Event>,
    private val onUserIdsListener: OnUserIdsListener,
    private val mediaObserver: MediaLifecycleObserver,
    private val exoObserver: ExoPlayerLifecycleObserver,
) : PagingDataAdapter<Event, EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = CardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val context = binding.root.context
        return EventViewHolder(
            binding,
            onInteractionListener,
            onUserIdsListener,
            mediaObserver,
            exoObserver,
            context
        )
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        event?.let {
            holder.bind(it)
        }
    }
}

class EventViewHolder(
    private val binding: CardEventBinding,
    private val onInteractionListener: OnInteractionListener<Event>,
    private val onUserIdsListener: OnUserIdsListener,
    private val mediaObserver: MediaLifecycleObserver,
    private val exoObserver: ExoPlayerLifecycleObserver,
    private val context: Context,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(event: Event) {
        binding.apply {

            event.authorAvatar?.let { authorAvatar.loadAvatar(it) }
            author.text = event.author
            authorJob.text = event.authorJob
            published.text = event.published.createDate()

            menu.isVisible = event.ownedByMe
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_item)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.edit -> {
                                onInteractionListener.onEdit(event)
                                true
                            }
                            R.id.remove -> {
                                onInteractionListener.onRemove(event)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            content.text = event.content
            content.setOnClickListener {
                onInteractionListener.onSelect(event)
            }

            if (!event.link.isNullOrEmpty()) {
                link.isVisible = true
                link.text = event.link
            } else {
                link.isVisible = false
            }

            if (event.attachment == null) {
                imageView.isVisible = false
                videoView.isVisible = false
                audioView.isVisible = false
            } else {
                when (event.attachment.type) {
                    AttachmentType.IMAGE -> imageView.apply {
                        isVisible = true
                        loadImage(event.attachment.url)
                    }

                    AttachmentType.VIDEO -> {
                        videoView.isVisible = true
                        exoObserver.apply {
                            val mediaItem = MediaItem.fromUri(Uri.parse(event.attachment.url))
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
                                    mediaPlayer?.setDataSource(event.attachment.url)
                                    this.play()
                                }
                            }
                        }
                    }
                }
            }

            like.isChecked = event.likedByMe
            like.setOnClickListener {
                onInteractionListener.onLike(event)
            }

            participate.isChecked = event.participatedByMe
            participate.setOnClickListener {
                onInteractionListener.onParticipate(event)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(event)
            }

            if (!event.likeOwnerIds.isNullOrEmpty()) {
                likeOwnerIds.isVisible = true
                likeOwnerIds.text = context.getString(
                    R.string.likes, event.likeOwnerIds.size
                )
                likeOwnerIds.setOnClickListener {
                    onUserIdsListener.onUserIds(event.likeOwnerIds)
                }
            } else {
                likeOwnerIds.isVisible = false
            }

            if (!event.speakerIds.isNullOrEmpty()) {
                speakerIds.isVisible = true
                speakerIds.text = context.getString(
                    R.string.speakers, event.speakerIds.size
                )
                speakerIds.setOnClickListener {
                    onUserIdsListener.onUserIds(event.speakerIds)
                }
            } else {
                speakerIds.isVisible = false
            }

            if (!event.participantsIds.isNullOrEmpty()) {
                participantsIds.isVisible = true
                participantsIds.text = context.getString(
                    R.string.participants, event.participantsIds.size
                )
                participantsIds.setOnClickListener {
                    onUserIdsListener.onUserIds(event.participantsIds)
                }
            } else {
                participantsIds.isVisible = false
            }

        }
    }
}

class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem == newItem
    }
}