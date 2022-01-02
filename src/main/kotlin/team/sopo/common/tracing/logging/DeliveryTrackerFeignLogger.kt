package team.sopo.common.tracing.logging

import feign.Logger
import feign.Request
import feign.Response
import feign.Util
import feign.Util.UTF_8
import feign.Util.decodeOrDefault
import org.apache.logging.log4j.LogManager
import team.sopo.common.tracing.DeliveryTracing
import team.sopo.common.tracing.DeliveryTrackerRepository
import java.io.IOException


class DeliveryTrackerFeignLogger(private val repository: DeliveryTrackerRepository) : Logger() {

    private val logger = LogManager.getLogger(DeliveryTrackerFeignLogger::class)

    override fun log(configKey: String, format: String, vararg args: Any) {
        val data = String.format(methodTag(configKey) + format, *args)
        logger.info(data)
    }

    override fun logRequest(configKey: String, logLevel: Level, request: Request) {
        repository.saveRequestInfo(getApiId(request), request.url(), request.httpMethod())
        super.logRequest(configKey, logLevel, request)
    }

    override fun logIOException(
        configKey: String,
        logLevel: Level,
        ioe: IOException,
        elapsedTime: Long
    ): IOException {
        return super.logIOException(configKey, logLevel, ioe, elapsedTime)
    }

    override fun logAndRebufferResponse(
        configKey: String,
        logLevel: Level,
        response: Response,
        elapsedTime: Long
    ): Response {
        val reason = if (response.reason() != null && logLevel > Level.NONE) " " + response.reason() else ""
        val status = response.status()
        log(configKey, "<--- HTTP/1.1 %s%s (%sms)", status, reason, elapsedTime)
        if (logLevel.ordinal >= Level.HEADERS.ordinal) {
            for (field in response.headers().keys) {
                for (value in Util.valuesOrEmpty(response.headers(), field)) {
                    log(configKey, "%s: %s", field!!, value!!)
                }
            }
            var bodyLength = 0
            if (response.body() != null && !(status == 204 || status == 205)) {
                // HTTP 204 No Content "...response MUST NOT include a message-body"
                // HTTP 205 Reset Content "...response MUST NOT include an entity"
                if (logLevel.ordinal >= Level.FULL.ordinal) {
                    log(configKey, "") // CRLF
                }
                val apiId = getApiId(response)
                val bodyData = Util.toByteArray(response.body().asInputStream())
                repository.saveResponseInfo(apiId, elapsedTime, response.status(), getResponseBodyString(bodyData))
                logContent(apiId)

                bodyLength = bodyData.size
                if (logLevel.ordinal >= Level.FULL.ordinal && bodyLength > 0) {
                    log(configKey, "%s", decodeOrDefault(bodyData, UTF_8, "Binary data"))
                }
                log(configKey, "<--- END HTTP (%s-byte body)", bodyLength)
                return response.toBuilder().body(bodyData).build()
            } else {
                log(configKey, "<--- END HTTP (%s-byte body)", bodyLength)
            }
        }
        return response
    }

    private fun logContent(apiId: String) {
        val content = repository.getContentByApiId(apiId)
        DeliveryTracing(content).trace()
    }

    private fun getApiId(request: Request): String {
        return request.headers()["api-id"]?.first() ?: throw IllegalStateException("api-id 헤더 정보가 존재하지 않습니다.")
    }

    private fun getApiId(response: Response): String {
        return response.request().headers()["api-id"]?.first()
            ?: throw IllegalStateException("api-id 헤더 정보가 존재하지 않습니다.")
    }

    private fun getResponseBodyString(bodyData: ByteArray): String {
        return decodeOrDefault(bodyData, UTF_8, "-")
    }
}