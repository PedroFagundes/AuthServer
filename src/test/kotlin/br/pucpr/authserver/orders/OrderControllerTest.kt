package br.pucpr.authserver.orders

import br.pucpr.authserver.orders.dto.AddItemRequest
import br.pucpr.authserver.orders.dto.OrderResponse
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.time.LocalDateTime

class OrderControllerTest {
    private val service = mockk<OrderService>()
    private val controller = OrderController(service)

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
        val resp = OrderResponse(1L, 2L, "PENDING", LocalDateTime.now(), emptyList(), BigDecimal.ZERO)
        every { service.create(2L) } returns resp

        val result = controller.create(2L)
        result.statusCode shouldBe HttpStatus.CREATED
        result.body shouldBe resp
    }

    @Test
    fun `removeItem should return 200 OK`() {
        val resp = OrderResponse(1L, 2L, "PENDING", LocalDateTime.now(), emptyList(), BigDecimal.ZERO)
        every { service.removeItem(1L, 5L) } returns resp

        val result = controller.removeItem(1L, 5L)
        result.statusCode shouldBe HttpStatus.OK
        result.body shouldBe resp
    }
}