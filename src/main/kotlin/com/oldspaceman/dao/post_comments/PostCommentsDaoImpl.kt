package com.oldspaceman.dao.post_comments

import com.oldspaceman.dao.DatabaseFactory.dbQuery
import com.oldspaceman.dao.user.UserTable
import com.oldspaceman.util.IdGenerator
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class PostCommentsDaoImpl : PostCommentsDao {
    override suspend fun addComment(postId: Long, userId: Long, content: String): PostCommentRow? {
        return dbQuery {
            val commentId = IdGenerator.generateId()

            PostCommentsTable.insert {
                it[PostCommentsTable.commentId] = commentId
                it[PostCommentsTable.postId] = postId
                it[PostCommentsTable.userId] = userId
                it[PostCommentsTable.content] = content
            }

            PostCommentsTable
                .join(
                    onColumn = PostCommentsTable.userId,
                    otherColumn = UserTable.id,
                    otherTable = UserTable,
                    joinType = JoinType.INNER
                )
                .select { (PostCommentsTable.postId eq postId) and (PostCommentsTable.commentId eq commentId) }
                .singleOrNull()
                ?.let { toPostCommentRow(it) }
        }
    }

    override suspend fun removeComment(commentId: Long, postId: Long): Boolean {
        return dbQuery {
            PostCommentsTable.deleteWhere {
                (PostCommentsTable.commentId eq commentId) and (PostCommentsTable.postId eq postId)
            } > 0
        }
    }

    override suspend fun findComment(commentId: Long, postId: Long): PostCommentRow? {
        return dbQuery{
            PostCommentsTable
                .join(
                    onColumn = PostCommentsTable.userId, // присоединение по userId из PostCommentsTable
                    otherColumn = UserTable.id, // присоединение по id из UserTable
                    otherTable = UserTable,
                    joinType = JoinType.INNER // возвращает только строки, удовлетворяющие условию объединения.
                )
                .select{ (PostCommentsTable.postId eq postId) and (PostCommentsTable.commentId eq commentId) }
                .singleOrNull()
            //if we get not null -> convert postcommenRow
                ?.let { toPostCommentRow(it) }
        }
    }

    override suspend fun getComments(postId: Long, pageNumber: Int, pageSize: Int): List<PostCommentRow> {
        return dbQuery {
            PostCommentsTable
                .join(
                    onColumn = PostCommentsTable.userId,
                    otherColumn = UserTable.id,
                    otherTable = UserTable,
                    joinType = JoinType.INNER
                )
                .select(where = {PostCommentsTable.postId eq postId} )
                .orderBy(column = PostCommentsTable.createdAt, SortOrder.DESC)
                .limit(n = pageSize, offset = ( (pageNumber - 1) * pageSize).toLong() )
                .map { toPostCommentRow(it) }
        }
    }


    private fun toPostCommentRow(resultRow: ResultRow): PostCommentRow {
       return PostCommentRow(
           commentId = resultRow[PostCommentsTable.commentId],
           content = resultRow[PostCommentsTable.content],
           postId = resultRow[PostCommentsTable.postId],
           userId = resultRow[PostCommentsTable.userId],
           createdAt = resultRow[PostCommentsTable.createdAt].toString(),
           userName = resultRow[UserTable.name],
           userImageUrl = resultRow[UserTable.imageUrl]
       )
    }



}