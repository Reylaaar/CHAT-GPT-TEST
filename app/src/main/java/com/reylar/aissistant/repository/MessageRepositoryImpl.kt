package com.reylar.aissistant.repository

import com.reylar.aissistant.common.Resource
import com.reylar.aissistant.common.safeApiCall
import com.reylar.aissistant.data.ApiService
import com.reylar.aissistant.domain.MessageRepository
import com.reylar.aissistant.model.CompletionRequest
import com.reylar.aissistant.model.CompletionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MessageRepositoryImpl(
    private val apiService: ApiService
) : MessageRepository {
    override fun sendCompletionRequest(completionRequest: CompletionRequest): Flow<Resource<CompletionResponse>> =
        flow {
            emit(Resource.Loading())

            try {
                val messageRequest = requestToServer(completionRequest = completionRequest)

                if (messageRequest is Resource.Success) {
                    messageRequest.data?.let { completionResponse ->
                        emit(Resource.Success(completionResponse))
                    }
                } else {
                    emit(Resource.Error("Message failed"))
                }

            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Something went wrong"))
            }
        }

    suspend fun requestToServer(
        completionRequest: CompletionRequest
    ): Resource<CompletionResponse> {

        val response = safeApiCall(Dispatchers.IO) {
            apiService.getCompletions(
                completionResponse = completionRequest
            )
        }

        if (response.data?.isSuccessful == true) {
            response.data.body().let { completionResponse ->
                return completionResponse?.let { Resource.Success(it) }!!
            }
        }

        return Resource.Error("${response.message}")
    }
}