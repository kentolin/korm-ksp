package com.korm.ksp.runtime

import com.korm.ksp.core.DataSource
import com.korm.ksp.core.DatabaseConfig
import java.sql.Connection

class Database(config: DatabaseConfig) {
    private val dataSource = DataSource(config)

    fun <T> transaction(block: (Connection) -> T): T {
        return dataSource.getConnection().use { connection ->
            connection.autoCommit = false
            try {
                val result = block(connection)
                connection.commit()
                result
            } catch (e: Exception) {
                connection.rollback()
                throw e
            }
        }
    }

    fun <T> withConnection(block: (Connection) -> T): T {
        return dataSource.getConnection().use { connection ->
            block(connection)
        }
    }

    fun close() {
        dataSource.close()
    }
}