package com.streamcraft.server.response

import kotlinx.serialization.Serializable

@Serializable
data class TwitchAuthResponse(
    val access_token: String,
    val expires_in: Int,
    val refresh_token: String? = null,
    val scope: List<String>,
    val token_type: String,
)