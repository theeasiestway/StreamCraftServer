package com.streamcraft.server

import com.streamcraft.server.configs.ClientConfigs
import com.streamcraft.server.configs.ServerConfigs
import com.streamcraft.server.request.AuthRequest
import com.streamcraft.server.response.AuthResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
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
import io.ktor.server.request.PipelineRequest
import io.ktor.server.request.header
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.json.Json


class Server(
    private val serverConfigs: ServerConfigs,
    private val clientConfigs: ClientConfigs,
) {
    private val VALID_ORIGIN = "${clientConfigs.clientScheme}://${clientConfigs.clientHost}"
    private val AUTH_REQUEST_KEY = AttributeKey<AuthRequest>(AuthRequest::class.java.simpleName)

    suspend fun startServer() {
        instanceServer {
            intercept(ApplicationCallPipeline.Plugins) {
                validateRequest()
            }
            routing {
                post("/auth") {
                    instanceHttpClient().use { client ->
                        try {
                            val authRequest = call.attributes[AUTH_REQUEST_KEY]
                            println("efwefwef authRequest: $authRequest")
                            val tokens = exchangeAuthCode(
                                client = client,
                                authRequest = authRequest,
                            )
                            println("efwefwef tokens: $tokens")
                            call.respond(
                                status = HttpStatusCode.OK,
                                message = tokens,
                            )
                        } catch (e: Exception) {
                            println("efwefwef exchange tokens error: $e")
                            call.respond(
                                status = HttpStatusCode.InternalServerError,
                                message = HttpStatusCode.InternalServerError.description,
                            )
                            return@post
                        }
                    }
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
                    host = clientConfigs.clientHost,
                    schemes = listOf(clientConfigs.clientScheme),
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
            println("efwefwef before check origin")
            validateOrigin(request = call.request)
            println("efwefwef origin valid")
            val authRequest = call.receive<AuthRequest>()
            println("efwefwef authRequest parsed")
            validateRedirectUri(redirectUri = authRequest.redirectUri)
            println("efwefwef redirectUri valid")
            cacheRequest(
                call = call,
                request = authRequest,
            )
        } catch (e: Exception) {
            println("efwefwef validate error: $e")
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = HttpStatusCode.BadRequest.description,
            )
            finish()
        }
    }

    private fun validateOrigin(request: PipelineRequest) {
        val origin = request.header(name = HttpHeaders.Origin)
        require(origin == VALID_ORIGIN)
    }

    private fun validateRedirectUri(redirectUri: String) {
        require(redirectUri == clientConfigs.clientRedirectUri)
    }

    private fun cacheRequest(
        call: PipelineCall,
        request: AuthRequest,
    ) {
        call.attributes.put(
            key = AUTH_REQUEST_KEY,
            value = request,
        )
    }

    private suspend fun exchangeAuthCode(
        client: HttpClient,
        authRequest: AuthRequest
    ): AuthResponse {
        return client.submitForm(
            url = "https://oauth2.googleapis.com/token",
            formParameters = Parameters.build {
                append("client_id", clientConfigs.clientId)
                append("client_secret", clientConfigs.clientSecret)
                append("code", authRequest.code)
                append("redirect_uri", authRequest.redirectUri)
                append("grant_type", "authorization_code")
            },
        ).body<AuthResponse>()
    }
}