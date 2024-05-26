package com.oldspaceman.repository.post

import com.oldspaceman.model.PostResponse
import com.oldspaceman.model.PostTextParams
import com.oldspaceman.model.PostsResponse
import com.oldspaceman.util.Response

interface PostRepository {

    // return single PostResponse
    suspend fun createPost(imageUrl: String, postTextParam: PostTextParams): Response<PostResponse>

    //return list PostsResponse
    suspend fun getFeedPosts(userId: Long, pageNumber: Int, pageSize: Int): Response<PostsResponse>

    suspend fun getPostsByUser(
        postsOwnerId: Long,
        currentUserId: Long,
        pageNumber: Int,
        pageSize: Int
    ): Response<PostsResponse>

    suspend fun getPost(postId: Long, currentUserId: Long): Response<PostResponse>

    suspend fun deletePost(postId: Long): Response<PostResponse>

}