package com.korm.ksp.annotations

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class JoinTable(
    val name: String = "",
    val joinColumns: Array<JoinColumn> = [],
    val inverseJoinColumns: Array<JoinColumn> = []
)
