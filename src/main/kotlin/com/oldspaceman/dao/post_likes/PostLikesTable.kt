package com.oldspaceman.dao.post_likes

import com.oldspaceman.dao.post.PostTable
import com.oldspaceman.dao.user.UserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object PostLikesTable: Table(name = "post_likes"){
    val likeId = long(name = "like_id").uniqueIndex()

    val postId = long(name = "post_id")
        .references(
            ref = PostTable.postId,
            //if user remove from Post Table -> remove this posts Likes Table (onDelete)
            onDelete = ReferenceOption.CASCADE
        )

    val userId = long(name = "user_id")
        .references(
            ref = UserTable.id,
            onDelete = ReferenceOption.CASCADE
        )

    val likeDate = datetime(name = "like_date").defaultExpression(defaultValue = CurrentDateTime)

}