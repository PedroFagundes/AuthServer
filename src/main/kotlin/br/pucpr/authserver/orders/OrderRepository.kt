package br.pucpr.authserver.orders

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface OrderRepository : JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product WHERE o.id = :id")
    fun findByIdWithItems(id: Long): Order?

    @Query(
        """
        select distinct o from Order o
        left join fetch o.items i
        left join fetch i.product
        left join o.user u
        where (:search is null or
            lower(u.name) like lower(concat('%', :search, '%')) or
            lower(u.email) like lower(concat('%', :search, '%')) or
            lower(i.product.name) like lower(concat('%', :search, '%')))
          and (:status is null or o.status = :status)
          and (:userId is null or u.id = :userId)
          and (:productId is null or i.product.id = :productId)
          and (:createdAfter is null or o.createdAt >= :createdAfter)
          and (:createdBefore is null or o.createdAt <= :createdBefore)
        order by o.createdAt desc
        """
    )
    fun findByFilters(
        @Param("search") search: String?,
        @Param("status") status: OrderStatus?,
        @Param("userId") userId: Long?,
        @Param("productId") productId: Long?,
        @Param("createdAfter") createdAfter: LocalDateTime?,
        @Param("createdBefore") createdBefore: LocalDateTime?
    ): List<Order>
}