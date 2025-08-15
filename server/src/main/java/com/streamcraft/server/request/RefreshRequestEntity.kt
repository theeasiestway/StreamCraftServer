package com.streamcraft.server.request

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequestEntity(
    val refreshToken: String,
    val redirectUri: String,
)