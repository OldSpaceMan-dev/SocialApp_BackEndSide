package com.oldspaceman.util

import io.ktor.http.*

// запечатанный класс
sealed class Response<T>(
    val code: HttpStatusCode = HttpStatusCode.OK,
    val data: T
) {
    class Success<T>(data: T): Response<T>(data = data)

    class Error<T>(
        code: HttpStatusCode,
        data: T
    ): Response<T>(
        code = code,
        data = data
    )

}