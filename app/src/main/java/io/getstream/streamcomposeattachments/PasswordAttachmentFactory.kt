package io.getstream.streamcomposeattachments

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.getstream.chat.android.compose.state.messages.attachments.AttachmentState
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
fun PasswordAttachmentFactory(attachmentState: AttachmentState) {
    Text(
        text = "Password",
        modifier = Modifier.fillMaxWidth()
    )

}

@Preview
@Composable
fun PasswordAttachmentFactoryPreview() {
    ChatTheme {

    }
}