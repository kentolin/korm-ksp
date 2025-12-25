package com.korm.ksp.core

data class DatabaseConfig(
    val url: String,
    val driver: String,
    val username: String = "",
    val password: String = "",
    val maxPoolSize: Int = 10,
    val minIdle: Int = 5,
    val connectionTimeout: Long = 30000,
    val idleTimeout: Long = 600000,
    val maxLifetime: Long = 1800000
)