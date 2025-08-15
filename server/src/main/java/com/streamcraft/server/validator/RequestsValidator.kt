package com.streamcraft.server.validator

import com.streamcraft.server.configs.CommonClientConfigs
import kotlin.jvm.Throws

class RequestsValidator(
    private val configs: CommonClientConfigs,
) {
    private val VALID_ORIGIN = "${configs.clientScheme}://${configs.clientHost}"

    @Throws(IllegalArgumentException::class)
    fun validateOrigin(origin: String) {
        require(origin == VALID_ORIGIN)
    }

    @Throws(IllegalArgumentException::class)
    fun validateRedirectUri(redirectUri: String) {
        require(redirectUri == configs.clientRedirectUri)
    }
}