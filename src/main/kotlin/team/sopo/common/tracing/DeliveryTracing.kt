package team.sopo.common.tracing

import com.google.gson.Gson
import com.google.gson.JsonObject
import team.sopo.common.tracing.content.DeliveryTrackerContent

class DeliveryTracing(private val content: DeliveryTrackerContent): Tracing(TracingEvent.DELIVERY) {
    override fun getTraceContent(): JsonObject {
        return Gson().toJsonTree(content).asJsonObject
    }
}