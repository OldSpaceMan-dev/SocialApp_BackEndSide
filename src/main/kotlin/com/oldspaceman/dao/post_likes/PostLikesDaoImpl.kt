package com.oldspaceman.dao.post_likes

import com.oldspaceman.dao.DatabaseFactory.dbQuery
import com.oldspaceman.util.IdGenerator
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class PostLikesDaoImpl : PostLikesDao {
    override suspend fun addLike(postId: Long, userId: Long): Boolean {
        return dbQuery{
            val insertStatement = PostLikesTable.insert {
                it[likeId] = IdGenerator.generateId()
                it[PostLikesTable.postId] = postId
                it[PostLikesTable.userId] = userId
            }
            insertStatement.resultedValues?.isNotEmpty()?: false// if result null -> false
        }
    }

    override suspend fun removeLike(postId: Long, userId: Long): Boolean {
        return dbQuery {
            PostLikesTable
                .deleteWhere { (PostLikesTable.postId eq postId) and (PostLikesTable.userId eq userId) } > 0
            //>0 record was removed successfully
        }
    }

    override suspend fun isPostLikedByUser(postId: Long, userId: Long): Boolean {
        return dbQuery {
            PostLikesTable
                .select{ (PostLikesTable.postId eq postId) and (PostLikesTable.userId eq userId)}
                .toList()//convert to List
                .isNotEmpty()
        }
    }
}