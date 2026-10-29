package br.pucpr.authserver.customers

import br.pucpr.authserver.NotFoundException
import br.pucpr.authserver.customers.dto.CustomerProfileRequest
import br.pucpr.authserver.customers.dto.CustomerProfileResponse
import br.pucpr.authserver.users.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomerProfileService(
    private val customerProfileRepository: CustomerProfileRepository,
    private val userRepository: UserRepository
) {
    private val log = LoggerFactory.getLogger(CustomerProfileService::class.java)

    @Transactional
    fun saveProfile(userId: Long, req: CustomerProfileRequest): CustomerProfileResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { NotFoundException("Usuário ID $userId não encontrado") }

        val profile = customerProfileRepository.findById(userId).orElse(
            CustomerProfile(address = req.address!!, phone = req.phone!!, user = user)
        ).apply {
            address = req.address!!
            phone = req.phone!!
        }

        val saved = customerProfileRepository.save(profile)
        log.info("Perfil atualizado para o usuário ID {}", userId)
        return CustomerProfileResponse(userId = user.id!!, address = saved.address, phone = saved.phone)
    }

    fun getProfile(userId: Long): CustomerProfileResponse {
        val profile = customerProfileRepository.findById(userId)
            .orElseThrow { NotFoundException("Perfil não encontrado para o usuário ID $userId") }
        return CustomerProfileResponse(userId = profile.id!!, address = profile.address, phone = profile.phone)
    }
}