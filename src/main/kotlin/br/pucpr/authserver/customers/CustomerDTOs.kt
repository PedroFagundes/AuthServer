package br.pucpr.authserver.customers.dto

import jakarta.validation.constraints.NotBlank

data class CustomerProfileRequest(
    @field:NotBlank(message = "O endereço é obrigatório")
    val address: String?,

    @field:NotBlank(message = "O telefone é obrigatório")
    val phone: String?
)

data class CustomerProfileResponse(
    val userId: Long,
    val address: String,
    val phone: String
)