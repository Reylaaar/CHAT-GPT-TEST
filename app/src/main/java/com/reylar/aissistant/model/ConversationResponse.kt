package com.reylar.aissistant.model

import com.reylar.aissistant.model.CompletionResponse

data class ConversationResponse(
    val completionResponse: CompletionResponse? = null,
    val sender: String = "",
    val message: String = "",
    val isLoading: Boolean = false
)
