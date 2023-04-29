package com.reylar.aissistant

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reylar.aissistant.common.Resource
import com.reylar.aissistant.domain.MessageRepository
import com.reylar.aissistant.model.Choice
import com.reylar.aissistant.model.CompletionRequest
import com.reylar.aissistant.model.CompletionResponse
import com.reylar.aissistant.model.ConversationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class OpenMessageViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _messageState = MutableStateFlow(MessageState())
    val messageState = _messageState

    var openMessageText by mutableStateOf("")

    var animateScrollState by mutableStateOf(false)
        private set

    fun onMessageTextChanged(text: String) {
        this.openMessageText = text
    }

    fun clearMessageBox() {
        this.openMessageText = ""
    }

    fun onScrollToItem(scroll: Boolean) {
        this.animateScrollState = scroll
    }


    fun sendMessage(question: String) {

        val completionRequest = CompletionRequest(
            model = "text-davinci-003",
            prompt = question,
            max_tokens = 4000
        )

        messageRepository.sendCompletionRequest(
            completionRequest = completionRequest
        ).onEach { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { completionResponse ->

                        removeTypingText()

                        val conversationResponse = ConversationResponse(
                            completionResponse = completionResponse,
                            sender = "OpenAI"
                        )

                        _messageState.value = _messageState.value.copy(
                            messages = _messageState.value.messages + listOf(conversationResponse),
                            isLoading = false
                        )

                        onScrollToItem(true)
                    }
                }

                is Resource.Loading -> {
                    clearMessageBox()

                    sendUserQuestion(question)
                    sendTypingMsg()

                    onScrollToItem(true)
                }

                is Resource.Error -> {

                    _messageState.value = _messageState.value.copy(
                        isLoading = false,
                        error = "${response.message}",
                    )
                }
            }
        }.launchIn(viewModelScope)

    }


    private fun sendUserQuestion(question: String) {
        val conversationUser = ConversationResponse(
            sender = "Me", message = question,
            completionResponse = CompletionResponse()
        )

        _messageState.value = _messageState.value .copy(
            messages = _messageState.value .messages + listOf(conversationUser),
        )
    }


    private fun sendTypingMsg() {
        val conversationResponse = ConversationResponse(
            sender = "OpenAI", message = "typing...", isLoading = true,
            completionResponse = CompletionResponse()
        )

        _messageState.value = _messageState.value.copy(
            isLoading = true,
            messages = _messageState.value.messages + listOf(conversationResponse),
        )
    }

    private fun removeTypingText() {

        _messageState.value = _messageState.value.copy(
            messages = _messageState.value.messages.toMutableList()
                .also { it.removeAt(_messageState.value.messages.lastIndex) },
        )
    }


    fun formattedResponseText(choice: List<Choice>): String {
        return choice.joinToString("\n") { it.text }.replaceFirst("\n\n", "")
    }
}
