package io.getstream.streamcomposeattachments.customattachmentviews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.getstream.chat.android.compose.state.messages.attachments.AttachmentState
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
fun PasswordAttachmentView(attachmentState: AttachmentState) {
    var showPassword by remember { mutableStateOf(false) }
    val passwordAttachment = attachmentState.messageItem.message.attachments.first { it.type == "password" }
    Row(
        modifier = Modifier
            .padding(6.dp)
            .clip(ChatTheme.shapes.attachment)
            .background(Color.White)
    ) {
        OutlinedTextField(
            value = passwordAttachment.extraData["password"].toString(),
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .height(50.dp),
            enabled = false,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = {
                val image = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(imageVector = image, null)
                }
            }
        )
    }
}
