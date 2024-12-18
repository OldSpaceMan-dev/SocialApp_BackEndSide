package com.oldspaceman.repository.post

import com.oldspaceman.dao.follows.FollowsDao
import com.oldspaceman.dao.post.PostDao
import com.oldspaceman.dao.post.PostRow
import com.oldspaceman.dao.post_likes.PostLikesDao
import com.oldspaceman.dao.user.UserDao
import com.oldspaceman.model.*
import com.oldspaceman.util.Response
import io.ktor.http.*

class PostRepositoryImpl(
    private val postDao: PostDao,
    private val followsDao: FollowsDao,
    private val postLikesDao: PostLikesDao,

) : PostRepository {


    override suspend fun createPost(imageUrl: String, postTextParam: PostTextParams): Response<PostResponse> {
        val postRow = postDao.createPost(
            caption = postTextParam.caption,
            imageUrl = imageUrl,
            userId = postTextParam.userId
        )

        return if (postRow != null){
            postDao.updatePostCount(userId = postTextParam.userId) // увеличиваем кол-во постов у юзера
            Response.Success(
                data = PostResponse(
                    success = true,
                    post = toPost(
                        postRow = postRow,
                        isPostLiked = false,
                        isOwnPost = true
                    )
                ) // notify only what post create and send postRow
            )
        } else {
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = PostResponse(
                    success = false,
                    message = "Post could not be inserted in the db" // not be вставить в базу данных
                )
            )
        }
    }



    override suspend fun getFeedPosts(userId: Long, pageNumber: Int, pageSize: Int): Response<PostsResponse> {
        // we need retrieve (извлекать) all users that following the current user
        // need new method get us to know all users ID following to user
        val followingUsers = followsDao.getAllFollowing(userId = userId).toMutableList() // toMutableList -- изменияемый список позволяет добавлять/удалять данные
        followingUsers.add(userId) // включить самого пользователя (чьи подписки были получены)

        val postsRows = postDao.getFeedsPost(
            userId = userId,
            follows = followingUsers,
            pageNumber = pageNumber,
            pageSize = pageSize
        )

        // maping postsRows wuth post
        val posts = postsRows.map {
            toPost(
                postRow = it,
                isPostLiked = postLikesDao.isPostLikedByUser(postId = it.postId, userId = userId),
                isOwnPost = it.userId == userId
            )
        }

        return Response.Success(
            data = PostsResponse(
                success = true,
                posts = posts
            )
        )

    }



    override suspend fun getPostsByUser(
        postsOwnerId: Long,
        currentUserId: Long,
        pageNumber: Int,
        pageSize: Int
    ): Response<PostsResponse> {

        val postsRows = postDao.getPostByUser(
            userId = postsOwnerId,
            pageNumber = pageNumber,
            pageSize = pageSize
        )

        // maping postsRows wuth post
        val posts = postsRows.map {
            toPost(
                postRow = it,
                isPostLiked = postLikesDao.isPostLikedByUser(postId = it.postId, userId = currentUserId),
                isOwnPost = it.userId == currentUserId
            )
        }

        return Response.Success(
            data = PostsResponse(
                success = true,
                posts = posts
            )
        )

    }



    override suspend fun getPost(postId: Long, currentUserId: Long): Response<PostResponse> {
        val post = postDao.getPost(postId = postId)

        return if (post == null) {
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = PostResponse(
                    success = false,
                    message = "Could not retrieve post from database"
                )
            )
        } else {
            val isPostLiked = postLikesDao.isPostLikedByUser(postId, currentUserId)
            //is current user Owner this post
            val isOwnPost = post.postId == currentUserId // проверка на пренадлежность поста конретному юзеру
            Response.Success(
                data = PostResponse(success = true, post = toPost(
                    postRow = post,
                    isPostLiked = isPostLiked,
                    isOwnPost = isOwnPost
                ))
            )
        }
    }


    override suspend fun deletePost(postParams: RemovePostParams): Response<PostResponse> {


        val post = postDao.getPost(postId = postParams.postId)


        return if (post != null && post.userId == postParams.userId) {
            val postIsDeleted = postDao.deletePost(postId = postParams.postId)

            if (postIsDeleted) {
                //уменьшаем каунт постов на -1
                postDao.updatePostCount(userId = postParams.userId, decrement = true)


                Response.Success(
                    data = PostResponse(success = true) // notify only what post deleted
                )
            } else {
                Response.Error(
                    code = HttpStatusCode.InternalServerError,
                    data = PostResponse(
                        success = false,
                        message = "Post could not be deleted from the db" // not be вставить в базу данных
                    )
                )
            }
        } else {
            Response.Error(
                code = HttpStatusCode.Forbidden,
                data = PostResponse(
                    success = false,
                    message = "User is not the owner of the post or or the post doesn't exist"
                )
            )
        }

    }

/*
    override suspend fun deletePost(postId: Long): Response<PostResponse> {
        val postIsDeleted = postDao.deletePost(
            postId = postId
        )

        return if (postIsDeleted){
            Response.Success(
                data = PostResponse(success = true) // notify only what post deleted
            )
        }else{
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = PostResponse(
                    success = false,
                    message = "Post could not be deleted from the db" // not be вставить в базу данных
                )
            )
        }
    }
 */

    private fun toPost(
        postRow: PostRow,
        isPostLiked: Boolean,
        isOwnPost: Boolean
    ): Post{
        return Post(
            postId = postRow.postId,
            caption = postRow.caption,
            imageUrl = postRow.imageUrl,
            createdAt = postRow.createdAt,
            likesCount = postRow.likesCount,
            commentsCount = postRow.commentsCount,
            userId = postRow.userId,
            userImageUrl = postRow.userImageUrl,
            userName = postRow.userName,
            isLiked = isPostLiked,
            isOwnPost = isOwnPost
        )
    }




}