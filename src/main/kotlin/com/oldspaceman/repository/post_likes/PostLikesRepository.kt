package com.oldspaceman.repository.post_likes

import com.oldspaceman.model.LikeParams
import com.oldspaceman.model.LikeResponse
import com.oldspaceman.util.Response

interface PostLikesRepository {
    suspend fun addLike(params: LikeParams): Response<LikeResponse>

    suspend fun removeLike(params: LikeParams): Response<LikeResponse>
}