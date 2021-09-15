package io.getstream.streamcomposeattachments.utils

import android.media.MediaPlayer

class RecordingUtils(private val player: MediaPlayer){

    fun play() {
        player.start()
    }

    fun stop() {
        if(player.isPlaying){
            player.stop()
            player.reset()
            player.release()
        }
    }

    val MediaPlayer.seconds:Int
        get() {
            return this.duration / 1000
        }

    val MediaPlayer.currentSeconds:Int
        get() {
            return this.currentPosition/1000
        }

}
