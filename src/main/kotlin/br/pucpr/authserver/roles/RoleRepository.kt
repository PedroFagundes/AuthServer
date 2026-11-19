package br.pucpr.authserver.roles

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository: JpaRepository<Role, Long> {
    fun findByName(name: String): Role?

    @Query(
        """
        select r from Role r
        where (:search is null or
            lower(r.name) like lower(concat('%', :search, '%')) or
            lower(r.description) like lower(concat('%', :search, '%')))
        order by r.name
        """
    )
    fun findAllBySearch(@Param("search") search: String?): List<Role>
}