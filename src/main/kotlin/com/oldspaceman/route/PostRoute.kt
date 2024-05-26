package com.oldspaceman.route

import com.oldspaceman.model.PostResponse
import com.oldspaceman.model.PostTextParams
import com.oldspaceman.model.PostsResponse
import com.oldspaceman.repository.post.PostRepository
import com.oldspaceman.util.Constants
import com.oldspaceman.util.getLongParameter
import com.oldspaceman.util.saveFile
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import java.io.File

//route handle the request (маршрут обработки запросов)


fun Routing.postRouting(){

    val postRepository by inject<PostRepository>()

    //make sure that the users that access this route is authenticated user (авторизован)
    authenticate {
        route(path = "/post"){
            //POST Method
            post(path = "/create"){//goint to receive multi part data
                var fileName = ""
                var postTextParams: PostTextParams? = null
                val multiPartData = call.receiveMultipart() // обрабатывать многочастичные данные

                multiPartData.forEachPart { partData ->
                    when(partData){
                        is PartData.FileItem -> {
                            //partdata is file -> save image to our server
                            fileName = partData.saveFile(
                                folderPath = Constants.POST_IMAGES_FOLDER_PATH
                            )
                        }
                        is PartData.FormItem -> {
                            if (partData.name == "post_data"){
                                postTextParams = Json.decodeFromString(partData.value)
                            }
                        }
                        else -> {}
                    }
                    partData.dispose() //освобождения ресурсов,
                }

                //building image url
                val imageUrl = "${Constants.BASE_URL}${Constants.POST_IMAGES_FOLDER}$fileName"

                //?succes convert Json post data to postTextParams |kotlin object
                if (postTextParams == null){ //not success
                    //delete this that we save
                    File("${Constants.POST_IMAGES_FOLDER_PATH}/$fileName").delete()

                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = PostResponse(
                            success = false,
                            message = "Could not parse post data"
                        )
                    )
                }else {
                    val result = postRepository.createPost(imageUrl, postTextParams!!) // !!значение точно не равно null.  значение не является null и необходимо избежать проверки на null.

                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                }
                
            }


            // GET Method - single request -- {} is need like the path paramiters
            get(path = "/{postId}"){
                try {
                    //apart repeat block - failed request - add this like extension fun in Util
                    val postId = call.getLongParameter(name = "postId")

                    val currentUserId = call.getLongParameter(
                        name = "currentUserId",
                        isQueryParameter = true
                    )

                    val result = postRepository.getPost(postId = postId, currentUserId = currentUserId)
                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                }catch (badRequestError: BadRequestException){
                    return@get // @get -> этоt возврат осуществляется из обработчика GET запроса.

                }catch (anyError: Throwable){
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = PostResponse(
                            success = false,
                            message = "An unexpected error has occurred, try again!"
                        )
                    )
                }
            }

            delete(path = "/{postId}"){
                try {
                    val postId = call.getLongParameter(name = "postId")

                    val result = postRepository.deletePost(postId)
                    call.respond(
                        status = result.code,
                        message = result.data
                    )

                }catch (badRequestError: BadRequestException){
                    return@delete

                }catch (anyError: Throwable){
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = PostResponse(
                            success = false,
                            message = "An unexpected error has occurred, try again!"
                        )
                    )
                }
            }
        }



        route(path = "/posts"){

            get(path = "/feed") {
                try {
                    val currentUserId = call.getLongParameter(
                        name = "currentUserId",
                        isQueryParameter = true
                    )
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                    val result = postRepository.getFeedPosts(
                        userId = currentUserId,
                        pageNumber = page,
                        pageSize = limit
                    )
                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                }catch (badRequestError: BadRequestException){
                    return@get

                }catch (anyError: Throwable){
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = PostsResponse(
                            success = false,
                            message = "An unexpected error has occurred, try again!"
                        )
                    )
                }
            }

            get(path = "/{userId}"){
                try {
                    val postsOwnerId = call.getLongParameter(name = "userId")
                    val currentUserId = call.getLongParameter(name = "currentUserId", isQueryParameter = true)
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                    val result = postRepository.getPostsByUser(
                        postsOwnerId = postsOwnerId,
                        currentUserId = currentUserId,
                        pageNumber = page,
                        pageSize = limit
                    )
                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                }catch (badRequestError: BadRequestException){
                    return@get

                }catch (anyError: Throwable){
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = PostsResponse(
                            success = false,
                            message = "An unexpected error has occurred, try again!"
                        )
                    )
                }
            }
        }



    }
}