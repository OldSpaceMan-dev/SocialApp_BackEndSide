package com.oldspaceman.route

import com.oldspaceman.model.AuthResponse
import com.oldspaceman.model.SignInParams
import com.oldspaceman.model.SignUpParams
import com.oldspaceman.repository.auth.AuthRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

// extension расширение method
fun Routing.authRouting(){
    val repository by inject<AuthRepository>()

    // signUp Logic
    route(path = "/signup"){
        //post request
        post {

            // kotlin serialisation will take JSON object that will be passed and deserialize
            //and signup params create object
            val params = call.receiveNullable<SignUpParams>()

            // could not desalinize JSON
            if (params == null){
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = AuthResponse(
                        errorMessage = "Invalid credentials!" //Недопустимые учетные данные
                    )
                )

                return@post
            }

            val result = repository.signUp(params = params)
            call.respond(
                status = result.code,
                message = result.data
            )

        }

    }

    // singIn logic
    route(path = "/login"){
        //post request
        post {

            // kotlin serialisation will take JSON object that will be passed and deserialize
            //and signup params create object
            val params = call.receiveNullable<SignInParams>()

            // could not desalinize JSON
            if (params == null){
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = AuthResponse(
                        errorMessage = "Invalid credentials!" //Недопустимые учетные данные
                    )
                )

                return@post
            }

            val result = repository.signIn(params = params)
            call.respond(
                status = result.code,
                message = result.data
            )
        }

    }


}