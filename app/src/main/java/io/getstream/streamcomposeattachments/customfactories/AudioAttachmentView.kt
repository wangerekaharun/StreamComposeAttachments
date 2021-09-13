package io.getstream.streamcomposeattachments.customfactories

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import io.getstream.chat.android.compose.state.messages.attachments.AttachmentState
import io.getstream.chat.android.compose.ui.attachments.AttachmentFactory
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.streamcomposeattachments.R

@Composable
fun AudioAttachmentView() {
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
        val (iconButton, text) = createRefs()
        IconButton(onClick = { /*TODO*/ }) {
            Image(
                painter = painterResource(R.drawable.ic_baseline_play_circle_filled_24),
                contentDescription = "Play Icon",
                modifier = Modifier
                    .width(50.dp)
                    .height(55.dp)
                    .constrainAs(iconButton) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }
            )
        }
        
        Text(
            text = "Sep 2, 12:00",
            fontSize = 16.sp,
            modifier = Modifier
                .constrainAs(text) {
                    start.linkTo(iconButton.end)
                    top.linkTo(iconButton.top)
                    bottom.linkTo(iconButton.top)
                }
        )
    }

}

@Preview
@Composable
fun AudioAttachmentViewPreview() {
    ChatTheme {
        AudioAttachmentView()
    }
}