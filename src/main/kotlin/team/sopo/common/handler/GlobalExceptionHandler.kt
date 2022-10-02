package team.sopo.common.handler

import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import team.sopo.common.exception.SopoException
import team.sopo.common.exception.error.Error
import team.sopo.common.exception.error.Errors
import team.sopo.common.exception.error.SopoError
import team.sopo.common.tracing.ApiTracingRepository
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.ConstraintViolationException


@RestControllerAdvice
class GlobalExceptionHandler(private val repository: ApiTracingRepository) {

    @ExceptionHandler(SopoException::class)
    fun handleSopoException(
        request: HttpServletRequest,
        e: SopoException
    ): ResponseEntity<Errors> {

        val error = Error(e, request.servletPath)
        val errors = Errors().apply { this.errors.add(error) }
        logError(error, e)

        return ResponseEntity(errors, e.getHttpStatus())
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(
        request: HttpServletRequest,
        response: HttpServletResponse,
        e: AuthenticationException
    ): ResponseEntity<Errors> {

        val error = Error(
            SopoError.AUTHENTICATION_FAIL,
            SopoError.AUTHENTICATION_FAIL.message ?: e.localizedMessage,
            request.servletPath
        )
        val errors = Errors().apply { this.errors.add(error) }
        logError(error, e)

        return ResponseEntity(errors, SopoError.AUTHENTICATION_FAIL.status)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(
        request: HttpServletRequest,
        response: HttpServletResponse,
        e: AccessDeniedException
    ): ResponseEntity<Errors> {

        val error = Error(
            SopoError.AUTHORIZE_FAIL,
            SopoError.AUTHORIZE_FAIL.message ?: e.localizedMessage,
            request.servletPath
        )
        val errors = Errors().apply { this.errors.add(error) }
        logError(error, e)

        return ResponseEntity(errors, SopoError.AUTHORIZE_FAIL.status)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        request: HttpServletRequest,
        response: HttpServletResponse,
        e: MethodArgumentNotValidException
    ): ResponseEntity<Errors> {

        val bindingResult = e.bindingResult
        val errorResponses = bindingResult
            .fieldErrors
            .stream()
            .map {
                it.defaultMessage?.let { errorMessage ->
                    Error(SopoError.VALIDATION, errorMessage, request.servletPath)
                }
            }
            .collect(Collectors.toList())

        val errors = Errors().apply {
            this.errors.addAll(errorResponses)
        }

        if (errors.errors.isNotEmpty()) {
            val error = errors.errors.first()
            logError(error, e)
        }

        return ResponseEntity(errors, SopoError.VALIDATION.status)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        request: HttpServletRequest,
        response: HttpServletResponse,
        e: ConstraintViolationException
    ): ResponseEntity<Errors> {

        val errorResponses = e.constraintViolations.map {
            Error(SopoError.VALIDATION, it.messageTemplate, request.servletPath)
        }.toList()

        val errors = Errors().apply {
            this.errors.addAll(errorResponses)
        }

        if (errors.errors.isNotEmpty()) {
            val error = errors.errors.first()
            logError(error, e)
        }

        return ResponseEntity(errors, SopoError.VALIDATION.status)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(
        request: HttpServletRequest,
        e: Exception
    ): ResponseEntity<Errors> {

        val error = Error(SopoError.UNKNOWN_ERROR, e.localizedMessage, request.servletPath)
        val errors = Errors().apply { this.errors.add(error) }

        logError(error, e)

        return ResponseEntity(errors, SopoError.UNKNOWN_ERROR.status)
    }

    private fun logError(error: Error, e: Exception) {
        e.printStackTrace()
        repository.saveErrorInfo(error.code, error.type, e, e.toString())
    }

}