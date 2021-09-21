package io.getstream.streamcomposeattachments.utils

import androidx.compose.runtime.Composable
import io.getstream.chat.android.client.models.Attachment.UploadState.Success
import io.getstream.chat.android.compose.ui.attachments.AttachmentFactory
import io.getstream.chat.android.core.ExperimentalStreamChatApi
import io.getstream.streamcomposeattachments.customattachmentviews.AudioAttachmentView
import io.getstream.streamcomposeattachments.customattachmentviews.PasswordAttachmentView

@ExperimentalStreamChatApi
val customAttachmentFactories: List<AttachmentFactory> = listOf(
    AttachmentFactory(
        canHandle = { attachments -> attachments.any { it.type == "audio" && it.uploadState in setOf(Success, null) } },
        content = @Composable { AudioAttachmentView(it) }
    ),
    AttachmentFactory(
        canHandle = { attachments -> attachments.any { it.type == "password" } },
        content = @Composable { PasswordAttachmentView(it) }
    )
)
