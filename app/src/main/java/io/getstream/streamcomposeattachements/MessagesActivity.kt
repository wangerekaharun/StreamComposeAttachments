package io.getstream.streamcomposeattachements

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.Attachment
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.livedata.ChatDomain

class MessagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val channelId = intent.getStringExtra(KEY_CHANNEL_ID)

        if (channelId == null) {
            finish()
            return
        }

        val attachment = Attachment(
            type = "paword",
            extraData = mutableMapOf("password" to "12345"),
        )
        val message = Message(
            cid = channelId,
            text = "Password",
            attachments = mutableListOf(attachment),
        )

        ChatDomain.instance().sendMessage(message = message).enqueue {result ->
            if (result.isSuccess) {
                Log.d("Password Attachment Sent Success",result.toString())
            } else {
                Log.d("Password Attachment Sent",result.error().message.toString())
            }

        }

        setContent {
            ChatTheme {
                MessagesScreen(
                    channelId = channelId,
                    messageLimit = 30,
                    onBackPressed = { finish() }
                )
            }
        }
    }

    companion object {
        private const val KEY_CHANNEL_ID = "channelId"

        fun getIntent(context: Context, channelId: String): Intent {
            return Intent(context, MessagesActivity::class.java).apply {
                putExtra(KEY_CHANNEL_ID, channelId)
            }
        }
    }
}