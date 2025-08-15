package com.streamcraft.server.configs

sealed class ClientConfigs {
    private val CLIENT_ID_KEY = "CLIENT_ID_KEY"
    private val CLIENT_SECRET_KEY = "CLIENT_SECRET_KEY"

    /** client_id from google or twitch console */
    val clientId: String
        get() = System.getenv("${keyPrefix}_$CLIENT_ID_KEY")

    /** client_secret from google or twitch console */
    val clientSecret: String
        get() = System.getenv("${keyPrefix}_$CLIENT_SECRET_KEY")

    protected abstract val keyPrefix: String

    object Youtube : ClientConfigs() {
        override val keyPrefix = "YOUTUBE"
    }

    object Twitch : ClientConfigs() {
        override val keyPrefix = "TWITCH"
    }
}