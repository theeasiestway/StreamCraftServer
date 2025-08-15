package com.streamcraft.server

import com.streamcraft.server.configs.ClientConfigs
import com.streamcraft.server.configs.CommonClientConfigs
import com.streamcraft.server.configs.ServerConfigs
import com.streamcraft.server.logger.ConsoleLogger
import com.streamcraft.server.router.TwitchRouter
import com.streamcraft.server.router.YouTubeRouter
import com.streamcraft.server.validator.RequestsValidator

suspend fun main() {
    val httpClient = createHttpClient(
        isDebug = ServerConfigs.isDebug,
    )
    val requestsValidator = RequestsValidator(
        configs = CommonClientConfigs,
    )
    val logger = ConsoleLogger(
        isDebug = ServerConfigs.isDebug,
    )
    val routers = listOf(
        YouTubeRouter(
            clientConfigs = ClientConfigs.Youtube,
            httpClient = httpClient,
            requestsValidator = requestsValidator,
            logger = logger,
        ),
        TwitchRouter(
            httpClient = httpClient,
            clientConfigs = ClientConfigs.Twitch,
            requestsValidator = requestsValidator,
            logger = logger,
        )
    )
    val server = Server(
        serverConfigs = ServerConfigs,
        commonClientConfigs = CommonClientConfigs,
        routers = routers,
        requestsValidator = requestsValidator,
        logger = logger,
    )
    server.startServer()
}