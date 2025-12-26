package com.korm.examples.ksp.relationships.entities

import com.korm.ksp.annotations.*

@Entity
@Table(name = "categories")
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val name: String,

    @Column(nullable = true)
    val description: String? = null
)
