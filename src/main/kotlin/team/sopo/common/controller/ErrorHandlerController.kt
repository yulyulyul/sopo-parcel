package team.sopo.common.controller

import com.google.gson.Gson
import team.sopo.common.enums.ResponseEnum
import team.sopo.common.model.api.ApiResult
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.PrintWriter
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class ErrorHandlerController: ErrorController {

    var logger: Logger = LogManager.getLogger(this.javaClass.name)

    @RequestMapping("/error")
    @Throws(Throwable::class)
    fun handleError(request: HttpServletRequest,
                    response: HttpServletResponse?) {
        if (request.getAttribute("javax.servlet.error.exception") != null) {

            throw (request.getAttribute("javax.servlet.error.exception") as Throwable)
        }

        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)
        val out: PrintWriter? = response?.writer
        val uuid = request.getHeader("uuid") ?: ""
        val path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI) as String

        if(status != null){

            when (status.toString().toInt()) {
                HttpStatus.NOT_FOUND.value() -> {
                    ApiResult(
                            code = ResponseEnum.NOT_FOUND_ERROR.CODE,
                            message = ResponseEnum.NOT_FOUND_ERROR.MSG,
                            uniqueCode = uuid,
                            path = path,
                            data = null
                    ).run {
                        Gson().toJson(this)
                    }.apply {
                        out?.print(this)
                    }
                }
                HttpStatus.FORBIDDEN.value() -> {
                    ApiResult(
                            code = ResponseEnum.FORBIDDEN_ACCESS_ERROR.CODE,
                            message = ResponseEnum.FORBIDDEN_ACCESS_ERROR.MSG,
                            uniqueCode = uuid,
                            path = path,
                            data = null
                        ).run {
                            Gson().toJson(this)
                        }.apply {
                        out?.print(this)
                    }
                }
                else -> {
                    ApiResult(
                            code = ResponseEnum.UNKNOWN_ERROR.CODE,
                            message = ResponseEnum.UNKNOWN_ERROR.MSG,
                            uniqueCode = request.getHeader("uuid"),
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
    }
}