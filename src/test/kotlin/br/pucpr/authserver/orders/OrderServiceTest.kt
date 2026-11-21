package br.pucpr.authserver.orders

import br.pucpr.authserver.BadRequestException
import br.pucpr.authserver.NotFoundException
import br.pucpr.authserver.orders.dto.AddItemRequest
import br.pucpr.authserver.products.Product
import br.pucpr.authserver.products.ProductRepository
import br.pucpr.authserver.users.UserRepository
import br.pucpr.authserver.users.oneUser
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.Optional

class OrderServiceTest {
    private val orderRepository = mockk<OrderRepository>()
    private val productRepository = mockk<ProductRepository>()
    private val userRepository = mockk<UserRepository>()
    private val service = OrderService(orderRepository, productRepository, userRepository)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        checkUnnecessaryStub(orderRepository, productRepository, userRepository)
    }

    @Test
    fun `create should start new order for valid user`() {
        val user = oneUser(id = 1L)
        val order = Order(id = 10L, user = user)

        every { userRepository.findById(1L) } returns Optional.of(user)
        every { orderRepository.save(any()) } returns order

        val res = service.create(1L)
        res.id shouldBe 10L
        res.userId shouldBe 1L
    }

    @Test
    fun `addItem should add product to PENDING order and calculate total`() {
        val user = oneUser(id = 1L)
        val order = Order(id = 10L, user = user, status = OrderStatus.PENDING)
        val product = Product(id = 5L, name = "Mouse", description = "Gamer", price = BigDecimal("100.00"), category = "Periféricos")

        every { orderRepository.findByIdWithItems(10L) } returns order
        every { productRepository.findById(5L) } returns Optional.of(product)

        val res = service.addItem(10L, AddItemRequest(productId = 5L, quantity = 2))
        res.items.size shouldBe 1
        res.total shouldBe BigDecimal("200.00")
    }

    @Test
    fun `addItem should throw BadRequestException when order is not PENDING`() {
        val closedStatus = OrderStatus.entries.first { it != OrderStatus.PENDING }
        val order = Order(id = 10L, user = oneUser(id = 1L), status = closedStatus)
        every { orderRepository.findByIdWithItems(10L) } returns order

        shouldThrow<BadRequestException> {
            service.addItem(10L, AddItemRequest(productId = 5L, quantity = 1))
        }
    }

    @Test
    fun `delete should throw NotFoundException when order does not exist`() {
        every { orderRepository.findById(99L) } returns Optional.empty()

        shouldThrow<NotFoundException> { service.delete(99L) }
    }
}