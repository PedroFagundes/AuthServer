package br.pucpr.authserver.orders

import br.pucpr.authserver.orders.dto.AddItemRequest
import br.pucpr.authserver.orders.dto.OrderResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import java.time.LocalDateTime
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
@SecurityRequirement(name = "AuthServer")
class OrderController(private val orderService: OrderService) {

    @GetMapping
    fun list(
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) status: OrderStatus?,
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) productId: Long?,
        @RequestParam(required = false) createdAfter: LocalDateTime?,
        @RequestParam(required = false) createdBefore: LocalDateTime?
    ): ResponseEntity<List<OrderResponse>> =
        ResponseEntity.ok(orderService.list(search, status, userId, productId, createdAfter, createdBefore))

    @PostMapping("/users/{userId}")
    fun create(@PathVariable userId: Long): ResponseEntity<OrderResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(userId))
    }

    @GetMapping("/{id}")
    fun getOrder(@PathVariable id: Long): ResponseEntity<OrderResponse> {
        return ResponseEntity.ok(orderService.getOrder(id))
    }

    @PutMapping("/{orderId}/items")
    fun addItem(
        @PathVariable orderId: Long,
        @Valid @RequestBody req: AddItemRequest
    ): ResponseEntity<OrderResponse> {
        return ResponseEntity.ok(orderService.addItem(orderId, req))
    }

    @DeleteMapping("/{orderId}/items/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun removeItem(
        @PathVariable orderId: Long,
        @PathVariable productId: Long
    ): ResponseEntity<OrderResponse> {
        return ResponseEntity.ok(orderService.removeItem(orderId, productId))
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteOrder(@PathVariable id: Long): ResponseEntity<Void> {
        orderService.delete(id)
        return ResponseEntity.ok().build()
    }
}