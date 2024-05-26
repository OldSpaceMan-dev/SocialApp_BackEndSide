package com.oldspaceman.util

import io.ktor.http.content.*
import java.io.File
import java.util.UUID

//save Image
fun PartData.FileItem.saveFile(folderPath: String): String{

    // формирует уникальное имя файла, объединяя уникальный идентификатор
    // и расширение оригинального имени файла в одну строку. ->
    val fileName = "${UUID.randomUUID()}.${File(originalFileName as String).extension}"

    //содержать массив байтов, представляющих содержимое файла, считанного из потока данных
    val fileBytes = streamProvider().readBytes()

    val folder = File(folderPath)
    folder.mkdirs()
    //save image
    File("$folder/$fileName").writeBytes(fileBytes)
    return fileName

}


// ->
//.extension вызывает метод, который извлекает расширение файла из строки имени файла.
//- Например, если originalFileName содержит "example.jpg", то ".extension" вернет "jpg".