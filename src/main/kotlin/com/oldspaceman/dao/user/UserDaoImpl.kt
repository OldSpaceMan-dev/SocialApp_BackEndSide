package com.oldspaceman.dao.user

import com.oldspaceman.dao.DatabaseFactory.dbQuery
import com.oldspaceman.dao.post.PostTable
import com.oldspaceman.model.SignUpParams
import com.oldspaceman.security.hashPassword
import com.oldspaceman.util.IdGenerator
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus


//insert and find method for database
class UserDaoImpl : UserDao {

    // insert method take parameter user registration and add it to our database
    override suspend fun insert(params: SignUpParams): UserRow? {
        return dbQuery{
            val insertStatement = UserTable.insert {
                it[id] = IdGenerator.generateId()
                it[name] = params.name
                it[email] = params.email
                //when insert new user we "hide" the password
                it[password] = hashPassword(params.password)//params.password
            }

            insertStatement.resultedValues?.singleOrNull()?.let {
                //convert that our user object
                rowToUser(it)
            }

        }
    }

    override suspend fun findByEmail(email: String): UserRow? {
        return dbQuery {
            UserTable.select { UserTable.email eq email }
                .map { rowToUser(it) }
                .singleOrNull()
        }
    }

    override suspend fun findById(userId: Long): UserRow? {
        return dbQuery {
            UserTable.select { UserTable.id eq userId }
                .map { rowToUser(it) }
                .singleOrNull()//return nullable user Row?
        }
    }


    override suspend fun updateUser(userId: Long, name: String, bio: String, imageUrl: String?): Boolean {
        return dbQuery {
            UserTable.update( where = {UserTable.id eq userId} ){
                it[UserTable.name] = name
                it[UserTable.bio] = bio
                it[UserTable.imageUrl] = imageUrl
            } > 0
        }
    }


    override suspend fun updateFollowsCount(follower: Long, following: Long, isFollowing: Boolean): Boolean {
        return dbQuery {
            val count = if (isFollowing) + 1 else -1

            //for follower record
            val success1 = UserTable.update({ UserTable.id eq follower}){
                //single column
                it.update(column = followingCount, value = followingCount.plus(count) )
            } > 0

            val success2 = UserTable.update({ UserTable.id eq following}){
                //single column
                it.update(column = followersCount, value = followersCount.plus(count) )
            } > 0

            success1 && success2// it returns
        }
    }

    override suspend fun getUsers(ids: List<Long>): List<UserRow> {
        return dbQuery {
            UserTable.select(where = {UserTable.id inList ids})
                .map { rowToUser(it) }
        }
    }

    override suspend fun getPopularUsers(limit: Int): List<UserRow> {
        return dbQuery {
            UserTable.selectAll()
                .orderBy(column = UserTable.followersCount, order = SortOrder.DESC)
                .limit(n = limit)
                .map { rowToUser(it) }
        }
    }







    //like a Map?
    private fun rowToUser(row: ResultRow): UserRow{
        return UserRow(
            id = row[UserTable.id],
            name = row[UserTable.name],
            bio = row[UserTable.bio],
            imageUrl = row[UserTable.imageUrl],
            password = row[UserTable.password],
            followersCount = row[UserTable.followersCount],
            followingCount = row[UserTable.followingCount],
            postCount = row[UserTable.postCount]
        )
    }

}