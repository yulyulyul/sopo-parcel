package team.sopo.common.infra.message

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class MessagingService {
    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>

    fun sendMessage(topic: String, msg: String){
        kafkaTemplate.send(topic, msg)
    }
}