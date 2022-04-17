package team.sopo.common.config.auth

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import team.sopo.common.exception.error.Error
import team.sopo.common.exception.error.Errors
import team.sopo.common.exception.error.SopoError
import java.nio.charset.StandardCharsets
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class CustomAuthenticationEntryPoint(private val objectMapper: ObjectMapper) : AuthenticationEntryPoint {
    private val logger = LogManager.getLogger(CustomAuthenticationEntryPoint::class)

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        logger.error("log: exception: {} ", authException)
        sendErrorResponse(request, response)
    }

    private fun sendErrorResponse(request: HttpServletRequest, response: HttpServletResponse) {

        val errors = Errors().apply {
            this.errors.add(Error(SopoError.AUTHENTICATION_FAIL, SopoError.AUTHENTICATION_FAIL.message ?: "인증에 실패한 유저입니다.", request.servletPath))
        }
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write(
            objectMapper.writeValueAsString(errors)
        )
    }

}