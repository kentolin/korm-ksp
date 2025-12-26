package com.korm.examples.ksp.relationships

import com.korm.examples.ksp.relationships.entities.*
import com.korm.ksp.core.DatabaseConfig
import com.korm.ksp.runtime.Database

fun main() {
    val config = DatabaseConfig(
        url = "jdbc:h2:mem:reldb;DB_CLOSE_DELAY=-1",
        driver = "org.h2.Driver"
    )

    val database = Database(config)

    try {
        // Create tables
        database.withConnection { connection ->
            connection.createStatement().use { stmt ->
                // Authors table
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS authors (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        email VARCHAR(255)
                    )
                """.trimIndent())

                // Books table
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS books (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        isbn VARCHAR(255) NOT NULL,
                        author_id BIGINT NOT NULL,
                        published_year INT,
                        FOREIGN KEY (author_id) REFERENCES authors(id)
                    )
                """.trimIndent())

                // Categories table
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS categories (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        description VARCHAR(500)
                    )
                """.trimIndent())

                // Profiles table (OneToOne with Author)
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS profiles (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        author_id BIGINT NOT NULL UNIQUE,
                        bio TEXT,
                        website VARCHAR(255),
                        FOREIGN KEY (author_id) REFERENCES authors(id)
                    )
                """.trimIndent())

                // Book-Category join table (ManyToMany)
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS book_categories (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        book_id BIGINT NOT NULL,
                        category_id BIGINT NOT NULL,
                        FOREIGN KEY (book_id) REFERENCES books(id),
                        FOREIGN KEY (category_id) REFERENCES categories(id),
                        UNIQUE(book_id, category_id)
                    )
                """.trimIndent())
            }
        }

        // Create repositories
        val authorRepo = AuthorRepository(database)
        val bookRepo = BookRepository(database)
        val categoryRepo = CategoryRepository(database)
        val profileRepo = ProfileRepository(database)
        val bookCategoryRepo = BookCategoryRepository(database)

        println("=== RELATIONSHIP EXAMPLES ===\n")

        // 1. Create Authors
        println("1. Creating Authors...")
        val author1 = authorRepo.save(Author(name = "J.K. Rowling", email = "jk@example.com"))
        val author2 = authorRepo.save(Author(name = "George R.R. Martin", email = "grrm@example.com"))
        println("   Created: ${author1.name} (ID: ${author1.id})")
        println("   Created: ${author2.name} (ID: ${author2.id})")

        // 2. Create Profile for Author (OneToOne)
        println("\n2. Creating Profile (OneToOne relationship)...")
        val profile1 = profileRepo.save(
            Profile(
                authorId = author1.id!!,
                bio = "British author, best known for Harry Potter series",
                website = "https://jkrowling.com"
            )
        )
        println("   Profile created for ${author1.name}")
        println("   Bio: ${profile1.bio}")

        // 3. Create Books (ManyToOne with Author)
        println("\n3. Creating Books (ManyToOne relationship with Author)...")
        val book1 = bookRepo.save(
            Book(
                title = "Harry Potter and the Philosopher's Stone",
                isbn = "978-0439708180",
                authorId = author1.id!!,
                publishedYear = 1997
            )
        )
        val book2 = bookRepo.save(
            Book(
                title = "Harry Potter and the Chamber of Secrets",
                isbn = "978-0439064873",
                authorId = author1.id!!,
                publishedYear = 1998
            )
        )
        val book3 = bookRepo.save(
            Book(
                title = "A Game of Thrones",
                isbn = "978-0553103540",
                authorId = author2.id!!,
                publishedYear = 1996
            )
        )
        println("   Created: ${book1.title} by author ID ${book1.authorId}")
        println("   Created: ${book2.title} by author ID ${book2.authorId}")
        println("   Created: ${book3.title} by author ID ${book3.authorId}")

        // 4. Create Categories
        println("\n4. Creating Categories...")
        val catFantasy = categoryRepo.save(Category(name = "Fantasy", description = "Fantasy fiction"))
        val catYA = categoryRepo.save(Category(name = "Young Adult", description = "Young adult fiction"))
        val catEpic = categoryRepo.save(Category(name = "Epic Fantasy", description = "Epic fantasy novels"))
        println("   Created categories: Fantasy, Young Adult, Epic Fantasy")

        // 5. Create ManyToMany relationships (Book-Category)
        println("\n5. Creating Book-Category relationships (ManyToMany)...")
        bookCategoryRepo.save(BookCategory(bookId = book1.id!!, categoryId = catFantasy.id!!))
        bookCategoryRepo.save(BookCategory(bookId = book1.id!!, categoryId = catYA.id!!))
        bookCategoryRepo.save(BookCategory(bookId = book2.id!!, categoryId = catFantasy.id!!))
        bookCategoryRepo.save(BookCategory(bookId = book2.id!!, categoryId = catYA.id!!))
        bookCategoryRepo.save(BookCategory(bookId = book3.id!!, categoryId = catFantasy.id!!))
        bookCategoryRepo.save(BookCategory(bookId = book3.id!!, categoryId = catEpic.id!!))
        println("   Book-Category mappings created")

        // 6. Query: Find books by author (demonstrating ManyToOne)
        println("\n6. Query: Books by ${author1.name}...")
        val author1Books = bookRepo.findAll().filter { it.authorId == author1.id }
        author1Books.forEach { book ->
            println("   - ${book.title} (${book.publishedYear})")
        }

        // 7. Query: Find profile for author (demonstrating OneToOne)
        println("\n7. Query: Profile for ${author1.name}...")
        val author1Profile = profileRepo.findAll().find { it.authorId == author1.id }
        author1Profile?.let {
            println("   Bio: ${it.bio}")
            println("   Website: ${it.website}")
        }

        // 8. Query: Find categories for a book (demonstrating ManyToMany)
        println("\n8. Query: Categories for '${book1.title}'...")
        val book1CategoryIds = bookCategoryRepo.findAll()
            .filter { it.bookId == book1.id }
            .map { it.categoryId }
        val book1Categories = categoryRepo.findAll()
            .filter { it.id in book1CategoryIds }
        book1Categories.forEach { category ->
            println("   - ${category.name}")
        }

        // 9. Statistics
        println("\n9. Statistics:")
        println("   Total Authors: ${authorRepo.count()}")
        println("   Total Books: ${bookRepo.count()}")
        println("   Total Categories: ${categoryRepo.count()}")
        println("   Total Profiles: ${profileRepo.count()}")
        println("   Total Book-Category mappings: ${bookCategoryRepo.count()}")

        println("\n=== All relationship examples completed successfully! ===")

    } catch (e: Exception) {
        println("Error: ${e.message}")
        e.printStackTrace()
    } finally {
        database.close()
    }
}
