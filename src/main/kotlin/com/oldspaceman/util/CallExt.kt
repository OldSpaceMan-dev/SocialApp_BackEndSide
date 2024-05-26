package com.oldspaceman.util

import com.oldspaceman.model.PostResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*

suspend fun ApplicationCall.getLongParameter(
    name: String,
    isQueryParameter: Boolean = false // paramiter pass on URL ||exp call.parameters["postId"]
): Long{

    val parameter = if (isQueryParameter){ // true -> queryParameters
        request.queryParameters[name]?.toLongOrNull() //-  параметр из строки запроса

    }else{
        parameters[name]?.toLongOrNull() // параметра из тела запроса
    } ?: kotlin.run {
        respond(
            status = HttpStatusCode.BadRequest,
            message = PostResponse(
                success = false,
                message = "Parameter $name is missing or invalid"
            )
        )
        //if значение параметра не может быть преобразовано в тип Long или отсутствует ->BadRE..
        throw BadRequestException("Parameter $name is missing or invalid")
    }
    return parameter
}


/* .
Если он установлен в true, то она пытается получить значение параметра из строки запроса
с помощью request.queryParameters[name]?.toLongOrNull().
Если параметр существует в строке запроса и может быть преобразован в Long,
,то это значение сохраняется в переменной parameter.

Если isQueryParameter установлен в false,
функция пытается получить значение параметра из тела запроса с помощью parameters[name]?.toLongOrNull().
*/