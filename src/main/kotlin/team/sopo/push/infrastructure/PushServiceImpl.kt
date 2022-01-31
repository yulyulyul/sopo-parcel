package team.sopo.push.infrastructure

import org.springframework.stereotype.Service
import team.sopo.push.domain.PushInfo
import team.sopo.push.domain.PushService

@Service
class PushServiceImpl(private val userClient: UserClient, private val producer: KafkaProducer) : PushService {
    override fun pushCompleteParcels(userId: Long, parcelIds: List<Long>) {
        val pushToken = userClient.getPushToken(userId).pushToken
        producer.sendMsg(PushInfo.ParcelUpdateCompleteMessage(pushToken, parcelIds))
    }

    override fun pushAwakenDevice(topic: String) {
        producer.sendMsg(PushInfo.DeviceAwakenMessage(topic))
    }
}