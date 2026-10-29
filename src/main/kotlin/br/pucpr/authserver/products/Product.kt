package br.pucpr.authserver.products

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "tbl_products")
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    var name: String,

    @Column(nullable = false)
    var description: String,

    @Column(nullable = false)
    var price: BigDecimal,

    @Column(nullable = false)
    var category: String,

    @Column(nullable = false)
    var active: Boolean = true
)