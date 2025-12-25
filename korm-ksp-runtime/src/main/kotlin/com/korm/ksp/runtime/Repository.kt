package com.korm.ksp.runtime

interface Repository<T, ID> {
    fun save(entity: T): T
    fun findById(id: ID): T?
    fun findAll(): List<T>
    fun delete(entity: T)
    fun deleteById(id: ID)
    fun count(): Long
    fun existsById(id: ID): Boolean
}