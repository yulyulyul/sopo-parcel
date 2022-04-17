package team.sopo.common.config.jwt

import io.jsonwebtoken.*
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import team.sopo.common.config.auth.CustomUserDetailService
import team.sopo.common.exception.InsufficientConditionException
import team.sopo.common.exception.InvalidUserException
import java.util.*

@Component
class JwtProvider(
    @Value("\${jwt.secret}") jwtSecret: String,
    private val userDetailService: CustomUserDetailService,
) {
    private val logger = LogManager.getLogger(JwtProvider::class)
    private val secretKey = Base64.getEncoder().encodeToString(jwtSecret.toByteArray(Charsets.UTF_8))

    companion object {
        private const val BEARER = "bearer"
        private const val ROLES = "roles"
        private const val accessValidTime = 60 * 60 * 1000L
        private const val refreshValidTime = 14 * 24 * 60 * 60 * 1000L
    }

    fun createToken(userToken: String, roles: List<String>): JwtToken {
        val claims = Jwts.claims().setSubject(userToken)
        claims[ROLES] = roles

        val now = Date()

        val accessToken = Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + accessValidTime))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()

        val refreshToken = Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + refreshValidTime))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()

        return JwtToken(BEARER, userToken, accessToken, refreshToken, accessValidTime)
    }

    fun getAuthentication(token: String): UsernamePasswordAuthenticationToken {
        val claims = parseClaims(token) ?: throw InvalidUserException("유효하지 않은 토큰입니다.")

        if (!claims.containsKey(ROLES) || (claims[ROLES] as List<*>).isEmpty()) {
            throw InvalidUserException("토큰에 유효한 권한 정보가 없습니다.")
        }
        val roles = claims[ROLES] as List<*>

        val userDetail = userDetailService.loadUserByUsername(claims.subject, roles)

        return UsernamePasswordAuthenticationToken(
            userDetail,
            "",
            userDetail.authorities
        )

    }

    fun getData(key: String, token: String): Any {
        val claims = parseClaims(token)
        if(claims.isNullOrEmpty() || !claims.containsKey(key)){
            throw InsufficientConditionException("claims를 파싱하는데 실패했습니다.")
        }
        return claims[key] as Any
    }

    private fun parseClaims(token: String): Claims? {
        return try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }

    fun validToken(token: String): Boolean {
        return try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            true
        } catch (e: JwtException) {
            logger.error(e.toString())
            false
        } catch (e: IllegalArgumentException) {
            logger.error(e.toString())
            false
        }
    }
}