package br.pucpr.authserver.products

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun findByNameIgnoreCase(name: String): Product?
    fun findByCategoryIgnoreCase(category: String, sort: Sort): List<Product>
}