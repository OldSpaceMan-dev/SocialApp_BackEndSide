package com.oldspaceman.dao.post

interface PostDao{

    //Boolean - post created or not
    //add PostRow? - тк хотим вернуть сам пост а не bool
    suspend fun createPost(caption: String, imageUrl: String, userId: Long): PostRow?

    //feeds - лента // follows - people that following
    suspend fun getFeedsPost(userId: Long, follows: List<Long>, pageNumber: Int, pageSize: Int): List<PostRow>

    //call when we visit user profile - see the post (only one)
    suspend fun getPostByUser(userId: Long, pageNumber: Int, pageSize: Int): List<PostRow>

    //get single post
    suspend fun getPost(postId: Long): PostRow?


    //increment post comment count and like count
    suspend fun updateLikesCount(postId: Long, decrement: Boolean = false): Boolean

    suspend fun updateCommentsCount(postId: Long, decrement: Boolean = false): Boolean

    suspend fun deletePost(postId: Long): Boolean

}