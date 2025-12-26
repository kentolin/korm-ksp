package com.korm.ksp.annotations

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class OneToOne(
    val mappedBy: String = "",
    val cascade: Array<CascadeType> = [],
    val fetch: FetchType = FetchType.LAZY,
    val optional: Boolean = true
)

enum class CascadeType {
    ALL, PERSIST, MERGE, REMOVE, REFRESH, DETACH
}

enum class FetchType {
    LAZY, EAGER
}
