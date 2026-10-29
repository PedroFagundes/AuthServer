package br.pucpr.authserver.customers

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerProfileRepository : JpaRepository<CustomerProfile, Long>