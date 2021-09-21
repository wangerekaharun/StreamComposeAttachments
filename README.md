# Custom Attachments in Stream Compose SDK
## Introduction 

Stream recently [announced](https://getstream.io/blog/jetpack-compose-sdk/) their Jetpack compose SDK which is currently in beta.  This follows the announcemnt of the stable verison of Jetpack Compose.

In this tutorial you'll learn

- AttachmentFactory and creating a custom Attachment Factory.
- Customizing Stream [Jetpack Compose UI Components](https://getstream.io/chat/docs/sdk/android/compose/overview/)
- Sending custom files as attachments.
- Adding previews for your custom attachment.

**Note:** You can try out the new SDK by checking out the [Jetpack Compose Chat Tutorial](https://getstream.io/chat/compose/tutorial/) 

Stream Chat SDK supports a number of attachments by default for example images, urls, gifs and videos. With the XML UI components, you could create custom `AttachmentViewFactory` class that would be used to render your attachment preview. The good news is even with the Composec components, you still have this abilty :]

Let's start by getting to know what is an Attachment Factory.

## Introduction to `AttachmentFactory`

This is a class that allows you to build and render your attachments to the list of messages. 

## Examples of Default `AttachmentFactory`

As earlier mentioned, the Stream Chat SDK provides you with a number of factories to handle image, video, URL and GIF attachments.  

## Creating Your Own Attachment Factory

For you to created your own attachment factory, you'll need:

- A view to show the attachment
- Have a new `AttachmentFactory` that checks for your custom attachment and specify how to render the attachment.

You'll be learning how to create this in the next section.

## Custom Password Attachment

You have this attachment that you'd want to send to a certain channel:

```kotlin
val attachment = Attachment(
    type = "password",
    extraData = mutableMapOf("password" to "12345"),
)
val message = Message(
    cid = channelId,
    attachments = mutableListOf(attachment),
)
```

This is a message with an attachment which contains a password. The password is passed through the `extraData` attribute. By default, the SDK doesn't know how to render this attachment.

To render this, first create a view that shows your password. In this case it'll be a composable. Here's how the code looks like:

```kotlin
// 1
@Composable
// 2
fun PasswordAttachmentView(attachmentState: AttachmentState) {
    // 3
    var showPassword by remember { mutableStateOf(false) }
    // 4
    val passwordAttachment = attachmentState.messageItem.message.attachments.first { it.type == "password" }
  
    Row(
        modifier = Modifier
            .padding(6.dp)
            .clip(ChatTheme.shapes.attachment)
            .background(Color.White)
    ) {
        // 5
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
```

Here's a what the code above does:

1. Annotating the function using the `@Composable`  annotation. This let's you build the component's UI programmatically as opposed to the previous way of using XML.
2. The function takes `AttachmentState`  as arguements. `AttachmentState` is a class that handles the state of the attachment. It has a `MessageItem` which contains all the information pertaining a particular attachment.
3. This stores the `showPassword` in menmory of the composable by using the `remember` composable. This value is an observable which triggers a recomposition of the composable when the value changes.
4. Looks for messages whose `type` is password from the `AttachmentState`.
5. This is composable for inputs.  Here, the value of the `OutlinedTextField` is set by getting the `extraData` that was passed to the attachemnt. Notice that the `visualTransformation` and `trailingIcon` are dependent on the value of `showPassword`. This helps in toggling the pasword visibility.

Now that you alreadly have the view that is supposed to render your attachment, next is creating the custom `AttachmentFactory` 

## Creating Custom Password AttachmentFactory

You have to create your own factory. This is how it will look like:

```kotlin
@ExperimentalStreamChatApi
val customAttachmentFactories: List<AttachmentFactory> = listOf(
     AttachmentFactory(
        canHandle = { attachments -> attachments.any { it.type == "password" } },
        content = @Composable { PasswordAttachmentView(it) }
    )
)
```

This is a list AttachmentFactories. Each `AttachmentFactory` has the following two properties:

- `canHandle` - This is where you specify what type of attachments the factory can handle. In this case it's the attachments with `type` password.
- `content` - This specifies the view that renders your custom attachment. This uses the `PasswordAttachmentView` composable that you've just seen earlier.

With this done, the only remaining thing to do is to add this custom factories to the `ChatTheme`. To do this override the `attachmentFactories`:

```kotlin
ChatTheme(
    attachmentFactories = customAttachmentFactories + defaultFactories
)
```

To get `default` factories you simple do this:

```Koltin
val defaultFactories = StreamAttachmentFactories.defaultFactories()
```

Now when you send a message with a `type` password. This is how it appears on the MessageList:

![password_attachment](/Users/harun/AndroidStudioProjects/StreamComposeAttachements/images/password_attachment.png)

Tappinn the password visibility icon, the password is shown:

![password_attachment_show](/Users/harun/AndroidStudioProjects/StreamComposeAttachements/images/password_attachment_show.png)

This is possible because of the `showPassword` variable which makes the `OutlinedTextField` to be recomposed and the icon to change too. You can toggle in between the states easily.  Now all attachments with `type` password will be rendered with this view by default.

You've seen how to send attachments which do not have files. In the next section, you'll be learning how to send record voice notes and send them as custom attachemnts too.

## Custom Audio Attachment

Sending audio files is a much sort after feature in chat apps. With the Stream Chat SDK you can send audio files as custom attachments. Similar to the password attachment, you'll need to:

- Create and send your custom attachment.
- Create a custom view for your attachment.
- Add your custom `AttachmentFactory`.

Here's the message with the attachment:

```kotlin
val attachment = Attachment(
    type = "audio",
    upload = File(output),
)
val message = Message(
    cid = channelId,
    attachments = mutableListOf(attachment),
)
```

For attachments with files, you have to use the `upload` property. Which uploads your attachment and you can access the attachemnt using the `url` property of the attachment.

Now that your attachment is ready to be sent, you'll learn how to create a preview of the same next.

## Previewing Custom Attachments

For an audio, you'll need a UI with a play functionality so that you can listen to the audio that has been sent. For playing you'll use the android `MediaPlayer` for playing the audio file.

This is how you composable will be:

```kotlin
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
        // 5
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
        // 6
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
```

To explain the code above:

1. This is another state variable that tracks the playing state. It helps in toggling between the play and stop icons.
2. Gets the audio attachment from `AttachmentState` .
3. You're creating a `PlayerWrapper` instance which has utility methods for playing, stopping and relasing the `MediaPlayer`. You also pass the audio file as URI.
4. This calls the `release()` function from `PlayerWrapper` class to clean up the  `MediaPlayer`.
5. This is an `IconButton` composable that changes state depending on the state of the player. Tapping this plays the audio and shows the stop icon. Tapping stop icon will stop the audio.
6. This is a composable which shows the name of the file.

You have the preview ready, you'll be creating a custom messages screen which has a record  button at the bottom.![custom_message_composer](/Users/harun/AndroidStudioProjects/StreamComposeAttachements/images/custom_message_composer.png)

## Customizing the Messages Screen

## Creating a Custom Message Composer

## Conclusion

You've seen how easy it is to add custom attachments to Jetpack Compose UI compent.  You can add as many custom attachments as your app supports.

The the full sample project with examples in this tutorial [on GitHub](https://github.com/wangerekaharun/StreamComposeAttachments)

You can also go through the [Sending Custom Attachments]() sections that explain more about custom attachments.

The [Compose SDK](https://getstream.io/chat/docs/sdk/android/compose/overview/) is still in beta so incase you have any feedback on using the SDK! You can reach the team easily [on Twitter](https://twitter.com/getstream_io) and [on GitHub](https://github.com/GetStream/stream-chat-android).

