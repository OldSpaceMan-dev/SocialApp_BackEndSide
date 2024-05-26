package com.oldspaceman.di

import com.oldspaceman.dao.follows.FollowsDao
import com.oldspaceman.dao.follows.FollowsDaoImpl
import com.oldspaceman.dao.post.PostDao
import com.oldspaceman.dao.post.PostDaoImpl
import com.oldspaceman.dao.post_comments.PostCommentsDao
import com.oldspaceman.dao.post_comments.PostCommentsDaoImpl
import com.oldspaceman.dao.post_likes.PostLikesDao
import com.oldspaceman.dao.post_likes.PostLikesDaoImpl
import com.oldspaceman.dao.user.UserDao
import com.oldspaceman.dao.user.UserDaoImpl
import com.oldspaceman.repository.auth.AuthRepository
import com.oldspaceman.repository.auth.AuthRepositoryImpl
import com.oldspaceman.repository.follows.FollowsRepository
import com.oldspaceman.repository.follows.FollowsRepositoryImpl
import com.oldspaceman.repository.post.PostRepository
import com.oldspaceman.repository.post.PostRepositoryImpl
import com.oldspaceman.repository.post_comments.PostCommentsRepository
import com.oldspaceman.repository.post_comments.PostCommentsRepositoryImpl
import com.oldspaceman.repository.post_likes.PostLikesRepository
import com.oldspaceman.repository.post_likes.PostLikesRepositoryImpl
import com.oldspaceman.repository.profile.ProfileRepository
import com.oldspaceman.repository.profile.ProfileRepositoryImpl
import org.koin.dsl.module

// next DI provide(предоставлять) UserRepositoryImpl we are passing USerDao object
// over injecting user repository

val appModule = module {
    single<UserDao> { UserDaoImpl() }
    single<AuthRepository> { AuthRepositoryImpl( get() ) }

    single<FollowsDao> { FollowsDaoImpl() }
    single<FollowsRepository> { FollowsRepositoryImpl(get(), get()) }

    single<PostLikesDao> { PostLikesDaoImpl() }
    single<PostLikesRepository> { PostLikesRepositoryImpl(get(), get()) }

    single<PostDao> { PostDaoImpl() }
    single<PostRepository> { PostRepositoryImpl(get(), get(), get()) }

    single<ProfileRepository> { ProfileRepositoryImpl(get(), get()) }

    single<PostCommentsDao> { PostCommentsDaoImpl() }
    single<PostCommentsRepository> { PostCommentsRepositoryImpl(get(), get()) }



}