package com.korm.ksp.runtime

interface CrudRepository<T, ID> : Repository<T, ID> {
    fun saveAll(entities: List<T>): List<T>
    fun update(entity: T): T
    fun deleteAll()
    fun deleteAll(entities: List<T>)
}