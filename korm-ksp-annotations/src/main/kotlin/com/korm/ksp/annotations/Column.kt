package com.korm.ksp.annotations

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class Column(
    val name: String = "",
    val nullable: Boolean = true,
    val unique: Boolean = false,
    val length: Int = 255
)