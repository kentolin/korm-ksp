package com.korm.ksp.core.dialect

class MySQLDialect : Dialect {
    override fun getDriverClassName() = "com.mysql.cj.jdbc.Driver"

    override fun getAutoIncrementKeyword() = "AUTO_INCREMENT"

    override fun getBooleanType() = "TINYINT(1)"

    override fun getTextType() = "TEXT"

    override fun supportsReturning() = false

    override fun getLimit(offset: Int?, limit: Int?): String {
        return buildString {
            if (limit != null) {
                append(" LIMIT ")
                if (offset != null) append("$offset, ")
                append(limit)
            }
        }
    }
}