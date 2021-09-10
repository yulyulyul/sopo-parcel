package team.sopo.common.logging.controller_advice

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import team.sopo.common.extension.toString
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.MarkerManager

import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import java.util.*

@ControllerAdvice
class APIResultHandler() : ResponseBodyAdvice<Any> {

    var logger: Logger = LogManager.getLogger(this.javaClass)
    val objectMapper = ObjectMapper()
    val javaTimeModule = JavaTimeModule()

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        return true
    }

    override fun beforeBodyWrite(body: Any?, returnType: MethodParameter, selectedContentType: MediaType,
                                 selectedConverterType: Class<out HttpMessageConverter<*>>, request: ServerHttpRequest,
                                 response: ServerHttpResponse): Any? {

        val servletRequestAttributes = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?)!!
        val httpServletResponse = servletRequestAttributes.response
        val params: MutableMap<String, Any?> = TreeMap()
        val user = try {
            request.principal?.name ?: ""
        }
        catch (e: java.lang.IllegalStateException){
            ""
        }
        params["log_time"] = Date().toString("yyyy/MM/dd HH:mm:ss")
        params["http_status"] = httpServletResponse?.status
        params["http_method"] = servletRequestAttributes.request.method
        params["user"] = user
        params["request_uri"] = servletRequestAttributes.request.requestURI

        if (body != null) {
            params["return_message"] = body
        }

        val responseString = objectMapper.registerModule(javaTimeModule).writeValueAsString(params)

        logger.info(MarkerManager.getMarker("response"), responseString)

        return body
    }

}
