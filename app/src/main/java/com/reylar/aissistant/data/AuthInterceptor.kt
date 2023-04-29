package com.reylar.aissistant.data

import com.reylar.aissistant.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        var request: Request = chain.request()

        val headers =
            request.headers.newBuilder().add("Authorization", "Bearer ${BuildConfig.AI_TOKEN}")
                .add("Content-Type", "application/json").build()

        request = request.newBuilder().headers(headers).build()
        return chain.proceed(request)
    }
}