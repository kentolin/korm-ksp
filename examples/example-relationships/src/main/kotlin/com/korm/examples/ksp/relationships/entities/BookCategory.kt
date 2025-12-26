package com.korm.examples.ksp.relationships.entities

import com.korm.ksp.annotations.*

@Entity
@Table(name = "book_categories")
data class BookCategory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "book_id", nullable = false)
    val bookId: Long,

    @Column(name = "category_id", nullable = false)
    val categoryId: Long
)
