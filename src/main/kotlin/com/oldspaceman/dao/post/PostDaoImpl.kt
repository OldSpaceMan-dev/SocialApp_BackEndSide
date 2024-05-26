package com.oldspaceman.dao.post

import com.oldspaceman.dao.DatabaseFactory.dbQuery
import com.oldspaceman.dao.user.UserTable
import com.oldspaceman.util.IdGenerator
import kotlinx.coroutines.selects.select
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus

class PostDaoImpl : PostDao {


    override suspend fun createPost(caption: String, imageUrl: String, userId: Long): Boolean {
        return dbQuery{
            val insertStatement = PostTable.insert {
                it[postId] = IdGenerator.generateId()
                it[PostTable.caption] = caption // заголовок
                it[PostTable.imageUrl] = imageUrl
                it[likesCount] = 0
                it[commentsCount] = 0
                it[PostTable.userId] = userId
            }
            insertStatement.resultedValues?.singleOrNull() != null
        }
    }



    // if follows has 1 elemrnt = not followed anyone and
    // ID pass in this list = ID this user
    override suspend fun getFeedsPost(
        userId: Long,
        follows: List<Long>,
        pageNumber: Int,
        pageSize: Int
    ): List<PostRow> {
        return dbQuery {
            if (follows.size > 1){
                getPosts(users = follows, pageNumber = pageNumber, pageSize = pageSize)
            }else{
                PostTable
                    .join(
                        otherTable = UserTable,
                        onColumn = PostTable.userId,
                        otherColumn = UserTable.id,
                        joinType = JoinType.INNER
                    )
                    .selectAll()
                    .orderBy(column = PostTable.likesCount, order = SortOrder.DESC) // likesCount = show popular post if you haven't followed yet
                    .limit( n = pageSize, offset = ((pageNumber -1) * pageSize).toLong() )
                    .map { toPostRow(it) }
            }
        }
    }



    override suspend fun getPostByUser(userId: Long, pageNumber: Int, pageSize: Int): List<PostRow> {
        return dbQuery {

            getPosts(users = listOf(userId), pageNumber = pageNumber, pageSize = pageSize)
        }
    }



    override suspend fun getPost(postId: Long): PostRow? {
        return dbQuery {
            PostTable
                .join( //присоединить таблицу UserTable
                    otherTable = UserTable,
                    onColumn = PostTable.userId, // присоединение по userId из PostTable
                    otherColumn = UserTable.id, // присоединение по id из UserTable
                    joinType = JoinType.INNER // возвращает только строки, удовлетворяющие условию объединения.
                )
            // выполняется фильтрация результатов запроса только для строк,
            // где PostTable.postId равен postId.
                .select { PostTable.postId eq postId }
                .singleOrNull()
                ?.let{toPostRow(it)}//Если результат не равен null, то выполняется функция toPostRow
        }
    }

    override suspend fun updateLikesCount(postId: Long, decrement: Boolean): Boolean {
        return dbQuery {
            val value = if (decrement) -1 else 1 // if defolt we update comment +1

            PostTable.update(where = {PostTable.postId eq postId}) {
                it.update(column = likesCount, value = likesCount.plus(value) )
            } > 0
        }
    }


    override suspend fun updateCommentsCount(postId: Long, decrement: Boolean): Boolean {
        return dbQuery {
            val value = if (decrement) -1 else 1 // if defolt we update comment +1

            PostTable.update(where = {PostTable.postId eq postId}) {
                it.update(column = commentsCount, value = commentsCount.plus(value) )
            } > 0
        }
    }


    override suspend fun deletePost(postId: Long): Boolean {
        return dbQuery {
            PostTable.deleteWhere { PostTable.postId eq postId } > 0 // >0 -> post delete success
        }
    }


    //private fun
    private fun getPosts( users: List<Long>, pageNumber: Int, pageSize: Int ): List<PostRow> {
        return PostTable
            .join(
                otherTable = UserTable,
                onColumn = PostTable.userId,
                otherColumn = UserTable.id,
                joinType = JoinType.INNER
            )
            //Выбор строк из базы данных, где значение столбца `userID`
            // из таблицы `PostTable` содержится в списке `follows`
            .select(where = PostTable.userId inList users)
            .orderBy(column = PostTable.createdAt, order = SortOrder.DESC)
            // pageSize - кол-во элементов на странице // pageNumber обозначает номер текущей страницы,
            .limit(n = pageSize, offset = ((pageNumber -1) * pageSize).toLong()) // пагинация
            .map { toPostRow(it) }
    }



    //convert method
    private fun toPostRow(row: ResultRow): PostRow{
        // mapping this row: ResultRow In  PostRow
        return PostRow(
            postId = row[PostTable.postId],
            caption = row[PostTable.caption],
            imageUrl = row[PostTable.imageUrl],
            createdAt = row[PostTable.createdAt].toString(),
            likesCount = row[PostTable.likesCount],
            commentsCount = row[PostTable.commentsCount],
            userId = row[PostTable.userId],
            userName = row[UserTable.name],
            userImageUrl = row[UserTable.imageUrl]
        )
    }
}





