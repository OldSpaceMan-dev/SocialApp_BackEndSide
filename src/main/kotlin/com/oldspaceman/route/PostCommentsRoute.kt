package com.oldspaceman.route

import com.oldspaceman.model.CommentResponse
import com.oldspaceman.model.GetCommentsResponse
import com.oldspaceman.model.NewCommentParams
import com.oldspaceman.model.RemoveCommentParams
import com.oldspaceman.repository.post_comments.PostCommentsRepository
import com.oldspaceman.util.Constants
import com.oldspaceman.util.getLongParameter
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.postCommentsRouting(){
    val repository by inject<PostCommentsRepository>()

    authenticate {
        route(path = "/post/comments"){


            //,,!!!!! тут ошбика
            post(path = "/create"){
                try {
                    val params = call.receiveNullable<NewCommentParams>()

                    if (params == null){
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = CommentResponse(
                                success = false,
                                message = "Could not parse comment parameters"
                            )
                        )
                        return@post
                    }

                    val result = repository.addComment(params = params)
                    call.respond(status = result.code, message = result.data)

                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = CommentResponse(
                            success = false,
                            message = "An unexpected error occurred, try again!"
                        )
                    )
                    return@post
                }
            }


            delete(path = "/delete"){

                try {
                    val params = call.receiveNullable<RemoveCommentParams>()

                    if (params == null){
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = CommentResponse(
                                success = false,
                                message = "Could not parse comment parameters"
                            )
                        )
                        return@delete
                    }

                    val result = repository.removeComment(params = params)
                    call.respond(status = result.code, message = result.data)

                } catch (error: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = CommentResponse(
                            success = false,
                            message = "An unexpected error occurred, try again!"
                        )
                    )
                    return@delete
                }


            }


            //list of comments
            get(path = "/{postId}"){
                try {
                    val postId = call.getLongParameter(name = "postId")
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                    val result = repository.getPostComments(
                        postId = postId,
                        pageNumber = page,
                        pageSize = limit
                    )
                    call.respond(status = result.code, message = result.data)
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = GetCommentsResponse(
                            success = false,
                            message = "An unexpected error occurred, try again!"
                        )
                    )
                    return@get
                }

            }

        }
    }
}