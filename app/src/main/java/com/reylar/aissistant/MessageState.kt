package com.reylar.aissistant

import com.reylar.aissistant.model.ConversationResponse

data class MessageState(
    val messages: List<ConversationResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)