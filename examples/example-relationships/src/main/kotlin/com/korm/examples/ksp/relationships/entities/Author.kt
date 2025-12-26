package com.korm.examples.ksp.relationships.entities

import com.korm.ksp.annotations.*

@Entity
@Table(name = "authors")
data class Author(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = true)
    val email: String? = null
)
