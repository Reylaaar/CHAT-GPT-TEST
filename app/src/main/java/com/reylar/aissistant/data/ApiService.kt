package com.reylar.aissistant.data


import com.reylar.aissistant.model.CompletionRequest
import com.reylar.aissistant.model.CompletionResponse

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @POST("v1/completions")
    suspend fun getCompletions(@Body completionResponse: CompletionRequest) : Response<CompletionResponse>
}