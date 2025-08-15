package com.streamcraft.server.configs

object ServerConfigs {
    private const val SERVER_HOST_KEY = "SERVER_HOST_KEY"
    private const val SERVER_PORT_KEY = "PORT" // this value is a render.com requirement
    private const val SERVER_IS_DEBUG_KEY = "SERVER_IS_DEBUG_KEY"

    val host: String
        get() = System.getenv(SERVER_HOST_KEY)

    val port: Int
        get() = System.getenv(SERVER_PORT_KEY).toInt()

    val isDebug: Boolean
        get() = System.getenv(SERVER_IS_DEBUG_KEY).toBoolean()
}