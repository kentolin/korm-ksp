package com.korm.ksp.processor.generators

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.korm.ksp.processor.models.EntityMetadata
import java.io.OutputStream

class RepositoryGenerator(private val codeGenerator: CodeGenerator) {

    fun generate(metadata: EntityMetadata) {
        val fileName = "${metadata.className}Repository"
        val file = codeGenerator.createNewFile(
            dependencies = Dependencies(false),
            packageName = metadata.packageName,
            fileName = fileName
        )

        file.use { output ->
            output.appendText(generateRepository(metadata))
        }
    }

    private fun generateRepository(metadata: EntityMetadata): String {
        val idColumn = metadata.idColumn ?: error("Entity ${metadata.className} must have an @Id field")
        val idType = idColumn.kotlinType

        return """
package ${metadata.packageName}

import com.korm.ksp.runtime.CrudRepository
import com.korm.ksp.runtime.Database
import com.korm.ksp.core.exceptions.EntityNotFoundException
import java.sql.Connection
import java.sql.ResultSet

class ${metadata.className}Repository(private val database: Database) : CrudRepository<${metadata.className}, $idType> {

    override fun save(entity: ${metadata.className}): ${metadata.className} {
        return database.transaction { connection ->
            ${if (idColumn.isGenerated) generateInsertReturning(metadata) else generateInsert(metadata)}
        }
    }

    override fun findById(id: $idType): ${metadata.className}? {
        return database.withConnection { connection ->
            val sql = "SELECT * FROM ${metadata.tableName} WHERE ${idColumn.columnName} = ?"
            connection.prepareStatement(sql).use { stmt ->
                stmt.setObject(1, id)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) mapResultSet(rs) else null
                }
            }
        }
    }

    override fun findAll(): List<${metadata.className}> {
        return database.withConnection { connection ->
            val sql = "SELECT * FROM ${metadata.tableName}"
            connection.prepareStatement(sql).use { stmt ->
                stmt.executeQuery().use { rs ->
                    val result = mutableListOf<${metadata.className}>()
                    while (rs.next()) {
                        result.add(mapResultSet(rs))
                    }
                    result
                }
            }
        }
    }

    override fun update(entity: ${metadata.className}): ${metadata.className} {
        return database.transaction { connection ->
            ${generateUpdate(metadata)}
        }
    }

    override fun delete(entity: ${metadata.className}) {
        deleteById(entity.${idColumn.propertyName})
    }

    override fun deleteById(id: $idType) {
        database.transaction { connection ->
            val sql = "DELETE FROM ${metadata.tableName} WHERE ${idColumn.columnName} = ?"
            connection.prepareStatement(sql).use { stmt ->
                stmt.setObject(1, id)
                stmt.executeUpdate()
            }
        }
    }

    override fun count(): Long {
        return database.withConnection { connection ->
            val sql = "SELECT COUNT(*) FROM ${metadata.tableName}"
            connection.prepareStatement(sql).use { stmt ->
                stmt.executeQuery().use { rs ->
                    if (rs.next()) rs.getLong(1) else 0L
                }
            }
        }
    }

    override fun existsById(id: $idType): Boolean {
        return database.withConnection { connection ->
            val sql = "SELECT 1 FROM ${metadata.tableName} WHERE ${idColumn.columnName} = ?"
            connection.prepareStatement(sql).use { stmt ->
                stmt.setObject(1, id)
                stmt.executeQuery().use { rs ->
                    rs.next()
                }
            }
        }
    }

    override fun saveAll(entities: List<${metadata.className}>): List<${metadata.className}> {
        return entities.map { save(it) }
    }

    override fun deleteAll() {
        database.transaction { connection ->
            val sql = "DELETE FROM ${metadata.tableName}"
            connection.prepareStatement(sql).use { stmt ->
                stmt.executeUpdate()
            }
        }
    }

    override fun deleteAll(entities: List<${metadata.className}>) {
        entities.forEach { delete(it) }
    }

    private fun mapResultSet(rs: ResultSet): ${metadata.className} {
        return ${metadata.className}(
            ${metadata.columns.joinToString(",\n            ") { column ->
            "${column.propertyName} = ${generateResultSetMapping(column)}"
        }}
        )
    }
}
""".trimIndent()
    }

    private fun generateInsert(metadata: EntityMetadata): String {
        val nonIdColumns = metadata.columns.filter { !it.isGenerated }
        val columnNames = nonIdColumns.joinToString(", ") { it.columnName }
        val placeholders = nonIdColumns.joinToString(", ") { "?" }

        return """
            val sql = "INSERT INTO ${metadata.tableName} ($columnNames) VALUES ($placeholders)"
            connection.prepareStatement(sql).use { stmt ->
                ${nonIdColumns.mapIndexed { index, col ->
            "stmt.setObject(${index + 1}, entity.${col.propertyName})"
        }.joinToString("\n                ")}
                stmt.executeUpdate()
            }
            entity
        """.trimIndent()
    }

    private fun generateInsertReturning(metadata: EntityMetadata): String {
        val idColumn = metadata.idColumn!!
        val nonIdColumns = metadata.columns.filter { !it.isId }
        val columnNames = nonIdColumns.joinToString(", ") { it.columnName }
        val placeholders = nonIdColumns.joinToString(", ") { "?" }

        return """
            val sql = "INSERT INTO ${metadata.tableName} ($columnNames) VALUES ($placeholders)"
            connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                ${nonIdColumns.mapIndexed { index, col ->
            "stmt.setObject(${index + 1}, entity.${col.propertyName})"
        }.joinToString("\n                ")}
                stmt.executeUpdate()
                stmt.generatedKeys.use { rs ->
                    if (rs.next()) {
                        entity.copy(${idColumn.propertyName} = rs.getObject(1) as ${idColumn.kotlinType})
                    } else {
                        entity
                    }
                }
            }
        """.trimIndent()
    }

    private fun generateUpdate(metadata: EntityMetadata): String {
        val idColumn = metadata.idColumn!!
        val nonIdColumns = metadata.columns.filter { !it.isId }
        val setClause = nonIdColumns.joinToString(", ") { "${it.columnName} = ?" }

        return """
            val sql = "UPDATE ${metadata.tableName} SET $setClause WHERE ${idColumn.columnName} = ?"
            connection.prepareStatement(sql).use { stmt ->
                ${nonIdColumns.mapIndexed { index, col ->
            "stmt.setObject(${index + 1}, entity.${col.propertyName})"
        }.joinToString("\n                ")}
                stmt.setObject(${nonIdColumns.size + 1}, entity.${idColumn.propertyName})
                stmt.executeUpdate()
            }
            entity
        """.trimIndent()
    }

    private fun generateResultSetMapping(column: com.korm.ksp.processor.models.ColumnMetadata): String {
        return when (column.kotlinType.removeSuffix("?")) {
            "String" -> "rs.getString(\"${column.columnName}\")"
            "Int" -> "rs.getInt(\"${column.columnName}\")"
            "Long" -> "rs.getLong(\"${column.columnName}\")"
            "Boolean" -> "rs.getBoolean(\"${column.columnName}\")"
            "Double" -> "rs.getDouble(\"${column.columnName}\")"
            "Float" -> "rs.getFloat(\"${column.columnName}\")"
            else -> "rs.getObject(\"${column.columnName}\") as ${column.kotlinType}"
        } + if (column.isNullable) "" else ""
    }

    private fun OutputStream.appendText(text: String) {
        write(text.toByteArray())
    }
}
