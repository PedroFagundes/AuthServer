package br.pucpr.authserver.products

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun findByNameIgnoreCase(name: String): Product?
    fun findByCategoryIgnoreCase(category: String, sort: Sort): List<Product>

    @Query(
        """
        select p from Product p
        where (:search is null or
            lower(p.name) like lower(concat('%', :search, '%')) or
            lower(p.description) like lower(concat('%', :search, '%')))
          and (:category is null or lower(p.category) = lower(:category))
          and (:minPrice is null or p.price >= :minPrice)
          and (:maxPrice is null or p.price <= :maxPrice)
          and (:active is null or p.active = :active)
        """
    )
    fun findByFilters(
        @Param("search") search: String?,
        @Param("category") category: String?,
        @Param("minPrice") minPrice: BigDecimal?,
        @Param("maxPrice") maxPrice: BigDecimal?,
        @Param("active") active: Boolean?,
        sort: Sort
    ): List<Product>
}