package team.sopo.common.config.oauth2

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerExceptionResolver
import team.sopo.common.exception.SopoOauthException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class HttpAuthenticationEntryPoint: AuthenticationEntryPoint {

    private val logger: Logger = LogManager.getLogger(HttpAuthenticationEntryPoint::class)

    @Autowired
    @Qualifier("handlerExceptionResolver")
    lateinit var resolver: HandlerExceptionResolver

    override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {

        logger.info("HttpAuthenticationEntryPoint, AuthenticationException, ${authException.localizedMessage}")

        if(authException.cause is SopoOauthException){
            val sopoOauthException = authException.cause as SopoOauthException
            resolver.resolveException(request, response, null, sopoOauthException)
        }
    }
}