package io.getstream.streamcomposeattachments.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import io.getstream.chat.android.client.models.Attachment
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.offline.ChatDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StateViewModel : ViewModel() {
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    fun updateRecordingState(isRecording: Boolean) {
        _isRecording.value = isRecording
    }

    fun sendAttachment(channelId: String, output: String) {
        val attachment = Attachment(
            type = "audio",
            extraData = mutableMapOf("audiofile" to output),
        )
        val message = Message(
            cid = channelId,
            attachments = mutableListOf(attachment),
        )

        ChatDomain.instance().sendMessage(message = message).enqueue { result ->
            if (result.isSuccess) {
                Log.d("Audio Attachment Sent Success", result.data().attachments.toString())
            } else {
                Log.d("Audio Attachment Sent", result.error().message.toString())
            }

        }
    }
}