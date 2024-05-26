package com.oldspaceman.dao.post

import com.oldspaceman.dao.user.UserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime


object PostTable : Table(name = "posts"){
    val postId = long(name = "post_id").uniqueIndex()
    val caption = varchar(name = "caption", length = 300)
    val imageUrl = varchar(name = "image_url", length = 300)
    val likesCount = integer(name = "likes_count")
    val commentsCount = integer(name = "comments_count")

    val userId = long(name = "user_id")
                .references(
                        ref = UserTable.id,
                     //if user remove from user Table -> remove this posts Table (onDelete)
                        onDelete = ReferenceOption.CASCADE
                )
    val createdAt = datetime(name = "created_at").defaultExpression(defaultValue = CurrentDateTime)
}

//represent Post Row result
data class PostRow(
    val postId: Long,
    val caption: String,
    val imageUrl: String,
    val createdAt: String,
    val likesCount: Int,
    val commentsCount: Int,
    val userId: Long,

    //make query and join that with user Table .--. сделайте запрос и соедините его с пользовательской таблицей
    val userName: String,
    val userImageUrl: String?
)















