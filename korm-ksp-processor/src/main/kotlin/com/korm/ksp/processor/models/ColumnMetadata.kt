package com.korm.ksp.processor.models

data class ColumnMetadata(
    val propertyName: String,
    val columnName: String,
    val kotlinType: String,
    val sqlType: String,
    val isId: Boolean = false,
    val isGenerated: Boolean = false,
    val isNullable: Boolean = true,
    val isUnique: Boolean = false,
    val length: Int = 255
)