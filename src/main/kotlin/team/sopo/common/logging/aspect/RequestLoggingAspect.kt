package team.sopo.common.logging.aspect

import com.fasterxml.jackson.databind.ObjectMapper
import team.sopo.common.extension.toString
import net.minidev.json.JSONObject
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.MarkerManager
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.collections.HashMap

@Component
@Aspect
class RequestLoggingAspect {

    private val logger: Logger = LogManager.getLogger(this.javaClass)
    val objectMapper = ObjectMapper()

    @Pointcut("execution(* com.parcelpj..*Controller.*(..)) ")
    fun onRequest() {}

    @Around("onRequest()")
    @Throws(Throwable::class)
    fun methodLogger(proceedingJoinPoint: ProceedingJoinPoint): Any? {

        try {
            val request =
                (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?)!!.request // request 정보를 가져온다.
            val controllerName = proceedingJoinPoint.signature.declaringType.simpleName
            val methodName = proceedingJoinPoint.signature.name
            val user = try {
                            request.userPrincipal.name
                        }
                        catch (e: java.lang.IllegalStateException){
                            ""
                        }
            val params: MutableMap<String, Any> = HashMap()

            params["controller"] = controllerName
            params["method"] = methodName
            params["params"] = getParams(request)
            params["log_time"] = Date().toString("yyyy/MM/dd HH:mm:ss")
            params["request_uri"] = request.requestURI
            params["http_method"] = request.method
            params["user"]=user

            val requestString = objectMapper.writeValueAsString(params)
//            val parsingType = objectMapper.writeValueAsString("request")

            logger.info(MarkerManager.getMarker("request"), requestString)
            return proceedingJoinPoint.proceed()
        } catch (throwable: Throwable) {
            logger.error("logger aop: $throwable")
            throw throwable
        }
    }

    /**
     * request 에 담긴 정보를 JSONObject 형태로 반환한다.
     * @param request
     * @return
     */
    private fun getParams(request: HttpServletRequest): JSONObject {
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