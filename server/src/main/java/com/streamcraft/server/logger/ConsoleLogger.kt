package com.streamcraft.server.logger

import io.github.oshai.kotlinlogging.KotlinLogging


class ConsoleLogger(
    private val isDebug: Boolean,
) : Logger {

    private val logger = KotlinLogging.logger {}

    override fun d(message: String) {
        if (isDebug) {
            logger.debug { message }
        }
    }

    override fun e(message: String) {
        logger.error { message }
    }
}