package com.streamcraft.server.ext

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

suspend inline fun ApplicationCall.respondInternalServerError() {
    respond(
        status = HttpStatusCode.InternalServerError,
        message = HttpStatusCode.InternalServerError.description,
    )
}

suspend inline fun ApplicationCall.respondBadRequestError() {
    respond(
        status = HttpStatusCode.InternalServerError,
        message = HttpStatusCode.InternalServerError.description,
    )
}

suspend inline fun <reified T : Any> ApplicationCall.respondOkWithBody(message: T) {
    respond(
        status = HttpStatusCode.OK,
        message = message,
    )
}