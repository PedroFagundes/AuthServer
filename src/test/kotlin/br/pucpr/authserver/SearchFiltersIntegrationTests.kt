package br.pucpr.authserver

import br.pucpr.authserver.customers.CustomerProfileService
import br.pucpr.authserver.customers.dto.CustomerProfileRequest
import br.pucpr.authserver.orders.Order
import br.pucpr.authserver.orders.OrderItem
import br.pucpr.authserver.orders.OrderRepository
import br.pucpr.authserver.orders.OrderService
import br.pucpr.authserver.products.Product
import br.pucpr.authserver.products.ProductRepository
import br.pucpr.authserver.products.ProductService
import br.pucpr.authserver.roles.Role
import br.pucpr.authserver.roles.RoleRepository
import br.pucpr.authserver.roles.RoleService
import br.pucpr.authserver.users.SortDir
import br.pucpr.authserver.users.User
import br.pucpr.authserver.users.UserRepository
import br.pucpr.authserver.users.UserService
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@SpringBootTest
class SearchFiltersIntegrationTests @Autowired constructor(
    private val productRepository: ProductRepository,
    private val productService: ProductService,
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val customerProfileService: CustomerProfileService,
    private val roleRepository: RoleRepository,
    private val roleService: RoleService,
    private val orderRepository: OrderRepository,
    private val orderService: OrderService
) {
    @Test
    @Transactional
    fun `list endpoints filter products users roles and orders`() {
        val token = UUID.randomUUID().toString()
        val role = roleRepository.save(Role(name = "FILTER_$token", description = "integration test"))
        val user = userRepository.save(
            User(
                email = "filter-$token@example.com",
                password = "test-password",
                name = "Filter User $token",
                roles = mutableSetOf(role)
            )
        )
        customerProfileService.saveProfile(
            user.id!!,
            CustomerProfileRequest(address = "Filter Street $token", phone = "555-$token")
        )
        val product = productRepository.save(
            Product(
                name = "Filter Product $token",
                description = "Searchable description",
                price = BigDecimal("15.00"),
                category = "FilterCategory",
                active = true
            )
        )
        val order = Order(user = user).apply {
            items.add(
                OrderItem(
                    order = this,
                    product = product,
                    quantity = 1,
                    unitPrice = product.price
                )
            )
        }
        orderRepository.save(order)

        assertTrue(
            productService.list(
                search = token,
                category = "filtercategory",
                minPrice = BigDecimal("10.00"),
                maxPrice = BigDecimal("20.00"),
                active = true,
                sortDir = "ASC"
            ).any { it.id == product.id }
        )
        assertTrue(userService.search(token, role.name, SortDir.ASC).any { it.id == user.id })
        assertTrue(roleService.findAll(token).any { it.id == role.id })
        assertTrue(
            customerProfileService.list(
                search = token,
                address = "filter street",
                phone = token,
                userId = user.id
            ).any { it.userId == user.id }
        )

        val now = LocalDateTime.now()
        assertTrue(
            orderService.list(
                search = token,
                status = order.status,
                userId = user.id,
                productId = product.id,
                createdAfter = now.minusMinutes(1),
                createdBefore = now.plusMinutes(1)
            ).any { it.id == order.id }
        )
    }
}
