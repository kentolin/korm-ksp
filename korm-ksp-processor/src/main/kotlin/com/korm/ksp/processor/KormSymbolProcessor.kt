package com.korm.ksp.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.validate
import com.korm.ksp.processor.generators.RepositoryGenerator
import com.korm.ksp.processor.models.ColumnMetadata
import com.korm.ksp.processor.models.EntityMetadata
import com.korm.ksp.processor.utils.NameResolver
import com.korm.ksp.processor.utils.TypeMapper

class KormSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private val repositoryGenerator = RepositoryGenerator(codeGenerator)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val entitySymbols = resolver
            .getSymbolsWithAnnotation("com.korm.ksp.annotations.Entity")
            .filterIsInstance<KSClassDeclaration>()

        if (!entitySymbols.iterator().hasNext()) {
            return emptyList()
        }

        entitySymbols.forEach { classDeclaration ->
            if (classDeclaration.validate()) {
                processEntity(classDeclaration)
            }
        }

        return entitySymbols.filterNot { it.validate() }.toList()
    }

    private fun processEntity(classDeclaration: KSClassDeclaration) {
        logger.info("Processing entity: ${classDeclaration.simpleName.asString()}")

        val packageName = classDeclaration.packageName.asString()
        val className = classDeclaration.simpleName.asString()

        // Get table name from @Table annotation or use class name
        val tableAnnotation = classDeclaration.annotations
            .find { it.shortName.asString() == "Table" }
        val tableName = tableAnnotation?.arguments
            ?.find { it.name?.asString() == "name" }
            ?.value as? String
            ?: NameResolver.toTableName(className)

        // Process properties
        val columns = classDeclaration.getAllProperties()
            .filter { !it.hasAnnotation("com.korm.ksp.annotations.Transient") }
            .map { property -> processProperty(property) }
            .toList()

        val metadata = EntityMetadata(
            packageName = packageName,
            className = className,
            tableName = tableName,
            columns = columns
        )

        // Generate repository
        repositoryGenerator.generate(metadata)

        logger.info("Generated repository for: $className")
    }

    private fun processProperty(property: KSPropertyDeclaration): ColumnMetadata {
        val propertyName = property.simpleName.asString()
        val kotlinType = property.type.resolve().declaration.simpleName.asString() +
                if (property.type.resolve().isMarkedNullable) "?" else ""

        // Get column name from @Column annotation or use property name
        val columnAnnotation = property.annotations
            .find { it.shortName.asString() == "Column" }
        val columnName = columnAnnotation?.arguments
            ?.find { it.name?.asString() == "name" }
            ?.value as? String
            ?: NameResolver.toColumnName(propertyName)

        val nullable = columnAnnotation?.arguments
            ?.find { it.name?.asString() == "nullable" }
            ?.value as? Boolean
            ?: property.type.resolve().isMarkedNullable

        val unique = columnAnnotation?.arguments
            ?.find { it.name?.asString() == "unique" }
            ?.value as? Boolean
            ?: false

        val length = columnAnnotation?.arguments
            ?.find { it.name?.asString() == "length" }
            ?.value as? Int
            ?: 255

        val isId = property.hasAnnotation("com.korm.ksp.annotations.Id")
        val isGenerated = property.hasAnnotation("com.korm.ksp.annotations.GeneratedValue")

        val sqlType = TypeMapper.kotlinToSql(kotlinType)

        return ColumnMetadata(
            propertyName = propertyName,
            columnName = columnName,
            kotlinType = kotlinType,
            sqlType = sqlType,
            isId = isId,
            isGenerated = isGenerated,
            isNullable = nullable,
            isUnique = unique,
            length = length
        )
    }

    private fun KSPropertyDeclaration.hasAnnotation(qualifiedName: String): Boolean {
        return annotations.any {
            it.annotationType.resolve().declaration.qualifiedName?.asString() == qualifiedName
        }
    }
}