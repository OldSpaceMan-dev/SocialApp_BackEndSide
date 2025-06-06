package com.oldspaceman.dao.post_comments

import com.oldspaceman.dao.post.PostTable
import com.oldspaceman.dao.user.UserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object PostCommentsTable : Table(name = "post_comments"){
    val commentId = long(name = "comment_id").uniqueIndex()
    val postId = long(name = "post_id")
        //if post remove from post Table -> remove this posts Table (onDelete)
        .references(
            ref = PostTable.postId,
            onDelete = ReferenceOption.CASCADE
        )
    val userId = long(name = "user_id")
        .references(
            ref = UserTable.id,
            onDelete = ReferenceOption.CASCADE
        )
    val content = varchar(name = "content", length = 300)
    val createdAt = datetime(name = "created_at").defaultExpression(defaultValue = CurrentDateTime)
}

data class PostCommentRow(
    val commentId: Long,
    val content: String,
    val postId: Long,
    val userId: Long,
    //from user table
    val userName: String,
    val userImageUrl: String?,

    val createdAt: String
)