package com.streamcraft.server.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequestEntity(
    val code: String,
    val redirectUri: String,
)