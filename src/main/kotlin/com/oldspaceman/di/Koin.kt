package com.oldspaceman.di

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin


//extension(расширение) method

fun Application.configureDI(){
    install(Koin){
        modules(appModule)
    }
}

