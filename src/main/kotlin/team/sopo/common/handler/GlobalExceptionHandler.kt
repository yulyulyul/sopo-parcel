package team.sopo.common.handler

import org.apache.logging.log4j.LogManager
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import team.sopo.common.exception.SopoException
import team.sopo.common.exception.SopoOauthException
import team.sopo.common.exception.error.ErrorResponse
import team.sopo.common.exception.error.SopoError
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LogManager.getLogger(GlobalExceptionHandler::class)

    @ExceptionHandler(SopoException::class)
    fun handleSopoException(
        request: HttpServletRequest,
        e: SopoException
    ): ResponseEntity<ErrorResponse> {
        logger.error("SopoException", e)
        val response = ErrorResponse(e, request.servletPath)

        return ResponseEntity(response, e.getHttpStatus())
    }

    @ExceptionHandler(SopoOauthException::class)
    fun handleSopoOauthException(
        request: HttpServletRequest,
        response: HttpServletResponse,
        e: SopoOauthException): ResponseEntity<ErrorResponse> {

        val sopoOauthError = e.sopoError
        val errorResponse = ErrorResponse(sopoOauthError, sopoOauthError.message ?: e.localizedMessage, request.servletPath)
        return ResponseEntity(errorResponse, sopoOauthError.status)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(
        request: HttpServletRequest,
        response: HttpServletResponse,
        e: AuthenticationException
    ): ResponseEntity<ErrorResponse> {

        val errorResponse = ErrorResponse(SopoError.AUTHENTICATION_FAIL, SopoError.AUTHENTICATION_FAIL.message ?: e.localizedMessage, request.servletPath)
        return ResponseEntity(errorResponse, SopoError.AUTHENTICATION_FAIL.status)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(
        request: HttpServletRequest,
        response: HttpServletResponse,
        e: AccessDeniedException
    ): ResponseEntity<ErrorResponse> {

        val errorResponse = ErrorResponse(SopoError.AUTHORIZE_FAIL, SopoError.AUTHORIZE_FAIL.message ?: e.localizedMessage, request.servletPath)
        return ResponseEntity(errorResponse, SopoError.AUTHORIZE_FAIL.status)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        request: HttpServletRequest,
        response: HttpServletResponse,
        e: MethodArgumentNotValidException): ResponseEntity<List<ErrorResponse>> {

        val bindingResult = e.bindingResult
        val errorResponses = bindingResult
            .fieldErrors
            .stream()
            .map {
                it.defaultMessage?.let { errorMessage ->
                    ErrorResponse(SopoError.VALIDATION, errorMessage, request.servletPath)
                }
            }
            .collect(Collectors.toList())

        return ResponseEntity(errorResponses, SopoError.VALIDATION.status)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(
        request: HttpServletRequest,
        e: Exception): ResponseEntity<ErrorResponse>{
        logger.error(e.printStackTrace())
        logger.error("[Exception] : ${e.localizedMessage}")

        val errorResponse = ErrorResponse(SopoError.UNKNOWN_ERROR, e.localizedMessage, request.servletPath)
        return ResponseEntity(errorResponse, SopoError.UNKNOWN_ERROR.status)
    }

}