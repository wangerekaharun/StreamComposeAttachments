package io.getstream.streamcomposeattachments.utils

import android.media.MediaPlayer

class PlayerWrapper(private val player: MediaPlayer, private val onStop: () -> Unit) {
    init {
        player.setOnCompletionListener { onStop() }
    }

    fun play() {
        player.seekTo(0)
        player.start()
    }

    fun stop() {
        player.pause()
        onStop()
    }

    fun release() {
        player.release()
    }
}
