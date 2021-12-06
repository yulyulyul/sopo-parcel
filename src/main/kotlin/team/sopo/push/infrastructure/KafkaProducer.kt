package team.sopo.push.infrastructure

import com.google.gson.Gson
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import team.sopo.push.domain.PushInfo

@Component
class KafkaProducer(private val kafkaTemplate: KafkaTemplate<String, String>) {

    companion object{
        const val UPDATE_COMPLETE_MESSAGE = "updateComplete"
        const val DEVICE_AWAKEN_TOPIC = "deviceAwaken"
    }

    fun sendMsg(message: PushInfo.ParcelUpdateCompleteMessage){
        kafkaTemplate.send(UPDATE_COMPLETE_MESSAGE, Gson().toJson(message))
    }

    fun sendMsg(message: PushInfo.DeviceAwakenMessage){
        kafkaTemplate.send(DEVICE_AWAKEN_TOPIC, Gson().toJson(message))
    }
}