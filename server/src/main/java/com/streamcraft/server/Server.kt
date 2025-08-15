package com.streamcraft.server

import com.streamcraft.server.configs.CommonClientConfigs
import com.streamcraft.server.configs.ServerConfigs
import com.streamcraft.server.ext.respondBadRequestError
import com.streamcraft.server.logger.Logger
import com.streamcraft.server.router.Router
import com.streamcraft.server.validator.RequestsValidator
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.PipelineCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.header
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.json.Json


class Server(
    private val serverConfigs: ServerConfigs,
    private val commonClientConfigs: CommonClientConfigs,
    private val routers: List<Router>,
    private val requestsValidator: RequestsValidator,
    private val logger: Logger,
) {

    suspend fun startServer() {
        instanceServer {
            intercept(ApplicationCallPipeline.Plugins) {
                validateRequest()
            }
            routing {
                routers.forEach { router ->
                    router.auth(
                        routing = this,
                    )
                    router.refreshToken(
                        routing = this,
                    )
                }
            }
        }.startSuspend(wait = true)
    }

    private fun instanceServer(
        module: suspend Application.() -> Unit,
    ): EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> {
        return embeddedServer(
            factory = Netty,
            port = serverConfigs.port,
            host = serverConfigs.host,
        ) {
            install(CORS) {
                allowMethod(HttpMethod.Post)
                allowHeader(HttpHeaders.ContentType)
                allowHost(
                    host = commonClientConfigs.clientHost,
                    schemes = listOf(commonClientConfigs.clientScheme),
                )
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            module()
        }
    }

    private suspend fun PipelineContext<Unit, PipelineCall>.validateRequest() {
        try {
            requestsValidator.validateOrigin(
                origin = call.request.header(name = HttpHeaders.Origin).orEmpty(),
            )
            logger.d("[validateRequest] origin is valid")
        } catch (e: Exception) {
            logger.e("[validateRequest] error $e")
            call.respondBadRequestError()
            finish()
        }
    }
}