package com.korm.examples.ksp.basic

import com.korm.examples.ksp.basic.entities.User
import com.korm.examples.ksp.basic.entities.UserRepository
import com.korm.ksp.core.DatabaseConfig
import com.korm.ksp.runtime.Database

fun main() {
    // Create database configuration (using H2 in-memory database for testing)
    val config = DatabaseConfig(
        url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        driver = "org.h2.Driver"
    )

    val database = Database(config)

    try {
        // Create table
        database.withConnection { connection ->
            connection.createStatement().use { stmt ->
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(255) NOT NULL UNIQUE,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        age INT
                    )
                """.trimIndent())
            }
        }

        // Create repository
        val userRepository = UserRepository(database)

        // Test CREATE
        println("=== Testing CREATE ===")
        val user1 = User(username = "john_doe", email = "john@example.com", age = 25)
        val savedUser1 = userRepository.save(user1)
        println("Saved: $savedUser1")

        val user2 = User(username = "jane_smith", email = "jane@example.com", age = 30)
        val savedUser2 = userRepository.save(user2)
        println("Saved: $savedUser2")

        // Test READ
        println("\n=== Testing READ ===")
        val foundUser = userRepository.findById(savedUser1.id!!)
        println("Found by ID: $foundUser")

        val allUsers = userRepository.findAll()
        println("All users (${allUsers.size}):")
        allUsers.forEach { println("  - $it") }

        // Test UPDATE
        println("\n=== Testing UPDATE ===")
        val updatedUser = savedUser1.copy(age = 26)
        val updated = userRepository.update(updatedUser)
        println("Updated: $updated")

        // Test COUNT
        println("\n=== Testing COUNT ===")
        val count = userRepository.count()
        println("Total users: $count")

        // Test EXISTS
        println("\n=== Testing EXISTS ===")
        val exists = userRepository.existsById(savedUser1.id!!)
        println("User with ID ${savedUser1.id} exists: $exists")

        // Test DELETE
        println("\n=== Testing DELETE ===")
        userRepository.deleteById(savedUser2.id!!)
        println("Deleted user with ID: ${savedUser2.id}")

        val remainingUsers = userRepository.findAll()
        println("Remaining users (${remainingUsers.size}):")
        remainingUsers.forEach { println("  - $it") }

        println("\n=== All tests completed successfully! ===")

    } catch (e: Exception) {
        println("Error: ${e.message}")
        e.printStackTrace()
    } finally {
        database.close()
    }
}