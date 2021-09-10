package team.sopo.common.config.oauth2

import com.google.gson.Gson
import team.sopo.common.enums.ResponseEnum
import team.sopo.common.model.api.ApiResult
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import java.io.PrintWriter
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class Oauth2AccessDeniedHandler: AccessDeniedHandler {

    val logger: Logger = LogManager.getLogger(this.javaClass.name)

    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        accessDeniedException: AccessDeniedException?
    ) {
        logger.debug("@@ -> Oauth2AccessDeniedHandler")

        val out: PrintWriter? = response?.writer
        val uuid = request?.getHeader("uuid") ?: ""
        val path = request?.getAttribute(RequestDispatcher.ERROR_REQUEST_URI) as String ?: ""

        ApiResult(
            code = ResponseEnum.UNAUTHORIZED_ACCESS_ERROR.CODE,
            message = accessDeniedException?.message ?: "Oauth2AccessDeniedHandler",
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