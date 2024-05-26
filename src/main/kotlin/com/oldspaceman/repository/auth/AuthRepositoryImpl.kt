package com.oldspaceman.repository.auth

import com.oldspaceman.dao.user.UserDao
import com.oldspaceman.model.AuthResponse
import com.oldspaceman.model.AuthResponseData
import com.oldspaceman.model.SignInParams
import com.oldspaceman.model.SignUpParams
import com.oldspaceman.plugins.generateToken
import com.oldspaceman.security.hashPassword
import com.oldspaceman.util.Response
import io.ktor.http.*

//implementation our UserRepository

class AuthRepositoryImpl(
    //need user dao
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun signUp(params: SignUpParams): Response<AuthResponse> {
        return if (userAlreadyExist(params.email)) {
            Response.Error(
                code = HttpStatusCode.Conflict,
                data = AuthResponse(
                    errorMessage = "The user with this email already exists!"
                )
            )
        }else{
            val insertedUser = userDao.insert(params = params)

            //data base error - could not insert new user
            if (insertedUser == null){
                Response.Error(
                    code = HttpStatusCode.InternalServerError,
                    data = AuthResponse(
                        errorMessage = "Ooops, sorry we could not register the user, try later!"
                    )
                )
            }else{
                Response.Success(
                    data = AuthResponse(
                        data = AuthResponseData(
                            id = insertedUser.id,
                            name = insertedUser.name,
                            bio = insertedUser.bio,
                            token = generateToken(params.email) // will be later
                        )
                    )
                )
            }
        }
    }




    override suspend fun signIn(params: SignInParams): Response<AuthResponse> {
        val user = userDao.findByEmail(params.email)

        return if (user == null){
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = AuthResponse(
                    errorMessage = "Invalid credentials, no user with this email!"
                )
            )
        } else {
            val hashedPassword = hashPassword(params.password)
            // like user has hash password now - we equal this date

            if (user.password == hashedPassword){ // params.password
                Response.Success(
                    data = AuthResponse(
                        data = AuthResponseData(
                            id = user.id,
                            name = user.name,
                            bio = user.bio,
                            token = generateToken(params.email),
                                // also verified what token is not null
                                // we pass this = Security.kt
                            followingCount = user.followingCount,
                            followersCount = user.followersCount
                        )
                    )
                )
            } else {
                Response.Error(
                    code = HttpStatusCode.Forbidden, // Запрещенный
                    data = AuthResponse(
                        errorMessage = "Invalid credentials, wrong password!"
                    )
                )
            }
        }


    }

    //some user with email don't exist - -create служебный utility method
    // if email exist -> true
    private suspend fun userAlreadyExist(email: String): Boolean{
        return userDao.findByEmail(email) != null
    }
}









