package com.reylar.aissistant.model

data class Choice(
    val text: String,
    val index: Int,
    val logprobs: Any?,
    val finish_reason: String
)