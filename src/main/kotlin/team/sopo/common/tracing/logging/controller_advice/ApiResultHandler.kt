package team.sopo.common.tracing.logging.controller_advice

import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import team.sopo.common.tracing.ApiTracingRepository

@ControllerAdvice
class ApiResultHandler(private val tracingRepository: ApiTracingRepository) : ResponseBodyAdvice<Any> {

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        return true
    }

    override fun beforeBodyWrite(
        body: Any?, returnType: MethodParameter, selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>, request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {

        val servletRequestAttributes = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?)!!
        val httpStatus = servletRequestAttributes.response?.status

//        if (body != null) {
//            tracingRepository.saveReturnMessage(body.toString())
//        }
//        if(body is Errors && body.errors.isNotEmpty()){
//            val error = body.errors.first()
//            tracingRepository.saveErrorInfo(error.code, error.type)
//        }
        tracingRepository.saveHttpStatus(httpStatus)

        return body
    }
}
