package io.getstream.streamcomposeattachments.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.compose.ui.attachments.*
import io.getstream.chat.android.compose.ui.channel.ChannelsScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.core.ExperimentalStreamChatApi
import io.getstream.chat.android.livedata.ChatDomain
import io.getstream.streamcomposeattachments.R
import io.getstream.streamcomposeattachments.customattachmentviews.AudioAttachmentView
import io.getstream.streamcomposeattachments.customattachmentviews.PasswordAttachmentView

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalStreamChatApi
class ChannelsScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val client = ChatClient.Builder("b67pax5b2wdq", applicationContext)
            .logLevel(ChatLogLevel.ALL)
            .build()
        ChatDomain.Builder(client, applicationContext).build()

        val defaultFactories = StreamAttachmentFactories.defaultFactories()

        val user = User(
            id = "tutorial-droid",
            extraData = mutableMapOf(
                "name" to "Tutorial Droid",
                "image" to "https://bit.ly/2TIt8NR",
            ),
        )

        client.connectUser(
            user = user,
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoidHV0b3JpYWwtZHJvaWQifQ.NhEr0hP9W9nwqV7ZkdShxvi02C5PR7SJE7Cs4y7kyqg"
        ).enqueue()

        setContent {
            ChatTheme(attachmentFactories = defaultFactories) {
                ChannelsList {
                    finish()
                }
            }
        }
    }
}

@ExperimentalStreamChatApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun ChannelsList(action: () -> Unit) {
    val context = LocalContext.current

    ChannelsScreen(
        title = stringResource(id = R.string.app_name),
        onItemClick = { channel ->
            context.startActivity(CustomMessageScreen.getIntent(context, channel.cid))
        },
        onBackPressed = { action.invoke() }
    )
}