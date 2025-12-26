package com.korm.examples.ksp.relationships.entities

import com.korm.ksp.annotations.*

@Entity
@Table(name = "profiles")
data class Profile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "author_id", nullable = false, unique = true)
    val authorId: Long,

    @Column(nullable = true)
    val bio: String? = null,

    @Column(nullable = true)
    val website: String? = null
)
