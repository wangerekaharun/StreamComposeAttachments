package io.getstream.streamcomposeattachments.customattachmentviews

import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.net.toUri
import io.getstream.chat.android.compose.state.messages.attachments.AttachmentState
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.streamcomposeattachments.R
import io.getstream.streamcomposeattachments.utils.PlayerWrapper

@Composable
fun AudioAttachmentView(attachmentState: AttachmentState) {
    // 1
    var playing by remember { mutableStateOf(false) }
    // 2
    val audioAttachment = attachmentState.messageItem.message.attachments.first { it.type == "audio" }
    // 3
    val player = PlayerWrapper(
        player = MediaPlayer.create(LocalContext.current, Uri.parse(audioAttachment.url)),
        onStop = { playing = false },
    )

    // 4
    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clip(ChatTheme.shapes.attachment)
            .background(Color.White)
    ) {
        val (iconButton, text) = createRefs()
        // 4
        IconButton(
            onClick = {
                playing = !playing
                if (playing) player.play() else player.stop()
            },
            modifier = Modifier
                .width(50.dp)
                .height(55.dp)
                .constrainAs(iconButton) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
        ) {
            Image(
                painter = painterResource(
                    when (playing) {
                        true -> R.drawable.ic_baseline_stop_circle_24
                        false -> R.drawable.ic_baseline_play_circle_filled_24
                    }
                ),
                contentDescription = "Play Icon",
            )
        }
        val fileName = audioAttachment.name ?: ""
        // 5
        Text(
            text = fileName,
            fontSize = 16.sp,
            modifier = Modifier
                .constrainAs(text) {
                    start.linkTo(iconButton.end)
                    top.linkTo(iconButton.top)
                    bottom.linkTo(iconButton.bottom)
                }
        )
    }
}
