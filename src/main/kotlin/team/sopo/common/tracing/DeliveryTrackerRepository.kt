package team.sopo.common.tracing

import feign.Request
import org.springframework.stereotype.Repository
import org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST
import org.springframework.web.context.request.RequestContextHolder
import team.sopo.common.tracing.content.DeliveryTrackerContent
import team.sopo.domain.parcel.ParcelCommand

@Repository
class DeliveryTrackerRepository : TracingRepository<Map<String, DeliveryTrackerContent>> {

    companion object {
        private const val DELIVERY_TRACKER_CONTENT = "deliveryTrackerContent"
    }

    fun saveTrackingPersonalData(req: ParcelCommand.TrackingPersonalData) {
        val content = get().apply {
            this.getOrPut(req.apiId) {
                DeliveryTrackerContent(
                    api_id = req.apiId,
                    user = req.userId,
                    carrier = req.carrier,
                    waybillNum = req.waybillNum
                )
            }
        }
        save(content)
    }

    fun saveRequestInfo(apiId: String, requestUrl: String, httpMethod: Request.HttpMethod) {
        val content = getContentByApiId(apiId)
        content.updateRequestInfo(requestUrl, httpMethod)
        replaceContentByApiId(apiId, content)
    }

    fun saveResponseInfo(apiId: String, elapsedTime: Long, httpStatus: Int, returnMessage: String) {
        val content = getContentByApiId(apiId)
        content.updateResponseInfo(elapsedTime, httpStatus, returnMessage)
        replaceContentByApiId(apiId, content)
    }

    fun saveError(apiId: String, exception: String, exception_message: String?) {

        val content = getContentByApiId(apiId)
        content.updateErrorInfo(exception, exception_message)
        replaceContentByApiId(apiId, content)
    }

    private fun replaceContentByApiId(apiId: String, content: DeliveryTrackerContent) {
        val map = get().apply { replace(apiId, content) }
        save(map)
    }

    fun getContentByApiId(apiId: String): DeliveryTrackerContent {
        return getContent()[apiId] ?: throw NullPointerException("api-id[$apiId]에 해당하는 요청정보가 존재하지 않습니다.")
    }

    override fun getContent(): Map<String, DeliveryTrackerContent> {
        return get()
    }

    private fun get(): MutableMap<String, DeliveryTrackerContent> {
        val attribute =
            RequestContextHolder.getRequestAttributes()?.getAttribute(DELIVERY_TRACKER_CONTENT, SCOPE_REQUEST)
        if (attribute == null) {
            val contentPerApiId = mutableMapOf<String, DeliveryTrackerContent>()
            save(contentPerApiId)
            return get()
        }

        return attribute as MutableMap<String, DeliveryTrackerContent>
    }

    override fun save(content: Map<String, DeliveryTrackerContent>) {
        RequestContextHolder.getRequestAttributes()?.setAttribute(DELIVERY_TRACKER_CONTENT, content, SCOPE_REQUEST)
    }
}