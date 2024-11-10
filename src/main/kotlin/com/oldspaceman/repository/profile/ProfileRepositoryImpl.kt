package com.oldspaceman.repository.profile

import com.oldspaceman.dao.follows.FollowsDao
import com.oldspaceman.dao.user.UserDao
import com.oldspaceman.dao.user.UserRow
import com.oldspaceman.model.Profile
import com.oldspaceman.model.ProfileResponse
import com.oldspaceman.model.UpdateUserParams
import com.oldspaceman.util.Response
import io.ktor.http.*

class ProfileRepositoryImpl(
    private val userDao: UserDao,
    private val followsDao: FollowsDao
) : ProfileRepository {
    override suspend fun getUserById(userId: Long, currentUserId: Long): Response<ProfileResponse> {
        val userRow = userDao.findById(userId)

        return if (userRow == null){
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = ProfileResponse(
                    success = false,
                    message = "Could not find user with id: $userId"
                )
            )
        }else{
            //determine is current user is following this user
            // follower - кто следует
            // following - за кем следуют
            val isFollowing = followsDao.isAlreadyFollowing(follower = currentUserId, following = userId)

            val isOwnProfile = userId == currentUserId

            Response.Success(
                data = ProfileResponse(
                    success = true,
                    profile = toProfile(userRow, isFollowing, isOwnProfile)
                )
            )
        }
    }


    override suspend fun updateUser(updateUserParams: UpdateUserParams): Response<ProfileResponse> {
        //if user exist in our database
        val userExists = userDao.findById(userId = updateUserParams.userId) != null

        if (userExists){
            val userUpdated = userDao.updateUser(
                userId = updateUserParams.userId,
                name = updateUserParams.name,
                bio = updateUserParams.bio,
                imageUrl = updateUserParams.imageUrl
            )

            return if (userUpdated){
                Response.Success(
                    data = ProfileResponse(success = true)
                )
            }else{
                Response.Error(
                    code = HttpStatusCode.Conflict,
                    data = ProfileResponse(
                        success = false,
                        message = "Could not update user: ${updateUserParams.userId}"
                    )
                )
            }
        }else{
            return Response.Error(
                code = HttpStatusCode.NotFound,
                data = ProfileResponse(
                    success = false,
                    message = "Could not find the user: ${updateUserParams.userId}"
                )
            )
        }
    }



    private fun toProfile(userRow: UserRow, isFollowing: Boolean, isOwnProfile: Boolean): Profile{
        return Profile(
            id = userRow.id,
            name = userRow.name,
            bio = userRow.bio,
            imageUrl = userRow.imageUrl,
            followersCount = userRow.followersCount,
            followingCount = userRow.followingCount,
            isFollowing = isFollowing,
            isOwnProfile = isOwnProfile,
            postCount = userRow.postCount
        )
    }
}






