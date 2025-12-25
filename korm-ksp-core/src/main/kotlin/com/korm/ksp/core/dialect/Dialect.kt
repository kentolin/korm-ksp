package com.korm.ksp.core.dialect

interface Dialect {
    fun getDriverClassName(): String
    fun getAutoIncrementKeyword(): String
    fun getBooleanType(): String
    fun getTextType(): String
    fun supportsReturning(): Boolean
    fun getLimit(offset: Int?, limit: Int?): String
}