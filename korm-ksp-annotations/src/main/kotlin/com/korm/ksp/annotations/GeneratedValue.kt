package com.korm.ksp.annotations

enum class GenerationType {
    AUTO,
    IDENTITY,
    SEQUENCE,
    UUID
}

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class GeneratedValue(
    val strategy: GenerationType = GenerationType.AUTO,
    val sequenceName: String = ""
)