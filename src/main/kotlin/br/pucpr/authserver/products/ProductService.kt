package br.pucpr.authserver.products

import br.pucpr.authserver.NotFoundException
import br.pucpr.authserver.BadRequestException
import br.pucpr.authserver.products.dto.ProductRequest
import br.pucpr.authserver.products.dto.ProductResponse
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class ProductService(private val productRepository: ProductRepository) {
    private val log = LoggerFactory.getLogger(ProductService::class.java)

    @Transactional
    fun create(req: ProductRequest): ProductResponse {
        productRepository.findByNameIgnoreCase(req.name!!)?.let {
            log.warn("Tentativa de criar produto com nome duplicado: {}", req.name)
            throw BadRequestException("Já existe um produto com o nome ${req.name}")
        }

        val entity = Product(
            name = req.name,
            description = req.description!!,
            price = req.price!!,
            category = req.category!!
        )
        val saved = productRepository.save(entity)
        log.info("Produto criado com sucesso: ID {}", saved.id)
        return saved.toResponse()
    }

    fun list(
        search: String?,
        category: String?,
        minPrice: BigDecimal?,
        maxPrice: BigDecimal?,
        active: Boolean?,
        sortDir: String
    ): List<ProductResponse> {
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw BadRequestException("O preço mínimo não pode ser maior que o preço máximo")
        }

        val direction = if (sortDir.equals("DESC", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sort = Sort.by(direction, "name")

        val products = productRepository.findByFilters(
            search?.trim()?.takeIf { it.isNotEmpty() },
            category?.trim()?.takeIf { it.isNotEmpty() },
            minPrice,
            maxPrice,
            active,
            sort
        )
        return products.map { it.toResponse() }
    }

    fun findById(id: Long): ProductResponse {
        return productRepository.findById(id)
            .map { it.toResponse() }
            .orElseThrow { NotFoundException("Produto ID $id não encontrado") }
    }

    @Transactional
    fun update(id: Long, req: ProductRequest): ProductResponse {
        val product = productRepository.findById(id)
            .orElseThrow { NotFoundException("Produto ID $id não encontrado") }

        product.name = req.name!!
        product.description = req.description!!
        product.price = req.price!!
        product.category = req.category!!

        log.info("Produto ID {} atualizado com sucesso", id)
        return product.toResponse()
    }

    @Transactional
    fun delete(id: Long) {
        val product = productRepository.findById(id)
            .orElseThrow { NotFoundException("Produto ID $id não encontrado") }

        productRepository.delete(product)
        log.info("Produto ID {} removido com sucesso", id)
    }

    private fun Product.toResponse() = ProductResponse(
        id = id!!,
        name = name,
        description = description,
        price = price,
        category = category,
        active = active
    )
}