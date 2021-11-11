package team.sopo.common.config.oauth2

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.web.servlet.HandlerExceptionResolver
import team.sopo.common.exception.SopoOauthException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class Oauth2AccessDeniedHandler(private val resolver: HandlerExceptionResolver): AccessDeniedHandler {

    private val logger: Logger = LogManager.getLogger(Oauth2AccessDeniedHandler::class)

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        logger.info("Oauth2AccessDeniedHandler, AccessDeniedException, ${accessDeniedException.localizedMessage}")
        logger.info("HttpAuthenticationEntryPoint, AuthenticationException, ${accessDeniedException.localizedMessage}")

        if(accessDeniedException.cause is SopoOauthException){
            val sopoOauthException = accessDeniedException.cause as SopoOauthException
            resolver.resolveException(request, response, null, sopoOauthException)
        }
    }
}