package com.streamcraft.server.configs

object ClientConfigs {
    private const val CLIENT_ID_KEY = "CLIENT_ID_KEY"
    private const val CLIENT_SECRET_KEY = "CLIENT_SECRET_KEY"
    private const val CLIENT_HOST_KEY = "CLIENT_HOST_KEY"
    private const val CLIENT_SCHEME_KEY = "CLIENT_SCHEME_KEY"
    private const val CLIENT_REDIRECT_URI_KEY = "CLIENT_REDIRECT_URI_KEY"

    val clientId: String        // client_id from google console
        get() = System.getenv(CLIENT_ID_KEY)

    val clientSecret: String    // client_secret from google console
        get() = System.getenv(CLIENT_SECRET_KEY)

    val clientHost: String      // hostname.com
        get() = System.getenv(CLIENT_HOST_KEY)

    val clientScheme: String    // https
        get() = System.getenv(CLIENT_SCHEME_KEY)

    val clientRedirectUri: String    // https
        get() = System.getenv(CLIENT_REDIRECT_URI_KEY)
}