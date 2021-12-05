package team.sopo.push.infrastructure

import org.springframework.stereotype.Service
import team.sopo.common.exception.SystemException
import team.sopo.push.domain.PushInfo
import team.sopo.push.domain.PushService

@Service
class PushServiceImpl(private val userClient: UserClient, private val producer: KafkaProducer) : PushService {
    override fun pushCompleteParcels(userId: String, parcelIds: List<Long>) {
        val pushToken = userClient.getFcmToken(userId).data?.pushToken ?: throw SystemException("푸쉬 토큰을 가져오는데 실패하였습니다.")
        producer.sendMsg(PushInfo.ParcelUpdateCompleteMessage(pushToken, parcelIds))
    }
}