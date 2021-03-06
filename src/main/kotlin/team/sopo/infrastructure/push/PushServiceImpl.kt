package team.sopo.infrastructure.push

import org.springframework.stereotype.Service
import team.sopo.domain.push.PushInfo
import team.sopo.domain.push.PushService

@Service
class PushServiceImpl(private val userClient: UserClient, private val producer: KafkaProducer) : PushService {
    override fun pushCompleteParcels(userToken: String, parcelIds: List<Long>) {
        val pushToken = userClient.getPushToken(userToken).pushToken
        producer.sendMsg(PushInfo.ParcelUpdateCompleteMessage(pushToken, parcelIds))
    }

    override fun pushAwakenDevice(topic: String) {
        producer.sendMsg(PushInfo.DeviceAwakenMessage(topic))
    }
}