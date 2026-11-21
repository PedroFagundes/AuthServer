package br.pucpr.authserver.security

import br.pucpr.authserver.users.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.jackson.io.JacksonDeserializer
import io.jsonwebtoken.jackson.io.JacksonSerializer
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Date

@Component
class JWT {
    fun createToken(user: User): String =
        UserToken(user).let {
            Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(SECRET.toByteArray()))
                .json(JacksonSerializer())
                .issuedAt(utcNow().toDate())
                .expiration(utcNow().plusHours(if (it.isAdmin) ADMIN_EXPIRE_HOURS else EXPIRE_HOURS).toDate())
                .issuer(ISSUER)
                .subject(user.id.toString())
                .claim(USER_FIELD, it)
                .compact()
        }

    fun extract(req: HttpServletRequest): Authentication? {
        try {
            val header = req.getHeader(AUTHORIZATION)
            if (header == null || !header.startsWith(PREFIX)) return null
            val token = header.replace(PREFIX, "").trim()

            val claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.toByteArray()))
                .json(JacksonDeserializer(mapOf(USER_FIELD to UserToken::class.java)))
                .build()
                .parseSignedClaims(token)
                .payload

            if (claims.issuer != ISSUER) return null
            val user = claims.get(USER_FIELD, UserToken::class.java) ?: return null
            return user.toAuthentication()
        } catch (_: Throwable) {
            return null
        }
    }

    companion object {
        const val PREFIX = "Bearer "
        const val ISSUER = "PUCPR AuthServer"
        const val SECRET = "0e5582adfb7fa6bb770815f3c6b3534d11111111"
        const val EXPIRE_HOURS = 2L
        const val ADMIN_EXPIRE_HOURS = 24L
        const val USER_FIELD = "user"

        private fun utcNow() = ZonedDateTime.now(ZoneOffset.UTC)
        private fun ZonedDateTime.toDate(): Date = Date.from(this.toInstant())

        private fun UserToken.toAuthentication(): Authentication {
            val authorities = roles.map { SimpleGrantedAuthority("ROLE_$it") }
            return UsernamePasswordAuthenticationToken.authenticated(this, id, authorities)
        }
    }
}