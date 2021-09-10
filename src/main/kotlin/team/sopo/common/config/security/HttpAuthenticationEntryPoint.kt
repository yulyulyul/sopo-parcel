package team.sopo.common.config.security

import com.google.gson.Gson
import team.sopo.common.enums.ResponseEnum
import team.sopo.common.exception.CustomOauthException
import team.sopo.common.model.api.ApiResult
import org.apache.http.HttpStatus
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import java.io.PrintWriter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class HttpAuthenticationEntryPoint: AuthenticationEntryPoint {

    val logger: Logger = LogManager.getLogger(this.javaClass.name)

    override fun commence(request: HttpServletRequest?, response: HttpServletResponse?, authException: AuthenticationException?) {

        val out: PrintWriter? = response?.writer
        val uuid = request?.getHeader("uuid") ?: ""
        val path = request?.servletPath.orEmpty()


        if(authException?.cause is CustomOauthException){
            val customOauthException = authException.cause as CustomOauthException

            val apiResult = ApiResult(
                code = customOauthException.responseEnum.CODE,
                message = customOauthException.oauthErrMsg,
                uniqueCode = uuid,
                path = path,
                data = customOauthException.additionalData ?: ""
            )
            val apiResultJson = Gson().toJson(apiResult)
            logger.debug("@@ -> apiResultJson : $apiResultJson")
            logger.debug("@@ -> apiResult : $apiResult")

            response?.status = HttpStatus.SC_UNAUTHORIZED
            out?.println(apiResultJson)
        }
        else{
            ApiResult(
                code = ResponseEnum.UNAUTHORIZED_ACCESS_ERROR.CODE,
                message = authException?.cause?.message.orEmpty(),
                uniqueCode = uuid,
                path = path,
                data = null
            ).run {
                Gson().toJson(this)
            }.apply {
                out?.print(this)
            }
        }
    }
}