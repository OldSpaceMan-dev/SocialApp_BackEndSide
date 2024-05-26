package com.oldspaceman.dao.follows

interface FollowsDao {
    //method to follower User
    suspend fun followUser(follower: Long, following: Long): Boolean // to notify follow action was successful

    suspend fun unfollowUser(follower: Long, following: Long): Boolean

    //get list of followers ID and return listof ID
    //we're going to fetch user information from user table
    suspend fun getFollowers(userId: Long, pageNumber: Int, pageSize: Int): List<Long>
    suspend fun getFollowing(userId: Long, pageNumber: Int, pageSize: Int): List<Long>

    // need new method get us to know all users ID following to user
    //return the list of people following the current userID
    suspend fun getAllFollowing(userId: Long): List<Long>

    //if user already following
    suspend fun isAlreadyFollowing(follower: Long, following: Long): Boolean

}