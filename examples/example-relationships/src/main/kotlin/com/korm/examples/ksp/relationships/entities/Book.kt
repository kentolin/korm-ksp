package com.korm.examples.ksp.relationships.entities

import com.korm.ksp.annotations.*

@Entity
@Table(name = "books")
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val isbn: String,

    @Column(name = "author_id", nullable = false)
    val authorId: Long,

    @Column(nullable = true)
    val publishedYear: Int? = null
)
