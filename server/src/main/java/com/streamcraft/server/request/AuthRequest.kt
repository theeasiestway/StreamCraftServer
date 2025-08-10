package com.streamcraft.server.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val code: String,
    val redirectUri: String,
)