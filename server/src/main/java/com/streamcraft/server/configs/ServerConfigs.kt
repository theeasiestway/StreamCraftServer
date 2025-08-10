package com.streamcraft.server.configs

object ServerConfigs {
    private const val SERVER_HOST_KEY = "SERVER_HOST_KEY"
    private const val SERVER_PORT_KEY = "PORT" // this value is a render.com requirement

    val host: String
        get() = System.getenv(SERVER_HOST_KEY)

    val port: Int
        get() = System.getenv(SERVER_PORT_KEY).toInt()
}