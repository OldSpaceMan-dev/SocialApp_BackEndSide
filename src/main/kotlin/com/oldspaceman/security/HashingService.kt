package com.oldspaceman.security

import io.ktor.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

// for hash data
// also password don be seen in our database
//получаем данные из системы окружения

private val ALGORITHM = System.getenv("hash.algorithm")
private val HASH_KEY = System.getenv("hash.secret").toByteArray() // массив байтов
private val hMacKey = SecretKeySpec(HASH_KEY, ALGORITHM) // система окружения

fun hashPassword(password: String): String{
    val hMac = Mac.getInstance(ALGORITHM)
    hMac.init(hMacKey)

    return hex(hMac.doFinal(password.toByteArray()))
}