package com.korm.ksp.annotations

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class ManyToOne(
    val cascade: Array<CascadeType> = [],
    val fetch: FetchType = FetchType.EAGER,
    val optional: Boolean = true
)
