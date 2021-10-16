package team.sopo.common.tracing.logging.filter

import com.google.gson.Gson
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.util.StringUtils
import org.springframework.web.filter.CommonsRequestLoggingFilter
import team.sopo.common.tracing.ApiTracing
import team.sopo.common.tracing.ApiTracingRepository
import java.util.*
import javax.servlet.http.HttpServletRequest

class SopoRequestLoggingFilter(private val apiTracingRepository: ApiTracingRepository): CommonsRequestLoggingFilter() {

    override fun shouldLog(request: HttpServletRequest): Boolean {
        return logger.isDebugEnabled
    }

    override fun beforeRequest(request: HttpServletRequest, message: String) {
        return logger.debug(message)
    }

    override fun afterRequest(request: HttpServletRequest, message: String) {

        ApiTracing(content = apiTracingRepository.getContent()).trace()
        return logger.debug(message)
    }

    private fun queryStringToMap(queryString: String): Map<String, String>{
        val map = mutableMapOf<String, String>()

        if(queryString.contains("&")) {

            val tokenizer1 = StringTokenizer(queryString, "&")
            while (tokenizer1.hasMoreTokens()) {
                val tokenizer2 = StringTokenizer(tokenizer1.nextToken(), "=")
                map[tokenizer2.nextToken()] = tokenizer2.nextToken()
            }
        }
        else{
            val tokenizer1 = StringTokenizer(queryString, "=")
            map[tokenizer1.nextToken()] = tokenizer1.nextToken()
        }

        return map
    }

    override fun createMessage(request: HttpServletRequest, prefix: String, suffix: String): String {

        var parameter = ""
        var payload = ""
        var httpMethod = ""
        var user = ""


        val msg = StringBuilder()
        httpMethod = request.method
        msg.append(prefix)
        msg.append(httpMethod).append(' ')
        msg.append(request.requestURI)


        if (isIncludeQueryString) {
            val queryString = request.queryString
            if (queryString != null) {
                msg.append('?').append(queryString)
                parameter = Gson().toJsonTree(queryStringToMap(queryString)).asJsonObject.toString()
            }
        }

        if (isIncludeClientInfo) {
            val client = request.remoteAddr
            if (StringUtils.hasLength(client)) {
                msg.append(", client=").append(client)
            }
            val session = request.getSession(false)
            if (session != null) {
                msg.append(", session=").append(session.id)
            }
            val remoteUser = request.remoteUser
            if (remoteUser != null) {
                msg.append(", user=").append(remoteUser)
                user = remoteUser
            }
        }

        if (isIncludeHeaders) {
            val headers = ServletServerHttpRequest(request).headers
            if (headerPredicate != null) {
                val names = request.headerNames
                while (names.hasMoreElements()) {
                    val header = names.nextElement()
                    if (!headerPredicate!!.test(header)) {
                        headers[header] = "masked"
                    }
                }
            }
            msg.append(", headers=").append(headers)
        }

        if (isIncludePayload) {
            val messagePayload = getMessagePayload(request)
            if (messagePayload != null) {
                msg.append(", payload=").append(messagePayload)
                payload = messagePayload
            }
        }
        apiTracingRepository.saveRequestInfo(request.requestURI, parameter, payload, httpMethod, user)

        msg.append(suffix)
        return msg.toString()
    }
}