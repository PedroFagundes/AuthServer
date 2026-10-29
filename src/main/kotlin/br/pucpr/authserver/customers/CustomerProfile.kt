package br.pucpr.authserver.customers

import br.pucpr.authserver.users.User
import jakarta.persistence.*

@Entity
@Table(name = "tbl_customer_profiles")
class CustomerProfile(
    @Id
    val id: Long? = null,

    @Column(nullable = false)
    var address: String,

    @Column(nullable = false)
    var phone: String,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    var user: User? = null
)