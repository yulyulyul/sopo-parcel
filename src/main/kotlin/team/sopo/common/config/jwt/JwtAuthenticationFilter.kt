package team.sopo.common.config.jwt

import org.apache.commons.lang3.StringUtils
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter(private val jwtProvider: JwtProvider): OncePerRequestFilter() {

    companion object{
        private const val BEARER = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = getToken(request)

        if(jwtProvider.validToken(token)){
            val authentication = jwtProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }

    private fun getToken(request: HttpServletRequest): String {
        val authHeader = request.getHeader(AUTHORIZATION)
        if (StringUtils.isEmpty(authHeader) || !authHeader.startsWith(BEARER)) {
            return ""
        }
        return authHeader.substring(BEARER.length)
    }
}