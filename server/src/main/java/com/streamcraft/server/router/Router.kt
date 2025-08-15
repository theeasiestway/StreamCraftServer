package com.streamcraft.server.router

import io.ktor.server.routing.Routing

interface Router {
    val AUTH_ENDPOINT: String
    val REFRESH_ENDPOINT: String

    fun auth(
        routing: Routing,
    )

    fun refreshToken(
        routing: Routing,
    )
}