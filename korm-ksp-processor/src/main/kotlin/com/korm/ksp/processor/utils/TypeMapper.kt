package com.korm.ksp.processor.utils

object TypeMapper {
    fun kotlinToSql(kotlinType: String): String {
        return when (kotlinType.removeSuffix("?")) {
            "String" -> "VARCHAR"
            "Int" -> "INTEGER"
            "Long" -> "BIGINT"
            "Boolean" -> "BOOLEAN"
            "Double" -> "DOUBLE"
            "Float" -> "FLOAT"
            "java.time.LocalDateTime" -> "TIMESTAMP"
            "java.time.LocalDate" -> "DATE"
            "java.time.LocalTime" -> "TIME"
            "java.util.UUID" -> "UUID"
            "ByteArray" -> "BYTEA"
            else -> "VARCHAR"
        }
    }
}