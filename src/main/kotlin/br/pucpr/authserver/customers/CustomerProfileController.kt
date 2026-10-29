package br.pucpr.authserver.customers

import br.pucpr.authserver.customers.dto.CustomerProfileRequest
import br.pucpr.authserver.customers.dto.CustomerProfileResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/{userId}/profile")
class CustomerProfileController(private val customerProfileService: CustomerProfileService) {

    @PutMapping
    fun save(
        @PathVariable userId: Long,
        @Valid @RequestBody req: CustomerProfileRequest
    ): ResponseEntity<CustomerProfileResponse> {
        return ResponseEntity.ok(customerProfileService.saveProfile(userId, req))
    }

    @GetMapping
    fun get(@PathVariable userId: Long): ResponseEntity<CustomerProfileResponse> {
        return ResponseEntity.ok(customerProfileService.getProfile(userId))
    }
}