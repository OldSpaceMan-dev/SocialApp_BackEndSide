package com.oldspaceman.route

import com.oldspaceman.model.FollowAndUnfollowResponse
import com.oldspaceman.model.FollowsParams
import com.oldspaceman.repository.follows.FollowsRepository
import com.oldspaceman.util.Constants
import com.oldspaceman.util.getLongParameter
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject



fun Routing.followsRouting(){
    val repository by inject<FollowsRepository>()

    //make sure that user is try to access these resources is authenticated
    authenticate {
        route(path = "/follows"){


            post(path = "/follow") {
                try {
                    val params = call.receiveNullable<FollowsParams>()

                    // couldn't  convert parameter that we submitted by the user
                    //to create params object
                    if (params == null) {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = FollowAndUnfollowResponse(
                                success = false,
                                message = Constants.MISSING_PARAMETERS_ERROR_MESSAGE
                            )
                        )
                        return@post
                    }

                    val result = repository.followUser(follower = params.follower, following = params.following)

                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                    //if successfully convert users parameters to this follow params
//                    val result = if (params.isFollowing){ // true = following user
//                        repository.followUser(follower = params.follower, following = params.following)
//                    }else{
//                        repository.unfollowUser(follower = params.follower, following = params.following)
//                    }
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = FollowAndUnfollowResponse(
                            success = false,
                            message = Constants.UNEXPECTED_ERROR_MESSAGE
                        )
                    )
                }
            }

            post(path = "/unfollow"){
                try {
                    val params = call.receiveNullable<FollowsParams>()

                    // couldn't  convert parameter that we submitted by the user
                    //to create params object
                    if (params == null) {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = FollowAndUnfollowResponse(
                                success = false,
                                message = Constants.MISSING_PARAMETERS_ERROR_MESSAGE
                            )
                        )
                        return@post
                    }

                    val result = repository.unfollowUser(follower = params.follower, following = params.following)

                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = FollowAndUnfollowResponse(
                            success = false,
                            message = Constants.UNEXPECTED_ERROR_MESSAGE
                        )
                    )
                }
            }

            get(path = "/followers") {
                try {
                    val userId = call.getLongParameter(name = Constants.USER_ID_PARAMETER, isQueryParameter = true)
                    val page = call.request.queryParameters[Constants.PAGE_NUMBER_PARAMETER]?.toIntOrNull() ?: 0
                    val limit = call.request.queryParameters[Constants.PAGE_LIMIT_PARAMETER]?.toIntOrNull()
                        ?: Constants.DEFAULT_PAGE_SIZE

                    val result = repository.getFollowers(userId = userId, pageNumber = page, pageSize = limit)
                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                } catch (badRequestError: BadRequestException) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = Constants.MISSING_PARAMETERS_ERROR_MESSAGE
                    )
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = Constants.UNEXPECTED_ERROR_MESSAGE
                    )
                }
            }

            get(path = "/following") {
                try {
                    val userId = call.getLongParameter(name = Constants.USER_ID_PARAMETER, isQueryParameter = true)
                    val page = call.request.queryParameters[Constants.PAGE_NUMBER_PARAMETER]?.toIntOrNull() ?: 0
                    val limit = call.request.queryParameters[Constants.PAGE_LIMIT_PARAMETER]?.toIntOrNull()
                        ?: Constants.DEFAULT_PAGE_SIZE

                    val result = repository.getFollowing(userId = userId, pageNumber = page, pageSize = limit)
                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                } catch (badRequestError: BadRequestException) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = Constants.MISSING_PARAMETERS_ERROR_MESSAGE
                    )
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = Constants.UNEXPECTED_ERROR_MESSAGE
                    )
                }
            }

            get(path = "/suggestions") {
                try {
                    val userId = call.getLongParameter(name = Constants.USER_ID_PARAMETER, isQueryParameter = true)
                    val result = repository.getFollowingSuggestions(userId = userId)
                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                } catch (badRequestError: BadRequestException) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = Constants.MISSING_PARAMETERS_ERROR_MESSAGE
                    )
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = Constants.UNEXPECTED_ERROR_MESSAGE
                    )
                }
            }

        }
    }
}









