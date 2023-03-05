package ru.netology.diploma.adapter

import android.content.Context
import android.content.res.Resources
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
import ru.netology.diploma.databinding.CardEventBinding
import ru.netology.diploma.dto.AttachmentType
import ru.netology.diploma.dto.Event
import ru.netology.diploma.extensions.createDate
import ru.netology.diploma.extensions.loadAvatar
import ru.netology.diploma.extensions.loadImage
import ru.netology.diploma.mediplayer.MediaLifecycleObserver
import ru.netology.diploma.util.StringArg

class EventsAdapter(
    private val onInteractionListener: OnInteractionListener<Event>,
    private val onUserIdsListener: OnUserIdsListener,
    private val observer: MediaLifecycleObserver,
) : PagingDataAdapter<Event, EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = CardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val context = binding.root.context
        return EventViewHolder(binding, onInteractionListener, onUserIdsListener, observer, context)
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
    private val observer: MediaLifecycleObserver,
    private val context: Context,
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

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
                            R.id.remove -> {
                                onInteractionListener.onRemove(event)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(event)
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
            }


            event.attachment?.let {
                when (it.type) {
                    AttachmentType.IMAGE -> imageView.apply {
                        isVisible = true
                        loadImage(it.url)
                    }

                    AttachmentType.VIDEO -> videoView.apply {
                        isVisible = true
                        setMediaController(MediaController(context))
                        setVideoURI(
                            Uri.parse(event.attachment.url)
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
                                mediaPlayer?.setDataSource(event.attachment.url)
                            }.play()
                        }
                    }
                }
            }

            like.isChecked = event.likedByMe
            like.setOnClickListener {
                onInteractionListener.onLike(event)
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
            }

            if (!event.speakerIds.isNullOrEmpty()) {
                speakerIds.isVisible = true
                speakerIds.text = context.getString(
                    R.string.speakers, event.speakerIds.size
                )
                speakerIds.setOnClickListener {
                    onUserIdsListener.onUserIds(event.speakerIds)
                }
            }

            if (!event.participantsIds.isNullOrEmpty()) {
                participantsIds.isVisible = true
                participantsIds.text = context.getString(
                    R.string.participants, event.participantsIds.size
                )
                participantsIds.setOnClickListener {
                    onUserIdsListener.onUserIds(event.participantsIds)
                }
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