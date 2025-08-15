package com.streamcraft.server.router

import com.streamcraft.server.configs.ClientConfigs
import com.streamcraft.server.ext.respondBadRequestError
import com.streamcraft.server.ext.respondInternalServerError
import com.streamcraft.server.ext.respondOkWithBody
import com.streamcraft.server.logger.Logger
import com.streamcraft.server.request.AuthRequestEntity
import com.streamcraft.server.request.RefreshRequestEntity
import com.streamcraft.server.response.AuthResponse
import com.streamcraft.server.validator.RequestsValidator
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.post

class YouTubeRouter(
    private val clientConfigs: ClientConfigs,
    private val httpClient: HttpClient,
    private val requestsValidator: RequestsValidator,
    private val logger: Logger,
) : Router {

    override val AUTH_ENDPOINT = "/auth/youtube"
    override val REFRESH_ENDPOINT = "/refresh/youtube"

    override fun auth(
        routing: Routing,
    ) {
        with(routing) {
            post(AUTH_ENDPOINT) {
                try {
                    val authRequest = call.receive<AuthRequestEntity>()
                    requestsValidator.validateRedirectUri(redirectUri = authRequest.redirectUri)
                    val tokens = httpClient.submitForm(
                        url = "https://oauth2.googleapis.com/token",
                        formParameters = Parameters.build {
                            append("client_id", clientConfigs.clientId)
                            append("client_secret", clientConfigs.clientSecret)
                            append("code", authRequest.code)
                            append("redirect_uri", authRequest.redirectUri)
                            append("grant_type", "authorization_code")
                        },
                    ).body<AuthResponse>()
                    call.respondOkWithBody(message = tokens)
                } catch (e: Exception) {
                    handleError(
                        endpoint = AUTH_ENDPOINT,
                        exception = e,
                    )
                }
            }
        }
    }

    override fun refreshToken(
        routing: Routing,
    ) {
        with(routing) {
            post(REFRESH_ENDPOINT) {
                try {
                    val refreshRequest = call.receive<RefreshRequestEntity>()
                    requestsValidator.validateRedirectUri(redirectUri = refreshRequest.redirectUri)
                    val tokens = httpClient.post("https://oauth2.googleapis.com/token") {
                        contentType(ContentType.Application.FormUrlEncoded)
                        setBody(
                            Parameters.build {
                                append("client_id", clientConfigs.clientId)
                                append("client_secret", clientConfigs.clientSecret)
                                append("refresh_token", refreshRequest.refreshToken)
                                append("grant_type", "refresh_token")
                            }.formUrlEncode()
                        )
                    }.body<AuthResponse>()
                    call.respondOkWithBody(message = tokens)
                } catch (e: Exception) {
                    handleError(
                        endpoint = REFRESH_ENDPOINT,
                        exception = e,
                    )
                }
            }
        }
    }

    private suspend fun RoutingContext.handleError(
        endpoint: String,
        exception: Exception,
    ) {
        logger.e("[$endpoint] error: $exception")
        when (exception) {
            is IllegalArgumentException,
            is ContentTransformationException -> {
                call.respondBadRequestError()
            }
            else -> {
                call.respondInternalServerError()
            }
        }
    }
}