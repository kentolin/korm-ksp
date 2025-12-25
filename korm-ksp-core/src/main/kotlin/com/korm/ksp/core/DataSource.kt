package com.korm.ksp.core

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import javax.sql.DataSource as JdbcDataSource

class DataSource(config: DatabaseConfig) {
    private val hikariDataSource: HikariDataSource

    init {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.url
            driverClassName = config.driver
            username = config.username
            password = config.password
            maximumPoolSize = config.maxPoolSize
            minimumIdle = config.minIdle
            connectionTimeout = config.connectionTimeout
            idleTimeout = config.idleTimeout
            maxLifetime = config.maxLifetime
        }
        hikariDataSource = HikariDataSource(hikariConfig)
    }

    fun getConnection(): Connection = hikariDataSource.connection

    fun getDataSource(): JdbcDataSource = hikariDataSource

    fun close() {
        if (!hikariDataSource.isClosed) {
            hikariDataSource.close()
        }
    }
}