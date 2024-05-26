package com.oldspaceman.repository.auth

import com.oldspaceman.model.AuthResponse
import com.oldspaceman.model.SignInParams
import com.oldspaceman.model.SignUpParams
import com.oldspaceman.util.Response

interface AuthRepository {
    suspend fun signUp(params: SignUpParams): Response<AuthResponse>
    suspend fun signIn(params: SignInParams): Response<AuthResponse>

}

