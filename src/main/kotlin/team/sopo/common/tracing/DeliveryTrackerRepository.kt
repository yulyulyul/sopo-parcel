package team.sopo.common.tracing

import org.springframework.stereotype.Repository
import org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST
import org.springframework.web.context.request.RequestContextHolder
import team.sopo.common.tracing.content.DeliveryTrackerContent
import team.sopo.parcel.domain.Carrier
import java.lang.Exception

@Repository
class DeliveryTrackerRepository: TracingRepository<DeliveryTrackerContent> {

    companion object{
        private const val DELIVERY_TRACKER_CONTENT = "deliveryTrackerContent"
    }

    fun saveUser(user: String){
        val content = get().apply {
            this.user = user
        }
        save(content)
    }

    fun saveQueries(carrier: Carrier, waybillNum: String){
        val content = get().apply {
            this.carrier = carrier
            this.waybillNum = waybillNum
        }
        save(content)
    }

    fun saveSignature(client_name: String, method: String){
        val content = get().apply {
            this.client_name = client_name
            this.method = method
        }
        save(content)
    }

    fun saveError(exception: String, exception_message: String?, elapsedTime: Long){
        val content = get().apply{
            this.exception = exception
            this.exception_message = exception_message
            this.elapsedTime = elapsedTime
        }
        save(content)
    }

    fun saveRequest(request_url: String, http_method: String){
        val content = get().apply {
            this.request_url = request_url
            this.http_method = http_method
        }
        save(content)
    }

    fun saveResponse(elapsedTime: Long, http_status: Int, return_message: String){
        val content = get().apply {
            this.elapsedTime = elapsedTime
            this.http_status = http_status
            this.return_message = return_message
        }
        save(content)
    }

    override fun getContent(): DeliveryTrackerContent {
        return get()
    }

    private fun get(): DeliveryTrackerContent{
        val attribute = RequestContextHolder.getRequestAttributes()?.getAttribute(DELIVERY_TRACKER_CONTENT, SCOPE_REQUEST)
        if(attribute == null){
            save(DeliveryTrackerContent())
            return get()
        }
        if(attribute is DeliveryTrackerContent){
            return attribute
        }
        throw IllegalStateException()
    }

    override fun save(content: DeliveryTrackerContent){
        RequestContextHolder.getRequestAttributes()?.setAttribute(DELIVERY_TRACKER_CONTENT, content, SCOPE_REQUEST)
    }
}