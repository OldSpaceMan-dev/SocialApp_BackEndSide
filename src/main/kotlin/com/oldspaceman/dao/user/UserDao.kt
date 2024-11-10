package com.oldspaceman.dao.user

import com.oldspaceman.model.SignUpParams

//Data Access Object
interface UserDao {
    suspend fun insert(params: SignUpParams): UserRow?
    suspend fun findByEmail(email: String): UserRow?

    //find the user ID
    suspend fun findById(userId: Long): UserRow?

    //update the user
    suspend fun updateUser(userId: Long, name: String, bio: String, imageUrl: String?): Boolean

    // is isFollowing = true is already following User
    suspend fun updateFollowsCount(follower: Long, following: Long, isFollowing: Boolean): Boolean

    //get list of iD's
    suspend fun getUsers(ids: List<Long>): List<UserRow>

    suspend fun getPopularUsers(limit: Int): List<UserRow>



}



