package br.pucpr.authserver.orders

import br.pucpr.authserver.BadRequestException
import br.pucpr.authserver.NotFoundException
import br.pucpr.authserver.orders.dto.AddItemRequest
import br.pucpr.authserver.orders.dto.OrderItemResponse
import br.pucpr.authserver.orders.dto.OrderResponse
import br.pucpr.authserver.products.ProductRepository
import br.pucpr.authserver.users.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) {
    private val log = LoggerFactory.getLogger(OrderService::class.java)

    @Transactional
    fun create(userId: Long): OrderResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { NotFoundException("Utilizador ID $userId não encontrado") }

        val order = Order(user = user)
        val saved = orderRepository.save(order)
        log.info("Novo pedido iniciado: ID {} para o utilizador {}", saved.id, userId)
        return saved.toResponse()
    }

    @Transactional
    fun addItem(orderId: Long, req: AddItemRequest): OrderResponse {
        val order = orderRepository.findByIdWithItems(orderId)
            ?: throw NotFoundException("Pedido ID $orderId não encontrado")

        if (order.status != OrderStatus.PENDING) {
            log.warn("Tentativa de alterar pedido fechado: ID {}", orderId)
            throw BadRequestException("Itens só podem ser adicionados a pedidos com estado PENDING")
        }

        val product = productRepository.findById(req.productId!!)
            .orElseThrow { NotFoundException("Produto ID ${req.productId} não encontrado") }

        val existing = order.items.firstOrNull { it.product.id == product.id }
        if (existing != null) {
            existing.quantity += req.quantity!!
        } else {
            val newItem = OrderItem(
                order = order,
                product = product,
                quantity = req.quantity!!,
                unitPrice = product.price
            )
            order.items.add(newItem)
        }

        log.info("Produto ID {} adicionado ao pedido ID {}", product.id, orderId)
        return order.toResponse()
    }

    @Transactional
    fun removeItem(orderId: Long, productId: Long): OrderResponse {
        val order = orderRepository.findByIdWithItems(orderId)
            ?: throw NotFoundException("Pedido ID $orderId não encontrado")

        if (order.status != OrderStatus.PENDING) {
            throw BadRequestException("Itens só podem ser removidos de pedidos com estado PENDING")
        }

        val item = order.items.firstOrNull { it.product.id == productId }
            ?: throw NotFoundException("Produto ID $productId não se encontra no pedido")

        order.items.remove(item)
        log.info("Produto ID {} removido do pedido ID {}", productId, orderId)
        return order.toResponse()
    }

    fun getOrder(id: Long): OrderResponse {
        val order = orderRepository.findByIdWithItems(id)
            ?: throw NotFoundException("Pedido ID $id não encontrado")
        return order.toResponse()
    }

    @Transactional
    fun delete(id: Long) {
        val order = orderRepository.findById(id)
            .orElseThrow { NotFoundException("Pedido ID $id não encontrado") }

        orderRepository.delete(order)
        log.info("Pedido ID {} eliminado", id)
    }

    private fun Order.toResponse(): OrderResponse {
        val mappedItems = items.map {
            val subtotal = it.unitPrice.multiply(BigDecimal(it.quantity))
            OrderItemResponse(
                id = it.id ?: 0,
                productId = it.product.id!!,
                productName = it.product.name,
                quantity = it.quantity,
                unitPrice = it.unitPrice,
                subtotal = subtotal
            )
        }
        val total = mappedItems.fold(BigDecimal.ZERO) { acc, item -> acc.add(item.subtotal) }

        return OrderResponse(
            id = id!!,
            userId = user.id!!,
            status = status.name,
            createdAt = createdAt,
            items = mappedItems,
            total = total
        )
    }
}