package com.streamcraft.server.configs

object CommonClientConfigs {
    private val CLIENT_HOST_KEY = "CLIENT_HOST_KEY"
    private val CLIENT_SCHEME_KEY = "CLIENT_SCHEME_KEY"
    private val CLIENT_REDIRECT_URI_KEY = "CLIENT_REDIRECT_URI_KEY"

    val clientHost: String      // hostname.com
        get() = System.getenv(CLIENT_HOST_KEY)

    val clientScheme: String    // https
        get() = System.getenv(CLIENT_SCHEME_KEY)

    val clientRedirectUri: String    // https
        get() = System.getenv(CLIENT_REDIRECT_URI_KEY)
}