package br.pucpr.authserver.products

import br.pucpr.authserver.BadRequestException
import br.pucpr.authserver.NotFoundException
import br.pucpr.authserver.products.dto.ProductRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.Optional

class ProductServiceTest {
    private val productRepository = mockk<ProductRepository>()
    private val service = ProductService(productRepository)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        checkUnnecessaryStub(productRepository)
    }

    @Test
    fun `create should save and return product response`() {
        val req = ProductRequest("Teclado", "Mecânico", BigDecimal("250.00"), "Periféricos")
        val saved = Product(id = 1L, name = "Teclado", description = "Mecânico", price = BigDecimal("250.00"), category = "Periféricos")

        every { productRepository.findByNameIgnoreCase("Teclado") } returns null
        every { productRepository.save(any()) } returns saved

        val result = service.create(req)

        verify(exactly = 1) { productRepository.save(any()) }
        result.id shouldBe 1L
        result.name shouldBe "Teclado"
    }

    @Test
    fun `create should throw BadRequestException when product name already exists`() {
        val req = ProductRequest("Teclado", "Mecânico", BigDecimal("250.00"), "Periféricos")
        every { productRepository.findByNameIgnoreCase("Teclado") } returns mockk()

        shouldThrow<BadRequestException> { service.create(req) }
    }

    @Test
    fun `findById should throw NotFoundException when product does not exist`() {
        every { productRepository.findById(99L) } returns Optional.empty()

        shouldThrow<NotFoundException> { service.findById(99L) }
    }

    @Test
    fun `list should throw BadRequestException when minPrice is greater than maxPrice`() {
        shouldThrow<BadRequestException> {
            service.list(null, null, BigDecimal("500.00"), BigDecimal("100.00"), null, "ASC")
        }
    }
}