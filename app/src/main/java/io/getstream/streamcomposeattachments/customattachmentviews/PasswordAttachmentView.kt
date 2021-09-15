package io.getstream.streamcomposeattachments.customattachmentviews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.getstream.chat.android.compose.state.messages.attachments.AttachmentState
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.streamcomposeattachments.ui.theme.Background
import io.getstream.streamcomposeattachments.ui.theme.Teal200

@Composable
fun PasswordAttachmentView(attachmentState: AttachmentState) {
    var showPassword by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = attachmentState.messageItem.message.text,
        onValueChange = {

        },
        modifier = Modifier
            .fillMaxWidth()
            .background(Background)
            .padding(6.dp),
        enabled = false,
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        trailingIcon = {
            val image = if (showPassword)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            IconButton(onClick = {
                showPassword = !showPassword
            }) {
                Icon(imageVector  = image, null)
            }
        }

    )

}
