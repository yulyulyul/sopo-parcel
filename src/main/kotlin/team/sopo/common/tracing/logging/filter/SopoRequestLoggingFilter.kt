package team.sopo.common.tracing.logging.filter

import com.google.gson.Gson
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.web.filter.CommonsRequestLoggingFilter
import team.sopo.common.tracing.ApiTracing
import team.sopo.common.tracing.ApiTracingRepository
import java.util.*
import javax.servlet.http.HttpServletRequest

class SopoRequestLoggingFilter(private val apiTracingRepository: ApiTracingRepository): CommonsRequestLoggingFilter() {

    companion object{
        private const val REQUEST_URL = "request_url"
        private const val PARAMETER = "parameter"
        private const val PAYLOAD = "payload"
        private const val HTTP_METHOD = "http_method"
        private const val USER = "user"

    }

    override fun shouldLog(request: HttpServletRequest): Boolean {
        return logger.isDebugEnabled
    }

    override fun beforeRequest(request: HttpServletRequest, message: String) {
        return logger.debug(message)
    }

    override fun afterRequest(request: HttpServletRequest, message: String) {

        val requestInfo: Map<String, String> = Gson().fromJson(message, object : com.google.gson.reflect.TypeToken<Map<String, String>>() {}.type)
        apiTracingRepository.saveRequestInfo(
            requestInfo.getOrDefault(REQUEST_URL, ""),
            requestInfo.getOrDefault(PARAMETER, ""),
            requestInfo.getOrDefault(PAYLOAD, ""),
            requestInfo.getOrDefault(HTTP_METHOD, ""),
            requestInfo.getOrDefault(USER, "")
        )

        val content = apiTracingRepository.getContent()
        if(content.mapping_url.isNotBlank()){
            ApiTracing(content = content).trace()
        }

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

    private fun getRequestUrl(url: String, queryString: String?): String{
        var requestUrl = url
        if (isIncludeQueryString) {
            if (queryString != null) {
                requestUrl += "?$queryString"
            }
        }
        return requestUrl
    }

    override fun createMessage(request: HttpServletRequest, prefix: String, suffix: String): String {

        val httpMethod = request.method
        val map = mutableMapOf<String, String>()
        map[HTTP_METHOD] = httpMethod

        if (isIncludeQueryString) {
            val queryString = request.queryString
            if (queryString != null) {
                map[PARAMETER] =  Gson().toJsonTree(queryStringToMap(queryString)).asJsonObject.toString()
            }
        }
        map[REQUEST_URL] = getRequestUrl(request.requestURI, request.queryString)

        if (isIncludeClientInfo) {
            val remoteUser = request.remoteUser
            if (remoteUser != null) {
                map[USER] = remoteUser
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
        }

        if (isIncludePayload) {
            val messagePayload = getMessagePayload(request)
            if (messagePayload != null) {
                map[PAYLOAD] = messagePayload
            }
        }
        return  Gson().toJsonTree(map).asJsonObject.toString()
    }
}