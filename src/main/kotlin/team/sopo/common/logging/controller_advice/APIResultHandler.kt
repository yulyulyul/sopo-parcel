package team.sopo.common.logging.controller_advice

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
import team.sopo.common.exception.error.ErrorResponse
import team.sopo.common.extension.toString
import team.sopo.common.model.api.ApiResult
import team.sopo.common.tracing.ApiTracing
import java.util.*

@ControllerAdvice
class APIResultHandler() : ResponseBodyAdvice<Any> {

    private val logger: Logger = LogManager.getLogger(APIResultHandler::class)

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        return true
    }

    override fun beforeBodyWrite(body: Any?, returnType: MethodParameter, selectedContentType: MediaType,
                                 selectedConverterType: Class<out HttpMessageConverter<*>>, request: ServerHttpRequest,
                                 response: ServerHttpResponse): Any? {

        val servletRequestAttributes = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?)!!
        val requestUri = servletRequestAttributes.request.requestURI
        val httpStatus = servletRequestAttributes.response?.status
        val params: MutableMap<String, Any?> = TreeMap()

        if (body != null) {
            params["return_message"] = body.toString()
        }
        if(body is ApiResult<*>){
            body.path = servletRequestAttributes.request.requestURI
        }
        if(body is ErrorResponse){
            params["error_code"] = body.code
            params["error_type"] = body.type
        }
        if(body is List<*> && body.isNotEmpty()){
            body.first()?.apply {
                if(this is ErrorResponse){
                    params["error_code"] = this.code
                    params["error_type"] = this.type
                }
            }
        }

        params["response_time"] = Date().toString("yyyy/MM/dd HH:mm:ss")
        params["http_status"] = httpStatus

        logger.info("[RESPONSE] <== request_uri : $requestUri, httpStatus : $httpStatus")
        ApiTracing(params).trace()

        return body
    }
}
