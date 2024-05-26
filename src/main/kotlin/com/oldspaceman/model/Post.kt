package com.oldspaceman.model

import kotlinx.serialization.Serializable


@Serializable
data class PostTextParams(
    val caption: String, //description of the post
    val userId: Long
)

@Serializable
data class Post(
    val postId: Long,
    val caption: String,
    val imageUrl: String,
    val createdAt: String,

    val likesCount: Int,
    val commentsCount: Int,

    val userId: Long,
    val userName: String,
    val userImageUrl: String?,

    val isLiked: Boolean, //current user has like or not
    val isOwnPost: Boolean // current post belong to current user
)

@Serializable
data class PostResponse(
    val success: Boolean, // request was successful or not
    val post: Post? = null,
    val message: String? = null
)


// exm posts in profile user
@Serializable
data class PostsResponse(
    val success: Boolean, // request was successful or not
    val posts: List<Post> = listOf(),
    val message: String? = null
)