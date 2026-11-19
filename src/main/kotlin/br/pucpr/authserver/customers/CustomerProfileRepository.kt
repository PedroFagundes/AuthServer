package br.pucpr.authserver.customers

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CustomerProfileRepository : JpaRepository<CustomerProfile, Long> {
    @Query(
        """
        select p from CustomerProfile p
        join fetch p.user u
        where (:search is null or
            lower(p.address) like lower(concat('%', :search, '%')) or
            lower(p.phone) like lower(concat('%', :search, '%')) or
            lower(u.name) like lower(concat('%', :search, '%')) or
            lower(u.email) like lower(concat('%', :search, '%')))
          and (:address is null or lower(p.address) like lower(concat('%', :address, '%')))
          and (:phone is null or p.phone like concat('%', :phone, '%'))
          and (:userId is null or p.id = :userId)
        order by p.id
        """
    )
    fun findByFilters(
        @Param("search") search: String?,
        @Param("address") address: String?,
        @Param("phone") phone: String?,
        @Param("userId") userId: Long?
    ): List<CustomerProfile>
}