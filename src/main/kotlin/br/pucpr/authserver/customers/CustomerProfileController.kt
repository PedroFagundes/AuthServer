package br.pucpr.authserver.customers

import br.pucpr.authserver.customers.dto.CustomerProfileRequest
import br.pucpr.authserver.customers.dto.CustomerProfileResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "AuthServer")
class CustomerProfileController(private val customerProfileService: CustomerProfileService) {

    @GetMapping("/profiles")
    fun list(
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) address: String?,
        @RequestParam(required = false) phone: String?,
        @RequestParam(required = false) userId: Long?
    ): ResponseEntity<List<CustomerProfileResponse>> =
        ResponseEntity.ok(customerProfileService.list(search, address, phone, userId))

    @PostMapping("/{userId}/profile")
    fun create(
        @PathVariable userId: Long,
        @Valid @RequestBody req: CustomerProfileRequest
    ): ResponseEntity<CustomerProfileResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerProfileService.saveProfile(userId, req))
    }

    @PutMapping("/{userId}/profile")
    fun save(
        @PathVariable userId: Long,
        @Valid @RequestBody req: CustomerProfileRequest
    ): ResponseEntity<CustomerProfileResponse> {
        return ResponseEntity.ok(customerProfileService.saveProfile(userId, req))
    }

    @GetMapping("/{userId}/profile")
    fun get(@PathVariable userId: Long): ResponseEntity<CustomerProfileResponse> {
        return ResponseEntity.ok(customerProfileService.getProfile(userId))
    }

    @DeleteMapping("/{userId}/profile")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable userId: Long): ResponseEntity<Void> {
        customerProfileService.deleteProfile(userId)
        return ResponseEntity.noContent().build()
    }
}