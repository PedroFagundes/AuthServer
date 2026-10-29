package br.pucpr.authserver.products.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class ProductRequest(
    @field:NotBlank(message = "O nome é obrigatório")
    val name: String?,

    @field:NotBlank(message = "A descrição é obrigatória")
    val description: String?,

    @field:NotNull(message = "O preço é obrigatório")
    @field:DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    val price: BigDecimal?,

    @field:NotBlank(message = "A categoria é obrigatória")
    val category: String?
)

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val category: String,
    val active: Boolean
)