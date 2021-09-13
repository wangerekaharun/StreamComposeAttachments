package io.getstream.streamcomposeattachments.customattachmentviews

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.getstream.chat.android.compose.state.messages.attachments.AttachmentState
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
fun PasswordAttachmentView(attachmentState: AttachmentState) {
    Text(
        text = "Custom Password view",
        modifier = Modifier.fillMaxWidth()
    )

}

@Preview
@Composable
fun PasswordAttachmentViewPreview() {
    ChatTheme {

    }
}
