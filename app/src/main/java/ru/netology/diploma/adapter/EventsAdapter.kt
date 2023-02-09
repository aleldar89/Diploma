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
import ru.netology.diploma.databinding.CardEventBinding
import ru.netology.diploma.dto.AttachmentType
import ru.netology.diploma.dto.Event
import ru.netology.diploma.extensions.loadAvatar
import ru.netology.diploma.extensions.loadImage
import ru.netology.diploma.mediplayer.MediaLifecycleObserver
import ru.netology.diploma.util.StringArg

class EventsAdapter(
    private val onInteractionListener: OnInteractionListener<Event>,
    private val observer: MediaLifecycleObserver,
) : PagingDataAdapter<Event, EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = CardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onInteractionListener, observer)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        //TODO почему null-проверка
        event?.let {
            holder.bind(it)
        }
    }
}

class EventViewHolder(
    private val binding: CardEventBinding,
    private val onInteractionListener: OnInteractionListener<Event>,
    private val observer: MediaLifecycleObserver,
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    fun bind(event: Event) {
        binding.apply {

            event.authorAvatar?.let { authorAvatar.loadAvatar(it) }
            author.text = event.author
            authorJob.text = event.authorJob
            published.text = event.published

            menu.isVisible = event.ownedByMe
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
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

            event.attachment?.let {
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
                                mediaPlayer?.setDataSource(event.attachment.url)
                            }.play()
                        }
                    }

                    AttachmentType.VIDEO -> videoView.apply {
                        isVisible = true
                        //TODO возможно проблема с context
                        setMediaController(MediaController(context))
                        setVideoURI(
                            Uri.parse(event.attachment.url)
                        )
                        setOnPreparedListener { start() }
                        setOnCompletionListener { stopPlayback() }
                    }
                }
            }

            like.isChecked = event.likedByMe
            like.setOnClickListener {
                if (!event.ownedByMe)
                    onInteractionListener.onUnauthorized(event)
                else
                    onInteractionListener.onLike(event)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(event)
            }

            if (event.likeOwnerIds != null) {
                likeOwnerIds.isVisible = true
                //TODO превращение likeOwnerIds в аватары с именами аналогично ВК
                likeOwnerIds.text = event.likeOwnerIds.toString()
            }

            likeOwnerIds.setOnClickListener {
                onInteractionListener.onUserIds(event)
            }

            if (event.speakerIds != null) {
                speakerIds.isVisible = true
                //TODO превращение speakerIds в аватары с именами аналогично ВК
                speakerIds.text = event.speakerIds.toString()
            }

            speakerIds.setOnClickListener {
                onInteractionListener.onUserIds(event)
            }

            if (event.participantsIds != null) {
                participantsIds.isVisible = true
                //TODO превращение speakerIds в аватары с именами аналогично ВК
                participantsIds.text = event.participantsIds.toString()
            }

            participantsIds.setOnClickListener {
                onInteractionListener.onUserIds(event)
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