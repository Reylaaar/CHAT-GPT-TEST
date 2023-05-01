@file:OptIn(ExperimentalFoundationApi::class)

package com.reylar.aissistant

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.reylar.aissistant.model.ConversationResponse
import com.reylar.aissistant.common.CustomOutlinedTextField
import com.reylar.aissistant.common.Screen
import com.reylar.aissistant.common.SingleTapButton
import com.reylar.aissistant.common.localMessageViewModel
import com.reylar.aissistant.common.localNavController
import com.reylar.aissistant.ui.theme.Blue700
import com.reylar.aissistant.ui.theme.Gray100
import com.reylar.aissistant.ui.theme.Gray200
import com.reylar.aissistant.ui.theme.Gray400
import com.reylar.aissistant.ui.theme.Gray700
import com.reylar.aissistant.ui.theme.AIssistantTheme
import com.reylar.aissistant.ui.theme.White000
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@OptIn(ExperimentalAnimationApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        installSplashScreen().apply {

        }

        setContent {
            val mainNavController = rememberAnimatedNavController()
            val openMessageViewModel: OpenMessageViewModel = hiltViewModel()

            AIssistantTheme() {

                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    CompositionLocalProvider(
                        localNavController provides mainNavController,
                        localMessageViewModel provides openMessageViewModel
                    ) {
                        BoxWithConstraints {

                            AnimatedNavHost(
                                navController = mainNavController,
                                startDestination = Screen.Menu.route
                            ) {

                                composable(
                                    route = Screen.Menu.route
                                ) {
                                    MessageMenu()
                                }
                            }

                        }
                    }

                }
            }
        }
    }
}

@Composable
fun MessageMenu() {

    val context = LocalContext.current
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val messageViewModel = localMessageViewModel.current
    val messageState = messageViewModel.messageState.collectAsState().value

    LaunchedEffect(key1 = messageViewModel.animateScrollState) {
        if (messageState.messages.isNotEmpty()) {
            scope.launch {
                scrollState.animateScrollToItem(messageState.messages.lastIndex)
            }

        }
        messageViewModel.onScrollToItem(false)
    }

    Column() {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 5.dp),
            text = "Chat GPT-4",
            style = TextStyle(
                fontFamily = MaterialTheme.typography.headlineLarge.fontFamily,
                color = if (isSystemInDarkTheme()) White000 else Blue700,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            )
        )

        Divider(
            modifier = Modifier
                .padding(vertical = 5.dp),
            color = Gray200,
            thickness = 0.5.dp
        )

        LazyColumn(
            state = scrollState, modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .animateContentSize(),
            contentPadding = PaddingValues(horizontal = 7.dp)
        ) {

            items(messageState.messages.size) { index ->
                val message = messageState.messages[index]

                if (message.sender == "OpenAI") {
                    MessageAIItem(
                        conversationResponse = message,
                        openMessageViewModel = messageViewModel,
                        context = context
                    )
                } else {
                    MessageUserItem(conversationResponse = message)
                }
            }
        }

        CustomOutlinedTextField(
            modifier = Modifier.padding(top = 5.dp),
            placeholder = {
                Text(
                    "Enter your question here...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSystemInDarkTheme()) Gray200 else Gray400,
                )
            },
            value = messageViewModel.openMessageText,
            onValueChange = {
                messageViewModel.onMessageTextChanged(it)
            },
            style = TextStyle(
                color = if (isSystemInDarkTheme()) Color.White else Gray700,
                fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
            )
        )

        SingleTapButton(
            modifier = Modifier
                .navigationBarsPadding()
                .imePadding()
                .padding(17.dp)
                .fillMaxWidth()
                .semantics {
                    contentDescription = "give_boost"
                },
            onClick = {
                messageViewModel.sendMessage(messageViewModel.openMessageText)
            },
            enabled = !messageState.isLoading,
        ) {
            if (messageState.isLoading) {
                CircularProgressIndicator(color = Blue700)
            } else {
                Text("Send Message")
            }
        }
    }
}


@Composable
fun MessageUserItem(conversationResponse: ConversationResponse) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
    ) {

        Text(
            modifier = Modifier
                .align(Alignment.End),
            text = conversationResponse.sender,
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                fontSize = 18.sp,
                color = if (isSystemInDarkTheme()) White000 else Gray700
            ),
        )
        //Message Context
        Column(
            modifier = Modifier
                .padding(top = 3.dp)
                .align(Alignment.End)
                .clip(
                    RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 0.dp,
                        bottomEnd = 8.dp,
                        bottomStart = 8.dp
                    )
                )
                .background(Blue700)
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 5.dp),
                text = conversationResponse.message,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                    fontSize = 16.sp,
                    color = White000
                ),
            )
        }
    }
}


@Composable
fun MessageAIItem(
    conversationResponse: ConversationResponse,
    openMessageViewModel: OpenMessageViewModel,
    context : Context
) {
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
    ) {
        Row(
            Modifier.align(Alignment.Start)
        ) {
            Image(
                modifier = Modifier
                    .size(35.dp)
                    .align(Alignment.CenterVertically),
                painter = painterResource(id = R.drawable.openai_logo),
                contentDescription = "openai_logo"
            )
        }

        //Api calling
        //Message Context
        if (conversationResponse.isLoading) {
            Column(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .align(Alignment.Start)
                    .clip(
                        RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 8.dp,
                            bottomEnd = 8.dp,
                            bottomStart = 8.dp
                        )
                    )
                    .background(Gray100)
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 5.dp),
                    text = conversationResponse.message,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontFamily = MaterialTheme.typography.labelLarge.fontFamily,
                        fontSize = 16.sp,
                        color = Gray700
                    ),
                )
            }

        }

        if (conversationResponse.completionResponse?.id?.isNotEmpty() == true) {
            //Message Context
            Column(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .align(Alignment.Start)
                    .clip(
                        RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 8.dp,
                            bottomEnd = 8.dp,
                            bottomStart = 8.dp
                        )
                    )
                    .background(Gray700)
            ) {
                Text(
                    modifier = Modifier
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                clipboardManager.setText(AnnotatedString(openMessageViewModel.formattedResponseText(conversationResponse.completionResponse.choices)))
                                Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                            }
                        )
                        .padding(start = 4.dp,
                            end = 4.dp,
                            top = 5.dp,
                            bottom = 6.dp),
                    text = openMessageViewModel.formattedResponseText(conversationResponse.completionResponse.choices),
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                        fontSize = 16.sp,
                        color = White000,
                    ),
                )
            }
        }
    }
}


