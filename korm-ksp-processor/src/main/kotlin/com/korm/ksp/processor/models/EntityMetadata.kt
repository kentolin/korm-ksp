package com.korm.ksp.processor.models

data class EntityMetadata(
    val packageName: String,
    val className: String,
    val tableName: String,
    val columns: List<ColumnMetadata>
) {
    val idColumn: ColumnMetadata?
        get() = columns.firstOrNull { it.isId }
}