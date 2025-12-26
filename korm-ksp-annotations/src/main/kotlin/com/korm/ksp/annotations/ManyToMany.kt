package com.korm.ksp.annotations

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class ManyToMany(
    val mappedBy: String = "",
    val cascade: Array<CascadeType> = [],
    val fetch: FetchType = FetchType.LAZY
)
