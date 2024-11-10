package com.oldspaceman.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserParams(
    val userId: Long,
    val name: String,
    val bio: String,
    val imageUrl: String? = null
)


@Serializable
data class Profile(
    val id: Long,
    val name: String,
    val bio: String,
    val imageUrl: String? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isFollowing: Boolean, // if current user following -> navigate to profile screen(own profile)
    val isOwnProfile: Boolean, // show edit or follow
    val postCount: Int
)


@Serializable
data class ProfileResponse(
    val success: Boolean,
    val profile: Profile? = null,
    val message: String? = null
)