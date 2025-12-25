package com.korm.ksp.core.dialect

class PostgresDialect : Dialect {
    override fun getDriverClassName() = "org.postgresql.Driver"

    override fun getAutoIncrementKeyword() = "SERIAL"

    override fun getBooleanType() = "BOOLEAN"

    override fun getTextType() = "TEXT"

    override fun supportsReturning() = true

    override fun getLimit(offset: Int?, limit: Int?): String {
        return buildString {
            if (limit != null) append(" LIMIT $limit")
            if (offset != null) append(" OFFSET $offset")
        }
    }
}