package br.pucpr.authserver.products

import br.pucpr.authserver.products.dto.ProductRequest
import br.pucpr.authserver.products.dto.ProductResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import java.math.BigDecimal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
@SecurityRequirement(name = "AuthServer")
class ProductController(private val productService: ProductService) {

    @PostMapping
    fun create(@Valid @RequestBody req: ProductRequest): ResponseEntity<ProductResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(req))
    }

    @GetMapping
    fun list(
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) minPrice: BigDecimal?,
        @RequestParam(required = false) maxPrice: BigDecimal?,
        @RequestParam(required = false) active: Boolean?,
        @RequestParam(defaultValue = "ASC") sortDir: String
    ): ResponseEntity<List<ProductResponse>> =
        ResponseEntity.ok(productService.list(search, category, minPrice, maxPrice, active, sortDir))

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<ProductResponse> {
        return ResponseEntity.ok(productService.findById(id))
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody req: ProductRequest
    ): ResponseEntity<ProductResponse> {
        return ResponseEntity.ok(productService.update(id, req))
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        productService.delete(id)
        return ResponseEntity.ok().build()
    }
}