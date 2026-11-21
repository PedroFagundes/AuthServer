package br.pucpr.authserver.customers

import br.pucpr.authserver.NotFoundException
import br.pucpr.authserver.customers.dto.CustomerProfileRequest
import br.pucpr.authserver.users.UserRepository
import br.pucpr.authserver.users.oneUser
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional

class CustomerProfileServiceTest {
    private val profileRepository = mockk<CustomerProfileRepository>()
    private val userRepository = mockk<UserRepository>()
    private val service = CustomerProfileService(profileRepository, userRepository)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        checkUnnecessaryStub(profileRepository, userRepository)
    }

    @Test
    fun `saveProfile should save and return profile response`() {
        val user = oneUser(id = 1L)
        val profile = CustomerProfile(id = 1L, address = "Rua A, 10", phone = "41999999999", user = user)

        every { userRepository.findById(1L) } returns Optional.of(user)
        every { profileRepository.findById(1L) } returns Optional.empty()
        every { profileRepository.save(any()) } returns profile

        val res = service.saveProfile(1L, CustomerProfileRequest("Rua A, 10", "41999999999"))
        res.userId shouldBe 1L
        res.address shouldBe "Rua A, 10"
    }

    @Test
    fun `getProfile should throw NotFoundException when profile does not exist`() {
        every { profileRepository.findById(99L) } returns Optional.empty()

        shouldThrow<NotFoundException> { service.getProfile(99L) }
    }
}