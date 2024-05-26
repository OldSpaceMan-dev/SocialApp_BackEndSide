package com.oldspaceman.plugins

import com.oldspaceman.route.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authRouting()
        followsRouting()
        postRouting()
        profileRouting()
        postCommentsRouting()
        postLikesRouting()
        //view image
        staticResources("/", "static")
    }
}


