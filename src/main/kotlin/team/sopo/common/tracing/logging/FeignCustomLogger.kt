package team.sopo.common.tracing.logging

import feign.Logger
import feign.Request
import feign.Response
import feign.Util
import feign.Util.UTF_8
import feign.Util.decodeOrDefault
import org.apache.logging.log4j.LogManager
import team.sopo.common.tracing.DeliveryTrackerRepository
import team.sopo.parcel.infrastructure.DeliveryTrackerClient
import java.io.IOException


class FeignCustomLogger(private val repository: DeliveryTrackerRepository): Logger(){

    private val logger = LogManager.getLogger(FeignCustomLogger::class)

    override fun log(configKey: String, format: String, vararg args: Any) {}

    override fun logRequest(configKey: String, logLevel: Level, request: Request) {
        if(isDeliveryClient(configKey))
            repository.saveRequest(request.url(), request.httpMethod().toString())
        super.logRequest(configKey, logLevel, request)
    }

    override fun logIOException(
        configKey: String,
        logLevel: Level,
        ioe: IOException,
        elapsedTime: Long
    ): IOException {
        if(isDeliveryClient(configKey))
            repository.saveError(ioe::class.simpleName.toString(), ioe.message, elapsedTime)


        return super.logIOException(configKey, logLevel, ioe, elapsedTime)
    }
    override fun logAndRebufferResponse(
        configKey: String,
        logLevel: Level,
        response: Response?,
        elapsedTime: Long
    ): Response? {
        if(response != null && isDeliveryClient(configKey)){
            val bodyData = Util.toByteArray(response.body().asInputStream())
            repository.saveResponse(elapsedTime, response.status(), getResponseBodyString(bodyData))

            return response.toBuilder().body(bodyData).build()
        }
        return response
    }

    private fun getResponseBodyString(bodyData: ByteArray): String {
        return decodeOrDefault(bodyData, UTF_8, "-")
    }

    private fun isDeliveryClient(configKey: String): Boolean {
        return getClientName(configKey) == DeliveryTrackerClient::class.simpleName
    }

    private fun getClientName(configKey: String): String{
        return configKey.split("#")[0]
    }
}