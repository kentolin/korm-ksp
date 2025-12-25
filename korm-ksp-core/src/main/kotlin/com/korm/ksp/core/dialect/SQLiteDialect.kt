package com.korm.ksp.core.dialect

class SQLiteDialect : Dialect {
    override fun getDriverClassName() = "org.sqlite.JDBC"

    override fun getAutoIncrementKeyword() = "AUTOINCREMENT"

    override fun getBooleanType() = "INTEGER"

    override fun getTextType() = "TEXT"

    override fun supportsReturning() = true

    override fun getLimit(offset: Int?, limit: Int?): String {
        return buildString {
            if (limit != null) append(" LIMIT $limit")
            if (offset != null) append(" OFFSET $offset")
        }
    }
}