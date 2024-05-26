package com.oldspaceman.repository.post_comments

import com.oldspaceman.model.CommentResponse
import com.oldspaceman.model.GetCommentsResponse
import com.oldspaceman.model.NewCommentParams
import com.oldspaceman.model.RemoveCommentParams
import com.oldspaceman.util.Response

interface PostCommentsRepository {

    suspend fun addComment(params: NewCommentParams): Response<CommentResponse>

    suspend fun removeComment(params: RemoveCommentParams): Response<CommentResponse>

    suspend fun getPostComments(postId: Long, pageNumber: Int, pageSize: Int): Response<GetCommentsResponse>

}