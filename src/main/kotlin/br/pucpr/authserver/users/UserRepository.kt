package br.pucpr.authserver.users

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.domain.Sort
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Long> {
    fun findByEmail(email: String): User?

    @Query(
        """
            select distinct u from User u
            join u.roles r
            where r.name = :role
            order by u.name
        """
    )
    fun findByRole(role: String): List<User>

    @Query(
        """
        select distinct u from User u
        left join u.roles r
        where (:search is null or
            lower(u.name) like lower(concat('%', :search, '%')) or
            lower(u.email) like lower(concat('%', :search, '%')))
          and (:role is null or lower(r.name) = lower(:role))
        """
    )
    fun search(
        @Param("search") search: String?,
        @Param("role") role: String?,
        sort: Sort
    ): List<User>
}