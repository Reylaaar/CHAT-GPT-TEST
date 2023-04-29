package com.reylar.aissistant.domain

import com.reylar.aissistant.common.Resource
import com.reylar.aissistant.model.CompletionRequest
import com.reylar.aissistant.model.CompletionResponse
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    fun sendCompletionRequest(completionRequest: CompletionRequest) : Flow<Resource<CompletionResponse>>
}