package com.streamcraft.server

import com.streamcraft.server.configs.ClientConfigs
import com.streamcraft.server.configs.ServerConfigs

suspend fun main() {
    val server = Server(
        serverConfigs = ServerConfigs,
        clientConfigs = ClientConfigs,
    )
    server.startServer()
}