package com.streamcraft.server.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val access_token: String,
    val expires_in: Int,
    val refresh_token: String? = null,
    val scope: String,
    val token_type: String,
)