package com.oldspaceman.repository.follows

import com.oldspaceman.model.FollowAndUnfollowResponse
import com.oldspaceman.model.GetFollowsResponse
import com.oldspaceman.util.Response

interface FollowsRepository {

    suspend fun followUser(follower: Long, following: Long): Response<FollowAndUnfollowResponse>

    suspend fun unfollowUser(follower: Long, following: Long): Response<FollowAndUnfollowResponse>


    suspend fun getFollowers(userId: Long, pageNumber: Int, pageSize: Int): Response<GetFollowsResponse>

    suspend fun getFollowing(userId: Long, pageNumber: Int, pageSize: Int): Response<GetFollowsResponse>

    suspend fun getFollowingSuggestions(userId: Long): Response<GetFollowsResponse>
}