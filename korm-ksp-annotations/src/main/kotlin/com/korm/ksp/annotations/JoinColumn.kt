package com.korm.ksp.annotations

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class JoinColumn(
    val name: String = "",
    val referencedColumnName: String = "id",
    val nullable: Boolean = true,
    val unique: Boolean = false
)
