package team.sopo.common.tracing

import com.google.gson.Gson
import com.google.gson.JsonObject

class ApiTracing(private val content: ApiTracingContent): Tracing(TracingEvent.API) {
    override fun getTraceContent(): JsonObject {
        checkElasticId()
        return Gson().toJsonTree(content).asJsonObject
    }
}