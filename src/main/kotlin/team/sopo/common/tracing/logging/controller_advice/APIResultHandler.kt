package team.sopo.common.tracing.logging.controller_advice

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import team.sopo.common.exception.error.Error
import team.sopo.common.exception.error.Errors
import team.sopo.common.model.api.ApiResult
import team.sopo.common.tracing.ApiTracingRepository

@ControllerAdvice
class APIResultHandler(private val tracingRepository: ApiTracingRepository) : ResponseBodyAdvice<Any> {

    private val logger: Logger = LogManager.getLogger(APIResultHandler::class)

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        return true
    }

    override fun beforeBodyWrite(body: Any?, returnType: MethodParameter, selectedContentType: MediaType,
                                 selectedConverterType: Class<out HttpMessageConverter<*>>, request: ServerHttpRequest,
                                 response: ServerHttpResponse): Any? {

        val servletRequestAttributes = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?)!!
        val httpStatus = servletRequestAttributes.response?.status

//        if (body != null) {
//            tracingRepository.saveReturnMessage(body.toString())
//        }
        if(body is ApiResult<*>){
            body.path = servletRequestAttributes.request.requestURI
        }
//        if(body is Errors && body.errors.isNotEmpty()){
//            val error = body.errors.first()
//            tracingRepository.saveErrorInfo(error.code, error.type)
//        }
        tracingRepository.saveHttpStatus(httpStatus)

        return body
    }
}
