package br.pucpr.authserver.orders.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDateTime

data class AddItemRequest(
    @field:NotNull(message = "O ID do produto é obrigatório")
    val productId: Long?,

    @field:NotNull(message = "A quantidade é obrigatória")
    @field:Min(value = 1, message = "A quantidade mínima é 1")
    val quantity: Int?
)

data class OrderResponse(
    val id: Long,
    val userId: Long,
    val status: String,
    val createdAt: LocalDateTime,
    val items: List<OrderItemResponse>,
    val total: BigDecimal
)

data class OrderItemResponse(
    val id: Long,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val subtotal: BigDecimal
)