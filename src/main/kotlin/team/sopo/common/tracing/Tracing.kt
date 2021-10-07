package team.sopo.common.tracing

import brave.baggage.BaggageField
import com.fasterxml.uuid.EthernetAddress
import com.fasterxml.uuid.Generators
import com.google.gson.JsonObject
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager

abstract class Tracing(private val tracingEvent: TracingEvent) {
    private val logger = LogManager.getLogger(Tracing::class.java)

    abstract fun getTraceContent(): JsonObject

    fun trace(){
        logger.info(MarkerManager.getMarker(tracingEvent.name), getTraceContent().toString())
    }


    protected fun checkElasticId(){
        val elasticId = BeanUtils.getBean("elasticId") as BaggageField
        if(elasticId.value == null){
            elasticId.updateValue(generateElasticId().toString())
        }
    }

    private fun generateElasticId(): java.util.UUID {
        return Generators.timeBasedGenerator(EthernetAddress.fromInterface()).generate()
    }
}