package ru.netology.diploma.mediplayer

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import javax.inject.Inject

class ExoPlayerLifecycleObserver @Inject constructor(
    private val context: Context
) : LifecycleEventObserver {
    var exoPlayer: ExoPlayer? = null

    fun play(playerView: PlayerView, mediaItem: MediaItem) {
        exoPlayer = ExoPlayer.Builder(context).build().also {
            playerView.player = it
            it.setMediaItem(mediaItem)
            it.playWhenReady = false
            it.seekTo(0, 0L)
            it.prepare()
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE -> exoPlayer?.pause()
            Lifecycle.Event.ON_STOP -> {
                exoPlayer?.release()
                exoPlayer = null
            }
            Lifecycle.Event.ON_DESTROY -> source.lifecycle.removeObserver(this)
            else -> Unit
        }
    }
}