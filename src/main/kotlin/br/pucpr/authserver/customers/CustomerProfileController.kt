package br.pucpr.authserver.customers

import br.pucpr.authserver.customers.dto.CustomerProfileRequest
import br.pucpr.authserver.customers.dto.CustomerProfileResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class CustomerProfileController(private val customerProfileService: CustomerProfileService) {

    @GetMapping("/profiles")
    fun list(
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) address: String?,
        @RequestParam(required = false) phone: String?,
        @RequestParam(required = false) userId: Long?
    ): ResponseEntity<List<CustomerProfileResponse>> =
        ResponseEntity.ok(customerProfileService.list(search, address, phone, userId))

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
}