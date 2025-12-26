package com.korm.ksp.annotations

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class OneToMany(
    val mappedBy: String = "",
    val cascade: Array<CascadeType> = [],
    val fetch: FetchType = FetchType.LAZY,
    val orphanRemoval: Boolean = false
)
