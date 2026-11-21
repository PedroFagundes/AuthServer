package br.pucpr.authserver.products

import br.pucpr.authserver.products.dto.ProductRequest
import br.pucpr.authserver.products.dto.ProductResponse
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.math.BigDecimal

class ProductControllerTest {
    private val service = mockk<ProductService>()
    private val controller = ProductController(service)

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
        val req = ProductRequest("Monitor", "24 pol", BigDecimal("900.00"), "Hardware")
        val resp = ProductResponse(1L, "Monitor", "24 pol", BigDecimal("900.00"), "Hardware", true)
        every { service.create(req) } returns resp

        val result = controller.create(req)

        result.statusCode shouldBe HttpStatus.CREATED
        result.body shouldBe resp
    }

    @Test
    fun `getById should return 200 OK`() {
        val resp = ProductResponse(1L, "Monitor", "24 pol", BigDecimal("900.00"), "Hardware", true)
        every { service.findById(1L) } returns resp

        val result = controller.getById(1L)

        result.statusCode shouldBe HttpStatus.OK
        result.body shouldBe resp
    }
}