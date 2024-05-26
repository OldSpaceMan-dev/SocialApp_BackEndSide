package com.oldspaceman.dao.follows

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object FollowsTable : Table(name = "follows"){
    val followerId = long(name = "follower_id")  //id persons is following
    val followingId = long(name = "following_id")//id persons is follower
    //перехватывать данные
    val followData = datetime(name = "follow_data").defaultExpression(defaultValue = CurrentDateTime)
}