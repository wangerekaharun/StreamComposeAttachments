package io.getstream.streamcomposeattachments.customattachmentviews

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.net.toUri
import io.getstream.chat.android.compose.state.messages.attachments.AttachmentState
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.streamcomposeattachments.R
import io.getstream.streamcomposeattachments.utils.PlayerUtils

@Composable
fun AudioAttachmentView(attachmentState: AttachmentState) {
    val context = LocalContext.current
    var playing by remember { mutableStateOf(false) }
    val message = attachmentState.messageItem.message.attachments.first { it.type == "audio" }
    val player = PlayerUtils(
        MediaPlayer.create(
            context,
            message.extraData["audiofile"].toString().toUri()
        )
    )
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clip(ChatTheme.shapes.attachment)
            .background(Color.White)
    ) {
        val (iconButton, text) = createRefs()
        IconButton(
            onClick = {
                if (!playing) {
                    playing = true
                    player.play()
                } else {
                    playing = false
                    player.stop()
                }
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
                painter = if (playing) painterResource(R.drawable.ic_baseline_stop_circle_24)
                else painterResource(R.drawable.ic_baseline_play_circle_filled_24),
                contentDescription = "Play Icon",
            )
        }
        val fileName = message.extraData["audiofile"].toString().toUri().lastPathSegment ?: ""
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