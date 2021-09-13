package io.getstream.streamcomposeattachments.utils

import android.media.MediaRecorder

fun MediaRecorder.stopRecording() {
    this.apply {
        stop()
        release()
    }
}