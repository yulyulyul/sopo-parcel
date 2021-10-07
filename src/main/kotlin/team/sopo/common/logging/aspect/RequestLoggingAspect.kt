package team.sopo.common.logging.aspect

import com.google.gson.Gson
import net.minidev.json.JSONObject
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.CodeSignature
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import team.sopo.common.extension.toString
import team.sopo.common.tracing.ApiTracing
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*
import java.util.stream.Stream
import javax.servlet.http.HttpServletRequest

@Component
@Aspect
class RequestLoggingAspect {
    private val logger: Logger = LogManager.getLogger(this.javaClass)

    @Pointcut("execution(* team.sopo..*Controller.*(..)) ")
    fun onRequest() {}

    @Around("onRequest()")
    @Throws(Throwable::class)
    fun logAction(joinPoint: ProceedingJoinPoint): Any? {

        var result: Any? = null
        try {
            val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?)!!.request // request 정보를 가져온다.

            val user = try {
                request.userPrincipal.name
            }
            catch (e: java.lang.IllegalStateException){
                ""
            }
            catch (e: NullPointerException){
                ""
            }

            val requestUrl = getRequestUrl(joinPoint)
            val httpMethod = request.method
            val requestMap: MutableMap<String, Any?> = HashMap()
            requestMap["controller"] = joinPoint.signature.declaringType.simpleName
            requestMap["method"] = joinPoint.signature.name
            requestMap["body"] = getRequestBody(request).toString()
            requestMap["request_time"] = Date().toString("yyyy/MM/dd HH:mm:ss")
            requestMap["request_uri"] = requestUrl
            requestMap["http_method"] = httpMethod
            requestMap["user"]= user
            requestMap["params"] = Gson().toJsonTree(params(joinPoint)).asJsonObject.toString()

            logger.info("[REQUEST] ==> request_uri : $requestUrl, http_method : $httpMethod, user : $user")
            ApiTracing(requestMap).trace()

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
            value = annotationClass.getMethod("value").invoke(annotation) as Array<String>
        } catch (e: IllegalAccessException) {
            return null
        } catch (e: NoSuchMethodException) {
            return null
        } catch (e: InvocationTargetException) {
            return null
        }
        return String.format("%s%s", baseUrl, if (value.isNotEmpty()) value[0] else "")
    }

    private fun params(joinPoint: JoinPoint): Map<String, Any> {
        val codeSignature = joinPoint.signature as CodeSignature
        val parameterNames = codeSignature.parameterNames
        val args = joinPoint.args
        val params: MutableMap<String, Any> = HashMap()
        for (i in parameterNames.indices) {
            if(!parameterNames[i].equals("principal")){
                params[parameterNames[i]] = args[i]
            }
        }
        return params
    }

    /**
     * request 에 담긴 정보를 JSONObject 형태로 반환한다.
     * @param request
     * @return
     */
    private fun getRequestBody(request: HttpServletRequest): JSONObject {
        val jsonObject = JSONObject()
        val params = request.parameterNames

        while (params.hasMoreElements()) {
            val param = params.nextElement()
            val replaceParam = param.replace("\\.".toRegex(), "-")

            jsonObject[replaceParam] = request.getParameter(param)
        }
        return jsonObject
    }
}