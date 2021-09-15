package io.getstream.streamcomposeattachments.utils

import android.media.MediaPlayer

class PlayerUtils(private var player: MediaPlayer?) {

    fun play() {
        player?.start()
    }

    fun stop() {
        player?.release()
        player = null
    }
}
