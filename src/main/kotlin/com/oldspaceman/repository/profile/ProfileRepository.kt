package com.oldspaceman.repository.profile

import com.oldspaceman.model.ProfileResponse
import com.oldspaceman.model.UpdateUserParams
import com.oldspaceman.util.Response

interface ProfileRepository {

    //find user by Id
    //currentUserId- will help determine(определять) two extra inform we will forward(направить) to client app
    suspend fun getUserById(userId: Long, currentUserId: Long): Response<ProfileResponse>
        // if userId == currentUserId - its own profile - determine what show (follow / unfollow buttom)

    suspend fun updateUser(updateUserParams: UpdateUserParams): Response<ProfileResponse>

}