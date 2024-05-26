package com.oldspaceman.dao.follows

import com.oldspaceman.dao.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.*

class FollowsDaoImpl : FollowsDao {

    override suspend fun followUser(follower: Long, following: Long): Boolean {
        return dbQuery{
            //вставить
            val insertStatement = FollowsTable.insert {
                it[followerId] = follower
                it[followingId] = following
            }
            insertStatement.resultedValues?.singleOrNull() != null
        }
    }

    override suspend fun unfollowUser(follower: Long, following: Long): Boolean {
        return dbQuery {
            FollowsTable.deleteWhere {
                (followerId eq follower) and (followingId eq following)
            } > 0 // it's means that the record has been removed from the follow table
        }
    }



    override suspend fun getFollowers(userId: Long, pageNumber: Int, pageSize: Int): List<Long> {
        return dbQuery {
            FollowsTable.select {
                FollowsTable.followingId eq userId // select followingId == userId - represent where people being Followed
            }
                .orderBy(FollowsTable.followData, SortOrder.DESC) // sort from Z-A
                // size of max number of record we need to return and get offset
                .limit(n = pageSize, offset = ((pageNumber - 1) * pageSize).toLong())
                .map { it[FollowsTable.followerId] } // after need to map only get
        }
    }

    override suspend fun getFollowing(userId: Long, pageNumber: Int, pageSize: Int): List<Long> {
        return dbQuery {
            FollowsTable.select {
                FollowsTable.followerId eq userId // select followingId == userId
            }
                .orderBy(FollowsTable.followData, SortOrder.DESC) // sort from Z-A
                // size of max number of record we need to return and get offset
                .limit(n = pageSize, offset = ((pageNumber - 1) * pageSize).toLong())
                .map { it[FollowsTable.followingId] } // after need to map only get
        }
    }

    override suspend fun getAllFollowing(userId: Long): List<Long> {
        return dbQuery {
            FollowsTable
                .select { FollowsTable.followerId eq userId }
                .map { it[FollowsTable.followingId] }
        }
    }


    override suspend fun isAlreadyFollowing(follower: Long, following: Long): Boolean {
        return dbQuery {
            val queryResult = FollowsTable.select {
                (FollowsTable.followerId eq follower) and (FollowsTable.followingId eq following)
            }
            queryResult.toList().isNotEmpty()
        }
    }
}













