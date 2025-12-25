package com.korm.ksp.processor.utils

object NameResolver {
    fun toSnakeCase(camelCase: String): String {
        return camelCase.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
    }

    fun toTableName(className: String): String {
        return toSnakeCase(className)
    }

    fun toColumnName(propertyName: String): String {
        return toSnakeCase(propertyName)
    }
}