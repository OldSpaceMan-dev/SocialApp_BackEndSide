package com.oldspaceman.dao

import com.oldspaceman.dao.follows.FollowsTable
import com.oldspaceman.dao.post.PostTable
import com.oldspaceman.dao.post_comments.PostCommentsTable
import com.oldspaceman.dao.post_likes.PostLikesTable
import com.oldspaceman.dao.user.UserRow
import com.oldspaceman.dao.user.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    //method to connect my database
    fun init(){
        Database.connect(createHikariDataSource())
        transaction {
            // create dataBase UserRow / FollowsTable et
            SchemaUtils.create(UserTable, FollowsTable, PostTable, PostLikesTable, PostCommentsTable)
        }
    }

    private fun createHikariDataSource(): HikariDataSource {
        val driverClass = "org.postgresql.Driver"
        //adress database == defolt 5432 port
        // Получение параметров из переменных окружения с fallback на значения по умолчанию
        val jdbcUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/socialmediadb"
        val username = System.getenv("DB_USERNAME") ?: "arkadiyblank"
        val password = System.getenv("DB_PASSWORD") ?: "Arkadiy007"

        val hikariConfig = HikariConfig().apply {
            //configuration for hikari
            driverClassName = driverClass
            setJdbcUrl(jdbcUrl)
            setUsername(username) //TODO можно удалить
            setPassword(password) //TODO можно удалить
            maximumPoolSize = 3 // мах колл-во соединений с БД - переиспользуются
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        return HikariDataSource(hikariConfig)
    }

    // Метод для выполнения запросов к базе данных
    //generic helper method to execute on database queries
    // take suspend fun and return T type
    suspend fun <T> dbQuery(block: suspend () -> T) =
        newSuspendedTransaction(Dispatchers.IO) {block() }

}








