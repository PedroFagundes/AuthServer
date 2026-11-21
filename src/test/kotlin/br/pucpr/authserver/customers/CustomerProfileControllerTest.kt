package br.pucpr.authserver.customers

import br.pucpr.authserver.customers.dto.CustomerProfileRequest
import br.pucpr.authserver.customers.dto.CustomerProfileResponse
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class CustomerProfileControllerTest {
    private val service = mockk<CustomerProfileService>()
    private val controller = CustomerProfileController(service)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        checkUnnecessaryStub(service)
    }

    @Test
    fun `create should return 201 CREATED`() {
        val req = CustomerProfileRequest("Rua B, 20", "41988888888")
        val resp = CustomerProfileResponse(1L, "Rua B, 20", "41988888888")
        every { service.saveProfile(1L, req) } returns resp

        val result = controller.create(1L, req)
        result.statusCode shouldBe HttpStatus.CREATED
        result.body shouldBe resp
    }

    @Test
    fun `delete should return 204 NO_CONTENT`() {
        justRun { service.deleteProfile(1L) }

        val result = controller.delete(1L)
        result.statusCode shouldBe HttpStatus.NO_CONTENT
    }
}