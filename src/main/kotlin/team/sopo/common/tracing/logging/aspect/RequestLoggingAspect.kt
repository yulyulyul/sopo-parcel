package team.sopo.common.tracing.logging.aspect

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import team.sopo.common.tracing.ApiTracingRepository
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.stream.Stream

@Component
@Aspect
class RequestLoggingAspect(private val apiTracingRepository: ApiTracingRepository) {
    private val logger: Logger = LogManager.getLogger(this.javaClass)

    @Pointcut("execution(* team.sopo..*Controller.*(..)) ")
    fun onRequest() {}

    @Around("onRequest()")
    @Throws(Throwable::class)
    fun logAction(joinPoint: ProceedingJoinPoint): Any? {

        try {
            val requestUrl = getRequestUrl(joinPoint)
            val controller = joinPoint.signature.declaringType.simpleName
            val method = joinPoint.signature.name

            apiTracingRepository.saveControllerInfo(controller, method, requestUrl ?: "")

            return joinPoint.proceed()
        }
        catch (throwable: Throwable) {
            logger.error("logger aop: $throwable")
            throw throwable
        }
    }

    private fun getRequestUrl(joinPoint: JoinPoint): String?{
        val clazz = joinPoint.target.javaClass
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method
        val requestMapping = clazz.getAnnotation(RequestMapping::class.java)
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