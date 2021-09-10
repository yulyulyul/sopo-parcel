package team.sopo.common.handler

import team.sopo.common.enums.ResponseEnum
import team.sopo.common.exception.APIException
import team.sopo.common.model.api.ApiResult
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.ConstraintDefinitionException
import javax.validation.ConstraintViolationException


@RestControllerAdvice
class GlobalExceptionHandler {

    val logger = LogManager.getLogger(this.javaClass.name)

    @ExceptionHandler(APIException::class)
    fun handleAPIException(request: HttpServletRequest,
                           response: HttpServletResponse,
                           e: APIException
    ): ApiResult<String>
    {
        logger.error("API Processing Error!", e)
        logger.error("Response status code : ${response.status}")

        response.status = e.httpStatus
        return ApiResult(
                uniqueCode = request.getHeader("uuid") ?: "",
                code = e.responseEnum.CODE,
                message = e.message,
                path = request.servletPath,
                data = null
        )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(request: HttpServletRequest,
                                           response: HttpServletResponse,
                                           e: ConstraintViolationException): ApiResult<String>
    {
        response.status = HttpStatus.BAD_REQUEST.value()

        return ApiResult(
                uniqueCode = request.getHeader("uuid") ?: "",
                code = ResponseEnum.VALIDATION_ERROR.CODE,
                message = e.constraintViolations.last().message,
                path = request.servletPath,
                data = null
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        request: HttpServletRequest,
        response: HttpServletResponse,
        e: MethodArgumentNotValidException): ApiResult<String> {
        val bindingResult = e.bindingResult
        val strBuilder = StringBuilder()
        var isFirst = true
        bindingResult.fieldErrors.forEach { fieldError ->
            if(!isFirst){
                strBuilder.append(" , ")
            }
            strBuilder.append("${fieldError.defaultMessage}")
            isFirst = false
        }
        response.status = HttpStatus.BAD_REQUEST.value()
        return ApiResult(
            uniqueCode = request.getHeader("uuid") ?: "",
            code = ResponseEnum.VALIDATION_ERROR.CODE,
            message = strBuilder.toString(),
            path = request.servletPath,
            data = null
        )
    }

    @ExceptionHandler(ConstraintDefinitionException::class)
    fun handleConstraintDefinitionException(request: HttpServletRequest,
                                              response: HttpServletResponse,
                                              e: ConstraintDefinitionException): ApiResult<String> {
        response.status = HttpStatus.BAD_REQUEST.value()
        return ApiResult(
                uniqueCode = request.getHeader("uuid") ?: "",
                code = ResponseEnum.VALIDATION_ERROR.CODE,
                message = "Validation Error",
                path = request.servletPath,
                data = null
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(request: HttpServletRequest,
                                              response: HttpServletResponse,
                                              e: HttpMessageNotReadableException): ApiResult<String>
    {

        logger.error("handleHttpMessageNotReadableException : ${e.printStackTrace()}")
        logger.error("handleHttpMessageNotReadableException : ${e.localizedMessage}")
        response.status = HttpStatus.BAD_REQUEST.value()
        return ApiResult(
            uniqueCode = request.getHeader("uuid") ?: "",
            code = ResponseEnum.VALIDATION_ERROR.CODE,
            path = request.servletPath,
            data = null
        )
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException::class)
    fun handleAccessDeniedException(request: HttpServletRequest,
                                    response: HttpServletResponse,
                                    e: org.springframework.security.access.AccessDeniedException): ApiResult<String>
    {
        response.status = HttpStatus.UNAUTHORIZED.value()
        return ApiResult(
                uniqueCode = request.getHeader("uuid") ?: "",
                code = ResponseEnum.UNAUTHORIZED_ACCESS_ERROR.CODE,
                message = ResponseEnum.UNAUTHORIZED_ACCESS_ERROR.MSG,
                path = request.servletPath,
                data = null
        )
    }

    @ExceptionHandler(Exception::class)
    fun exceptionErrorHandler(request: HttpServletRequest,
                              response: HttpServletResponse,
                              exception: java.lang.Exception): Any?{

        val apiResult: ApiResult<Any?> = ApiResult()
        logger.error("Exception : $exception")
        logger.error("request info : $request.")

        response.status = HttpStatus.BAD_REQUEST.value()
        apiResult.uniqueCode = request.getHeader("uuid") ?: ""
        apiResult.code = ResponseEnum.UNKNOWN_ERROR.CODE
        apiResult.message = exception.message ?: ""
        apiResult.path = request.servletPath
        apiResult.data = null
        logger.debug("OUT : $apiResult")

        return apiResult
    }

}