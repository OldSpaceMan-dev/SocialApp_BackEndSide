package com.oldspaceman.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.oldspaceman.dao.user.UserDao
import com.oldspaceman.model.AuthResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject


private val jwtAudience = System.getenv("jwt.audience")
private val jwtIssuer =  System.getenv("jwt.domain")
private val jwtSecret = System.getenv("jwt.secret")

private const val CLAIM = "email" // объявить

//private val jwtRealm = "ktor sample app"


fun Application.configureSecurity() {
    val userDao by inject<UserDao>()

    // Please read the jwt property from the config file if you are using EngineMain

    //val jwtAudience = "jwt-audience"
    //val jwtDomain = "https://jwt-provider-domain/"


    authentication {
        jwt {

            //realm = jwtRealm

            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim(CLAIM).asString() != null) {
                    //user actually exist in our database
                    val userExists = userDao.findByEmail(email = credential.payload.getClaim(CLAIM).asString()) != null
                    //JWT token содержится in audience
                    val isValidAudience = credential.payload.audience.contains(jwtAudience)

                    if (userExists && isValidAudience) {
                        //return JWT principle - email зарегестирован в нашей database
                        JWTPrincipal(payload = credential.payload)
                    } else {
                        null
                    }
                }else{
                    //if token doesn't contain email as a claim (как требовалось)
                    null// it means token is invalid
                }

                //don't need to do - like JWT token do it for us
                //if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                //is token is invalid
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = AuthResponse(
                        errorMessage = "Token is not valid or has expired" //истекший
                    )
                )
            }
        }
    }
}

//generate JWT token for our user
//once they log in or sign up


fun generateToken(email: String): String{
    return JWT.create()
        .withAudience(jwtAudience)
        .withIssuer(jwtIssuer)
        .withClaim(CLAIM, email)//user email, validation over user token that be provided to our server
        //.withExpiresAt() // срок годности
        .sign(Algorithm.HMAC256(jwtSecret))
}



















