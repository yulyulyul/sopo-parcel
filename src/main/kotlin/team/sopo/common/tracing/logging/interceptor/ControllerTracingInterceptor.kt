package team.sopo.common.tracing.logging.interceptor

import org.apache.logging.log4j.LogManager
import org.springframework.web.bind.annotation.*
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import team.sopo.common.tracing.ApiTracingRepository
import java.lang.reflect.Method
import java.util.stream.Stream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ControllerTracingInterceptor(private val apiTracingRepository: ApiTracingRepository): HandlerInterceptor {

    private val logger = LogManager.getLogger(ControllerTracingInterceptor::class.java)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if(handler is HandlerMethod){
            val handlerMethod: HandlerMethod = handler
            val clazz = handlerMethod.beanType
            val requestUrl = getRequestUrl(clazz, handlerMethod.method)
            val controller = clazz.simpleName
            val method = handlerMethod.method.name

            apiTracingRepository.saveControllerInfo(controller, method, requestUrl ?: "")
        }

        return super.preHandle(request, response, handler)
    }

    private fun getRequestUrl(clazz: Class<*>, method: Method): String?{
        val requestMapping = clazz.getAnnotation(RequestMapping::class.java) ?: return null
        if(requestMapping.value.isEmpty())
            return null

        val baseUrl= requestMapping.value.first()

        return Stream.of(
            GetMapping::class.java, PutMapping::class.java, PostMapping::class.java,
            PatchMapping::class.java, DeleteMapping::class.java, RequestMapping::class.java
        )
            .filter{ mappingClass -> method.isAnnotationPresent(mappingClass) }
            .map { mappingClass -> getMappingUrl(method, mappingClass, baseUrl) }
            .findFirst().orElse(null)
    }

    private fun getMappingUrl(method: Method, annotationClass: Class<out Annotation>, baseUrl: String): String? {
        val annotation: Annotation = method.getAnnotation(annotationClass)
        val value: Array<String>
        try {
            val invoke = annotationClass.getMethod("value").invoke(annotation)
            value = if(invoke is Array<*>){
                invoke
                    .filterIsInstance<String>()
                    .toTypedArray()
            }
            else{
                arrayOf()
            }
        } catch (e: Exception){
            return null
        }
        return String.format("%s%s", baseUrl, if (value.isNotEmpty()) value[0] else "")
    }
}