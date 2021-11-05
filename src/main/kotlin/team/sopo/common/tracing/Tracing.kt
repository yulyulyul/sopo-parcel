package team.sopo.common.tracing

import com.google.gson.JsonObject
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager

abstract class Tracing(private val tracingEvent: TracingEvent) {
    private val logger = LogManager.getLogger(Tracing::class.java)

    abstract fun getTraceContent(): JsonObject

    fun trace(){
        logger.info(MarkerManager.getMarker(tracingEvent.name), getTraceContent().toString())
    }
}