package team.sopo.common.tracing

import com.google.gson.Gson
import com.google.gson.JsonObject
import team.sopo.common.tracing.content.ApiTracingContent

class ApiTracing(private val content: ApiTracingContent): Tracing(TracingEvent.API) {
    override fun getTraceContent(): JsonObject {
        return Gson().toJsonTree(content).asJsonObject
    }
}