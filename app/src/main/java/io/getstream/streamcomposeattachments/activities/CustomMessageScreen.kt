package io.getstream.streamcomposeattachments.activities

import android.Manifest
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.Attachment
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.compose.state.messages.Thread
import io.getstream.chat.android.compose.ui.attachments.StreamAttachmentFactories
import io.getstream.chat.android.compose.ui.messages.attachments.AttachmentsPicker
import io.getstream.chat.android.compose.ui.messages.composer.MessageComposer
import io.getstream.chat.android.compose.ui.messages.composer.components.MessageInput
import io.getstream.chat.android.compose.ui.messages.header.MessageListHeader
import io.getstream.chat.android.compose.ui.messages.list.MessageList
import io.getstream.chat.android.compose.ui.messages.overlay.SelectedMessageOverlay
import io.getstream.chat.android.compose.ui.messages.overlay.defaultMessageOptions
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.messages.AttachmentsPickerViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessageComposerViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import io.getstream.chat.android.core.ExperimentalStreamChatApi
import io.getstream.chat.android.offline.ChatDomain
import io.getstream.streamcomposeattachments.R
import io.getstream.streamcomposeattachments.ui.StateViewModel
import io.getstream.streamcomposeattachments.utils.customAttachmentFactories
import java.io.IOException


@ExperimentalStreamChatApi
class CustomMessageScreen : AppCompatActivity() {
    private var channelId = ""
    private val factory by lazy {
        MessagesViewModelFactory(
            context = this,
            clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager,
            chatClient = ChatClient.instance(),
            chatDomain = ChatDomain.instance(),
            channelId = intent.getStringExtra(KEY_CHANNEL_ID) ?: "",
            enforceUniqueReactions = true,
            messageLimit = 40
        )
    }

    private val listViewModel by viewModels<MessageListViewModel>(factoryProducer = { factory })
    private val attachmentsPickerViewModel by viewModels<AttachmentsPickerViewModel>(factoryProducer = { factory })
    private val composerViewModel by viewModels<MessageComposerViewModel>(factoryProducer = { factory })
    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var isRecordingState: Boolean = false
    private val stateViewModel by viewModels<StateViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channelId = intent.getStringExtra(KEY_CHANNEL_ID) ?: return
        mediaRecorder = MediaRecorder()
        output = this.getExternalFilesDir(null)?.absolutePath + "/audio.mp3"

        val defaultFactories = StreamAttachmentFactories.defaultFactories()

        setContent {
            ChatTheme(
                attachmentFactories = customAttachmentFactories + defaultFactories) {
                CustomUi { onBackPressed() }
            }
        }
    }

    @Composable
    fun CustomUi(action: () -> Unit) {
        val isShowingAttachments = attachmentsPickerViewModel.isShowingAttachments
        val selectedMessage = listViewModel.currentMessagesState.selectedMessage
        val user by listViewModel.user.collectAsState()

        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    CustomAudioComposer()
                },
                content = {
                    Column(modifier = Modifier.fillMaxSize()) {
                        MessageListHeader(
                            listViewModel.channel,
                            user,
                            true,
                            listViewModel.messageMode,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            onBackPressed = { action.invoke() },
                            onHeaderActionClick = { },
                        )

                        MessageList(
                            modifier = Modifier
                                .padding(it)
                                .background(ChatTheme.colors.appBackground),
                            viewModel = listViewModel,
                            onThreadClick = { message ->
                                composerViewModel.setMessageMode(Thread(message))
                                listViewModel.openMessageThread(message)
                            }
                        )
                    }
                }
            )

            if (isShowingAttachments) {
                AttachmentsPicker(
                    attachmentsPickerViewModel = attachmentsPickerViewModel,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .height(350.dp),
                    onAttachmentsSelected = { attachments ->
                        attachmentsPickerViewModel.changeAttachmentState(false)
                        composerViewModel.addSelectedAttachments(attachments)
                    },
                    onDismiss = {
                        attachmentsPickerViewModel.changeAttachmentState(false)
                        attachmentsPickerViewModel.dismissAttachments()
                    }
                )
            }

            if (selectedMessage != null) {
                SelectedMessageOverlay(
                    messageOptions = defaultMessageOptions(
                        selectedMessage,
                        user,
                        listViewModel.isInThread
                    ),
                    message = selectedMessage,
                    onMessageAction = { action ->
                        composerViewModel.performMessageAction(action)
                        listViewModel.performMessageAction(action)
                    },
                    onDismiss = { listViewModel.removeOverlay() }
                )
            }
        }
    }

    @Composable
    fun CustomAudioComposer() {
        val buttonState by stateViewModel.isRecording.collectAsState()
        MessageComposer(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            viewModel = composerViewModel,
            integrations = {
                Row {
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(35.dp)
                            .height(35.dp)
                            .padding(4.dp),
                        content = {
                            Icon(
                                imageVector = Icons.Default.Attachment,
                                contentDescription = null,
                                tint = ChatTheme.colors.textLowEmphasis,
                            )
                        },
                        onClick = {
                            attachmentsPickerViewModel.changeAttachmentState(true)
                        }
                    )
                    IconButton(
                        onClick = {
                            sendPasswordAttachmentMessage()
                        },
                        modifier = Modifier
                            .width(35.dp)
                            .height(35.dp)
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = ChatTheme.colors.textLowEmphasis,
                        )
                    }

                    IconButton(
                        onClick = {
                            if (!buttonState) {
                                checkPermissions()
                            } else {
                                stopRecording()
                            }
                        },
                        modifier = Modifier
                            .width(35.dp)
                            .height(35.dp)
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = if (buttonState) {
                                Icons.Default.Stop
                            } else Icons.Default.Mic,
                            contentDescription = null,
                            tint = if (buttonState) ChatTheme.colors.errorAccent else ChatTheme.colors.textLowEmphasis,
                        )
                    }
                }
            },
            input = {
                MessageInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(7f)
                        .padding(start = 8.dp),
                    value = composerViewModel.input,
                    attachments = composerViewModel.selectedAttachments,
                    activeAction = composerViewModel.activeAction,
                    onValueChange = { composerViewModel.setMessageInput(it) },
                    onAttachmentRemoved = { composerViewModel.removeSelectedAttachment(it) },
                    label = {
                        Text(
                            text = getString(R.string.text_input_label)
                        )
                    }
                )
            }
        )
    }

    private fun sendPasswordAttachmentMessage() {
        val attachment = Attachment(
            type = "password",
            extraData = mutableMapOf("password" to "12345"),
        )
        val message = Message(
            cid = channelId,
            attachments = mutableListOf(attachment),
        )

        ChatDomain.instance().sendMessage(message = message).enqueue { result ->
            if (result.isSuccess) {
                Log.d(
                    "Password Attachment Sent Success",
                    result.data().attachments.toString()
                )
            } else {
                Log.d(
                    "Password Attachment Sent",
                    result.error().message.toString()
                )
            }

        }
    }

    private fun checkPermissions() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO),
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                setupMediaRecorder()
                startRecording()
            }
            else -> {
                requestAudioPermissions()
            }
        }
    }

    private fun setupMediaRecorder() {
        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(output)
        }
    }

    private fun startRecording() {
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            isRecordingState = true
            stateViewModel.updateRecordingState(true)
            Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        if (isRecordingState) {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            isRecordingState = false
            stateViewModel.updateRecordingState(false)
            sendAudioAttachment()
        } else {
            Toast.makeText(this, "You are not recording right now!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendAudioAttachment() {
        stateViewModel.sendAttachment(channelId, output.toString())
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            // Permissions Granted
        }

    private fun requestAudioPermissions() {
        requestMultiplePermissions.launch(
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,

                )
        )
    }

    companion object {
        private const val KEY_CHANNEL_ID = "channelId"

        fun getIntent(context: Context, channelId: String): Intent {
            return Intent(context, CustomMessageScreen::class.java).apply {
                putExtra(KEY_CHANNEL_ID, channelId)
            }
        }
    }
}